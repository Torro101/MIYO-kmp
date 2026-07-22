# CI notes

## Workflow: `.github/workflows/kmp-ci.yml`

### Required (HARD) green jobs

1. **verify-structure** — source sets, no Android/JVM imports or JVM-only APIs in commonMain, expect/actual trios, `tools/miyo_kmpify.py expect-check`, bootstrap files present
2. **compile-common** — `:shared:compileKotlinMetadata`
3. **compile-shared-ios** (macos-14) — `:shared:linkDebugFrameworkIosSimulatorArm64`
4. **kmp-required** — aggregates the three above

### Soft (non-blocking) jobs — `continue-on-error: true`

| Job | Command | Why soft |
|-----|---------|----------|
| **compile-shared-android** | `:shared:compileDebugKotlinAndroid` (+ AAR if ok) | androidMain still ~1000+ Views/Hilt/Room/parsers files; not yet a reliable required gate |
| **compile-app-android** | `:app:assembleDebug` | Depends on full Android shared + app shell |

**Promotion criteria:** when `:shared:compileDebugKotlinAndroid` is green on `main` for a sustained window, set `continue-on-error: false` on `compile-shared-android` and add it to `kmp-required.needs`.

Triggers: `main`, `feature/kmp-migration`, PRs, manual dispatch.

## iOS host

- `MiyoIosBootstrap.start()` → sample runtime (`MiyoShared.startSampleRuntime()`)
- SwiftUI shell under `iosApp/`
- Production Android does **not** install sample repos (only `MiyoShared.initialize()` + context holder)

## Namespace

- `:app` → `org.koharu.miyo`
- `:shared` → `org.koharu.miyo.shared` (R/BuildConfig/databinding)

## Related

- `docs/KMPIFY_ADAPTATION.md` — why not MahmoudRH/kmpify; soft-gate policy
- `tools/miyo_kmpify.py` — expect audit / portable candidate scan
