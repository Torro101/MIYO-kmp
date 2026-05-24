#!/usr/bin/env python3
"""
Probe manga source URLs with a persistent cookie jar and report captcha state.

This is a developer/CI diagnostic tool. It does not solve or bypass captcha.
It verifies whether stored cookies are still accepted by a provider, writes the
cookie jar with owner-only permissions, and can watch URLs continuously.
"""

from __future__ import annotations

import argparse
import http.cookiejar
import json
import os
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from dataclasses import dataclass
from datetime import datetime, timezone
from pathlib import Path
from typing import Iterable


DEFAULT_COOKIE_JAR = Path.home() / ".local/state/miyo/captcha-probe/cookies.txt"
DEFAULT_USER_AGENT = (
    "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 "
    "(KHTML, like Gecko) Chrome/125.0 Mobile Safari/537.36"
)

CHALLENGE_MARKERS = (
    "just a moment",
    "performing security verification",
    "verification successful",
    "checking your browser",
    "cf-chl",
    "__cf_chl_tk",
    "cf_clearance",
    "captcha",
    "hcaptcha",
    "recaptcha",
)

CLEARANCE_COOKIES = (
    "cf_clearance",
    "__cf_bm",
    "__cflb",
    "__cfseq",
)


@dataclass(frozen=True)
class ProbeResult:
    url: str
    final_url: str | None
    status_code: int | None
    state: str
    has_clearance_cookie: bool
    cookie_count: int
    error: str | None = None

    def to_json(self) -> str:
        return json.dumps(
            {
                "time": datetime.now(timezone.utc).isoformat(),
                "url": self.url,
                "final_url": self.final_url,
                "status_code": self.status_code,
                "state": self.state,
                "has_clearance_cookie": self.has_clearance_cookie,
                "cookie_count": self.cookie_count,
                "error": self.error,
            },
            sort_keys=True,
        )


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("urls", nargs="*", help="Provider URLs to probe")
    parser.add_argument("--file", help="Text file containing one URL per line")
    parser.add_argument("--cookie-jar", default=str(DEFAULT_COOKIE_JAR), help="Mozilla cookie jar path")
    parser.add_argument("--watch", action="store_true", help="Probe forever")
    parser.add_argument("--interval", type=float, default=60.0, help="Watch interval in seconds")
    parser.add_argument("--timeout", type=float, default=25.0, help="Request timeout in seconds")
    parser.add_argument("--user-agent", default=DEFAULT_USER_AGENT)
    parser.add_argument("--referer", help="Optional Referer header")
    parser.add_argument("--json", action="store_true", help="Emit JSON lines only")
    return parser.parse_args()


def read_urls(args: argparse.Namespace) -> list[str]:
    urls = list(args.urls)
    if args.file:
        for line in Path(args.file).read_text(encoding="utf-8").splitlines():
            line = line.strip()
            if line and not line.startswith("#"):
                urls.append(line)
    normalized = []
    for url in urls:
        parsed = urllib.parse.urlparse(url)
        if parsed.scheme not in {"http", "https"} or not parsed.netloc:
            raise SystemExit(f"invalid URL: {url}")
        normalized.append(url)
    if not normalized:
        raise SystemExit("provide at least one URL or --file")
    return normalized


def load_cookie_jar(path: Path) -> http.cookiejar.MozillaCookieJar:
    path.parent.mkdir(parents=True, exist_ok=True)
    jar = http.cookiejar.MozillaCookieJar(str(path))
    if path.exists() and path.stat().st_size > 0:
        jar.load(ignore_discard=True, ignore_expires=False)
    jar.clear_expired_cookies()
    save_cookie_jar(jar, path)
    return jar


def save_cookie_jar(jar: http.cookiejar.MozillaCookieJar, path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    jar.save(ignore_discard=True, ignore_expires=False)
    os.chmod(path, 0o600)


def host_matches(cookie_domain: str, host: str) -> bool:
    domain = cookie_domain.lstrip(".").lower()
    host = host.lower()
    return host == domain or host.endswith("." + domain)


def has_clearance_cookie(jar: http.cookiejar.CookieJar, url: str) -> bool:
    parsed = urllib.parse.urlparse(url)
    host = parsed.hostname or ""
    now = time.time()
    for cookie in jar:
        if not host_matches(cookie.domain, host):
            continue
        if cookie.expires is not None and cookie.expires <= now:
            continue
        if cookie.name in CLEARANCE_COOKIES or cookie.name.startswith("__cf"):
            return True
    return False


def detect_challenge(body: bytes, headers: Iterable[tuple[str, str]]) -> bool:
    header_text = "\n".join(f"{k}: {v}" for k, v in headers).lower()
    text = body[:512_000].decode("utf-8", errors="ignore").lower()
    haystack = header_text + "\n" + text
    return any(marker in haystack for marker in CHALLENGE_MARKERS)


def build_opener(jar: http.cookiejar.CookieJar) -> urllib.request.OpenerDirector:
    return urllib.request.build_opener(urllib.request.HTTPCookieProcessor(jar))


def probe_url(
    opener: urllib.request.OpenerDirector,
    jar: http.cookiejar.MozillaCookieJar,
    url: str,
    timeout: float,
    user_agent: str,
    referer: str | None,
) -> ProbeResult:
    before_clearance = has_clearance_cookie(jar, url)
    request = urllib.request.Request(
        url,
        headers={
            "User-Agent": user_agent,
            "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
            "Cache-Control": "no-cache",
            **({"Referer": referer} if referer else {}),
        },
        method="GET",
    )
    try:
        with opener.open(request, timeout=timeout) as response:
            body = response.read(512_000)
            final_url = response.geturl()
            status_code = response.getcode()
            challenge = detect_challenge(body, response.headers.items())
    except urllib.error.HTTPError as error:
        body = error.read(512_000)
        final_url = error.geturl()
        status_code = error.code
        challenge = detect_challenge(body, error.headers.items())
    except Exception as error:
        return ProbeResult(
            url=url,
            final_url=None,
            status_code=None,
            state="network_error",
            has_clearance_cookie=before_clearance,
            cookie_count=len(jar),
            error=f"{type(error).__name__}: {error}",
        )

    after_clearance = has_clearance_cookie(jar, final_url or url)
    if challenge and before_clearance:
        state = "cookie_rejected_or_expired"
    elif challenge:
        state = "needs_verification"
    elif after_clearance:
        state = "verified"
    else:
        state = "ok_no_challenge_cookie"

    return ProbeResult(
        url=url,
        final_url=final_url,
        status_code=status_code,
        state=state,
        has_clearance_cookie=after_clearance,
        cookie_count=len(jar),
    )


def print_result(result: ProbeResult, json_only: bool) -> None:
    if json_only:
        print(result.to_json(), flush=True)
        return
    print(result.to_json(), flush=True)
    if result.state in {"needs_verification", "cookie_rejected_or_expired"}:
        print(f"action: open this URL in the app captcha WebView again: {result.url}", flush=True)


def main() -> int:
    args = parse_args()
    urls = read_urls(args)
    cookie_path = Path(args.cookie_jar).expanduser()
    jar = load_cookie_jar(cookie_path)
    opener = build_opener(jar)

    while True:
        for url in urls:
            result = probe_url(opener, jar, url, args.timeout, args.user_agent, args.referer)
            save_cookie_jar(jar, cookie_path)
            print_result(result, args.json)
        if not args.watch:
            break
        time.sleep(max(args.interval, 1.0))
    return 0


if __name__ == "__main__":
    sys.exit(main())
