# CI notes

## Workflow: `.github/workflows/kmp-ci.yml`

Required green jobs:

1. **verify-structure** — source sets, no Android/JVM imports in commonMain, expect/actual trios
2. **compile-shared-android** — `:shared:compileKotlinMetadata` + `:shared:compileDebugKotlinAndroid` + AAR
3. **compile-shared-ios** (macos-14) — `:shared:linkDebugFrameworkIosSimulatorArm64`
4. **compile-app-android** — `:app:compileDebugKotlin` + `:app:assembleDebug`

Triggers: `main`, `feature/kmp-migration`, PRs, manual dispatch.

## iOS host

- `MiyoIosBootstrap.start()` → sample runtime
- SwiftUI shell under `iosApp/`
- Production Android does **not** install sample repos (only `MiyoShared.initialize()` + context holder)

## Namespace

- `:app` → `org.koharu.miyo`
- `:shared` → `org.koharu.miyo.shared` (R/BuildConfig/databinding)
