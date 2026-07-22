#!/usr/bin/env python3
"""
miyo_kmpify — View-based KMP migration helper for MIYO.

Inspired by https://github.com/MahmoudRH/kmpify (Compose MigrationManager),
but for a **Views / XML / androidMain** KMP layout rather than Compose resources.

Scans `shared/src/androidMain/kotlin` for pure-Kotlin files (no Android /
AndroidX / OkHttp / Hilt / Room / Coil / Material imports) and can list or
move them into `shared/src/commonMain/kotlin`.

Usage:
  python3 tools/miyo_kmpify.py list
  python3 tools/miyo_kmpify.py list --json
  python3 tools/miyo_kmpify.py move --dry-run
  python3 tools/miyo_kmpify.py move
  python3 tools/miyo_kmpify.py check PATH.kt

Exit codes: 0 ok, 1 error, 2 nothing matched.
"""

from __future__ import annotations

import argparse
import json
import re
import shutil
import sys
from dataclasses import asdict, dataclass
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ANDROID_MAIN = ROOT / "shared" / "src" / "androidMain" / "kotlin"
COMMON_MAIN = ROOT / "shared" / "src" / "commonMain" / "kotlin"

# Imports that keep a file on androidMain (or need expect/actual).
_BLOCKING_PREFIXES = (
    "android.",
    "androidx.",
    "dalvik.",
    "com.google.android.",
    "dagger.",
    "javax.inject.",
    "javax.",
    "okhttp3.",
    "coil.",
    "coil3.",
    "org.json.",
    "kotlinx.parcelize.",
    "java.io.",
    "java.nio.",
    "java.net.",
    "java.util.concurrent.",
    "java.security.",
    "java.text.",
    "java.time.",
)

# okio: allow a few pure multiplatform symbols; block the rest.
_OKIO_ALLOWED = {
    "okio.Path",
    "okio.FileSystem",
    "okio.ByteString",
    "okio.buffer",
    "okio.use",
    "okio.Source",
    "okio.Sink",
    "okio.BufferedSource",
    "okio.BufferedSink",
}

IMPORT_RE = re.compile(r"^\s*import\s+(?:static\s+)?([`\w.*]+)", re.MULTILINE)

# Soft markers — not hard blockers but reported.
SOFT_MARKERS = (
    "ConcurrentHashMap",
    "System.currentTimeMillis",
    "System.nanoTime",
    "File(",
    "java.util.",
    "java.io.",
    "java.net.",
)

PACKAGE_RE = re.compile(r"^\s*package\s+([\w.]+)", re.MULTILINE)


@dataclass
class Candidate:
    rel_android: str
    package: str
    lines: int
    soft_markers: list[str]
    reason_clean: str


def read_text(path: Path) -> str:
    return path.read_text(encoding="utf-8", errors="replace")


def _import_blocked(imp: str) -> str | None:
    # strip backticks / alias
    imp = imp.strip().strip("`")
    if imp.startswith("okio."):
        base = imp.split(" as ")[0].rstrip(".*")
        # allow exact multiplatform-ish symbols and star only if we cannot tell
        if imp == "okio.*" or any(base == a or base.startswith(a + ".") for a in _OKIO_ALLOWED):
            return None
        return f"import {imp}"
    for prefix in _BLOCKING_PREFIXES:
        if imp == prefix.rstrip(".") or imp.startswith(prefix):
            return f"import {imp}"
    # kotatsu-parsers non-model packages tend to pull OkHttp / JVM APIs
    if imp.startswith("org.koitharu.kotatsu.parsers.") and not imp.startswith(
        "org.koitharu.kotatsu.parsers.model."
    ):
        return f"import {imp}"
    return None


def is_blocking(src: str) -> str | None:
    for m in IMPORT_RE.finditer(src):
        reason = _import_blocked(m.group(1))
        if reason:
            return reason
    # bare type references without import
    if re.search(r"(?<![\w.])android\.(content|os|util|view|widget|app|graphics)\.", src):
        return "bare android.* type reference"
    if re.search(r"(?<![\w.])androidx\.", src):
        return "bare androidx.* type reference"
    if re.search(r"(?<![\w.])okhttp3\.", src):
        return "bare okhttp3.* type reference"
    return None


def soft_hits(src: str) -> list[str]:
    return [marker for marker in SOFT_MARKERS if marker in src]


def scan() -> list[Candidate]:
    if not ANDROID_MAIN.is_dir():
        raise SystemExit(f"androidMain not found: {ANDROID_MAIN}")
    out: list[Candidate] = []
    for path in sorted(ANDROID_MAIN.rglob("*.kt")):
        src = read_text(path)
        block = is_blocking(src)
        if block:
            continue
        pkg_m = PACKAGE_RE.search(src)
        pkg = pkg_m.group(1) if pkg_m else ""
        rel = path.relative_to(ANDROID_MAIN).as_posix()
        out.append(
            Candidate(
                rel_android=rel,
                package=pkg,
                lines=src.count("\n") + 1,
                soft_markers=soft_hits(src),
                reason_clean="no android/androidx/okhttp/hilt/room/coil imports",
            )
        )
    return out


def common_dest(rel_android: str) -> Path:
    return COMMON_MAIN / rel_android


def cmd_list(as_json: bool) -> int:
    cands = scan()
    if as_json:
        print(json.dumps([asdict(c) for c in cands], indent=2))
    else:
        print(f"# pure-kotlin candidates under androidMain: {len(cands)}")
        print(f"# androidMain={ANDROID_MAIN}")
        print(f"# commonMain ={COMMON_MAIN}")
        for c in cands:
            soft = f"  soft={','.join(c.soft_markers)}" if c.soft_markers else ""
            dest = common_dest(c.rel_android)
            exists = " EXISTS" if dest.exists() else ""
            print(f"{c.rel_android}  ({c.lines} lines){soft}{exists}")
    return 0 if cands else 2


def cmd_check(path: Path) -> int:
    p = path if path.is_absolute() else (ROOT / path)
    if not p.is_file():
        print(f"not a file: {p}", file=sys.stderr)
        return 1
    src = read_text(p)
    block = is_blocking(src)
    if block:
        print(f"BLOCKED: {block}")
        return 1
    print("CLEAN (candidate for commonMain)")
    soft = soft_hits(src)
    if soft:
        print("soft markers:", ", ".join(soft))
    return 0


def cmd_move(dry_run: bool) -> int:
    cands = scan()
    if not cands:
        print("No candidates.")
        return 2
    moved = 0
    skipped = 0
    for c in cands:
        src = ANDROID_MAIN / c.rel_android
        dst = common_dest(c.rel_android)
        if dst.exists():
            print(f"SKIP exists: {c.rel_android}")
            skipped += 1
            continue
        print(f"{'DRY ' if dry_run else ''}MOVE {c.rel_android}")
        print(f"  -> {dst.relative_to(ROOT)}")
        if not dry_run:
            dst.parent.mkdir(parents=True, exist_ok=True)
            shutil.move(str(src), str(dst))
            parent = src.parent
            while parent != ANDROID_MAIN and parent.is_dir() and not any(parent.iterdir()):
                parent.rmdir()
                parent = parent.parent
        moved += 1
    print(f"done: moved={moved} skipped={skipped} dry_run={dry_run}")
    return 0


def cmd_expect_check() -> int:
    """CI hygiene: every expect file has android + ios actuals."""
    expect_names = [
        "Clipboard", "DateTime", "DateUtils", "FileSystem", "HttpClient",
        "ImageLoader", "Logger", "Network", "Notification", "Platform",
        "Preferences", "Share",
    ]
    missing = []
    for name in expect_names:
        for root, label in (
            (COMMON_MAIN / "org/koharu/miyo/core/di/expect" / f"{name}.kt", "common"),
            (ANDROID_MAIN / "org/koharu/miyo/core/di/expect" / f"{name}.kt", "android"),
            (
                ROOT / "shared/src/iosMain/kotlin/org/koharu/miyo/core/di/expect" / f"{name}.kt",
                "ios",
            ),
        ):
            if not root.is_file():
                missing.append(f"{label}:{name}")
    native = [
        COMMON_MAIN / "org/koharu/miyo/core/nativeio/PlatformNative.kt",
        ANDROID_MAIN / "org/koharu/miyo/core/nativeio/PlatformNative.android.kt",
        ROOT / "shared/src/iosMain/kotlin/org/koharu/miyo/core/nativeio/PlatformNative.ios.kt",
    ]
    for p in native:
        if not p.is_file():
            missing.append(str(p.relative_to(ROOT)))
    if missing:
        print("expect-check FAIL:")
        for m in missing:
            print(" ", m)
        return 1
    print(f"expect-check OK ({len(expect_names)} trios + PlatformNative)")
    return 0


def main(argv: list[str] | None = None) -> int:
    ap = argparse.ArgumentParser(description="MIYO View-based KMP pure-Kotlin scanner/mover")
    sub = ap.add_subparsers(dest="cmd", required=True)

    p_list = sub.add_parser("list", help="List pure-Kotlin candidates in androidMain")
    p_list.add_argument("--json", action="store_true")

    p_move = sub.add_parser("move", help="Move candidates androidMain -> commonMain")
    p_move.add_argument("--dry-run", action="store_true")

    p_check = sub.add_parser("check", help="Check a single .kt file")
    p_check.add_argument("path", type=Path)

    sub.add_parser("expect-check", help="Verify expect/actual trios for CI")
    sub.add_parser("audit", help="Alias of list")
    sub.add_parser("candidates", help="Alias of list")

    args = ap.parse_args(argv)
    if args.cmd in ("list", "audit", "candidates"):
        return cmd_list(getattr(args, "json", False))
    if args.cmd == "move":
        return cmd_move(args.dry_run)
    if args.cmd == "check":
        return cmd_check(args.path)
    if args.cmd == "expect-check":
        return cmd_expect_check()
    return 1


if __name__ == "__main__":
    sys.exit(main())
