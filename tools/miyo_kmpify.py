#!/usr/bin/env python3
"""miyo_kmpify.py — MIYO-specific KMP hygiene (not MahmoudRH/kmpify).

MahmoudRH/kmpify targets Compose Multiplatform migrations.
MIYO stays on Android Views + XML + Hilt/Room in androidMain, with a
portable commonMain surface and thin iOS actuals/host.

This tool:
  1) Audits common expect declarations vs android/ios actuals
  2) Scans androidMain for portable candidates (no android/androidx/java/okhttp/hilt/room/work)
  3) Optionally moves a curated allowlist into commonMain after JVM-API patches

Usage:
  python3 tools/miyo_kmpify.py audit
  python3 tools/miyo_kmpify.py candidates
  python3 tools/miyo_kmpify.py expect-check
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SHARED = ROOT / "shared" / "src"
COMMON = SHARED / "commonMain" / "kotlin"
ANDROID = SHARED / "androidMain" / "kotlin"
IOS = SHARED / "iosMain" / "kotlin"

BAD_IMPORT = re.compile(
    r"^\s*import\s+("
    r"android\.|androidx\.|okhttp3\.|okhttp\.|dagger\.|javax\.inject|"
    r"com\.google\.android|com\.google\.dagger|"
    r"java\.|javax\.|org\.jsoup|org\.json|coil\.|moshi\.|"
    r"kotlinx\.coroutines\.android|com\.hannesdorfmann|"
    r"io\.reactivex|uy\.kohesive\.injekt|org\.acra|com\.squareup\.|"
    r"org\.koitharu"
    r")",
    re.M,
)

EXPECT_RE = re.compile(
    r"^\s*expect\s+(?:class|object|fun|val|interface|enum\s+class)\s+(\w+)",
    re.M,
)
ACTUAL_RE = re.compile(
    r"^\s*actual\s+(?:class|object|fun|val|interface|enum\s+class)\s+(\w+)",
    re.M,
)


def kt_files(base: Path) -> list[Path]:
    if not base.exists():
        return []
    return sorted(base.rglob("*.kt"))


def is_portable(text: str) -> bool:
    if BAD_IMPORT.search(text):
        return False
    if re.search(r"\bandroidx?\.", text):
        return False
    if re.search(r"\bjava\.(util|io|net|text|time|lang)\.", text):
        return False
    if re.search(
        r"@(Hilt|Inject|AndroidEntryPoint|Module|Dao|Entity|Database|Composable|Parcelize|Worker)\b",
        text,
    ):
        return False
    if re.search(r"\bSystem\.(currentTimeMillis|nanoTime|out|err)\b", text):
        return False
    if "String.format" in text or "javaClass" in text:
        return False
    return True


def cmd_expect_check() -> int:
    expects: dict[str, list[str]] = {}
    for p in kt_files(COMMON):
        text = p.read_text(encoding="utf-8", errors="replace")
        for m in EXPECT_RE.finditer(text):
            expects.setdefault(m.group(1), []).append(str(p.relative_to(COMMON)))

    android_actuals = set()
    ios_actuals = set()
    for p in kt_files(ANDROID):
        text = p.read_text(encoding="utf-8", errors="replace")
        android_actuals.update(ACTUAL_RE.findall(text))
    for p in kt_files(IOS):
        text = p.read_text(encoding="utf-8", errors="replace")
        ios_actuals.update(ACTUAL_RE.findall(text))

    missing = []
    for name in sorted(expects):
        a = name in android_actuals
        i = name in ios_actuals
        status = "OK" if a and i else "MISSING"
        if status != "OK":
            missing.append(name)
        print(f"{status:8} expect {name:28} android={a} ios={i}  from {expects[name][0]}")

    # trio file check for di/expect
    trio_ok = True
    expect_dir = COMMON / "org/koharu/miyo/core/di/expect"
    for f in sorted(expect_dir.glob("*.kt")) if expect_dir.exists() else []:
        stem = f.name
        for side, base in (("android", ANDROID), ("ios", IOS)):
            ap = base / "org/koharu/miyo/core/di/expect" / stem
            if not ap.exists():
                print(f"MISSING {side} file for {stem}")
                trio_ok = False

    print("---")
    print(f"expects={len(expects)} missing_names={len(missing)} trio_ok={trio_ok}")
    return 1 if missing or not trio_ok else 0


def cmd_candidates() -> int:
    already = {str(p.relative_to(COMMON)) for p in kt_files(COMMON)}
    cands = []
    for p in kt_files(ANDROID):
        rel = str(p.relative_to(ANDROID))
        if rel.startswith("org/koharu/miyo/core/di/expect/"):
            continue
        if "PlatformNative" in rel:
            continue
        if rel in already:
            continue
        text = p.read_text(encoding="utf-8", errors="replace")
        if is_portable(text):
            cands.append(rel)
    print(f"portable_candidates_not_in_common={len(cands)}")
    for rel in cands:
        print(rel)
    return 0


def cmd_audit() -> int:
    rc = cmd_expect_check()
    print()
    cmd_candidates()
    for name in ("commonMain", "androidMain", "iosMain"):
        n = len(list((SHARED / name / "kotlin").rglob("*.kt"))) if (SHARED / name / "kotlin").exists() else 0
        print(f"count {name}={n}")
    leaks = []
    for p in kt_files(COMMON):
        text = p.read_text(encoding="utf-8", errors="replace")
        if BAD_IMPORT.search(text) or re.search(r"^\s*import\s+java\.", text, re.M):
            leaks.append(str(p.relative_to(COMMON)))
    print(f"common_platform_import_leaks={len(leaks)}")
    for x in leaks:
        print(" LEAK", x)
    return rc


def main(argv: list[str]) -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("command", choices=["audit", "candidates", "expect-check"])
    args = ap.parse_args(argv)
    if args.command == "audit":
        return cmd_audit()
    if args.command == "candidates":
        return cmd_candidates()
    if args.command == "expect-check":
        return cmd_expect_check()
    return 2


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
