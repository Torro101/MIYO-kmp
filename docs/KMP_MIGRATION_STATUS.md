# Miyo KMP migration status

Branch: `feature/kmp-migration`  
Last full pass: finished layout for Android + iOS (source only; **no Gradle/JDK runs** per request).

## Module map

| Module | Role |
|--------|------|
| `:shared` | Kotlin Multiplatform library — domain, androidMain app code, iosMain actuals, C++/JNI (Android) |
| `:app` | Thin Android application shell (debug/release `MiyoApp`, tests, signing) |
| `iosApp/` | SwiftUI host for `shared.framework` (build framework on macOS) |

### Source sets (`:shared`)

| Set | Contents |
|-----|----------|
| `commonMain` | Portable domain DTOs, repository interfaces, use-cases, ViewModels, expect APIs, `MiyoShared`, native facades |
| `androidMain` | Full Android UI (Views/XML), Room, Hilt, WorkManager, OkHttp, parsers, JNI wrappers, resources, C++ |
| `iosMain` | actual implementations for platform expects + native stubs |

## What this pass completed

1. **Version catalog**
   - `android-compileSdk` / `android-minSdk`
   - `okhttp-logging`
   - multiplatform `kotlinx-serialization-json` (not `-jvm`)

2. **Build scripts**
   - `shared`: ios deps, NDK version, consumer proguard, viewBinding/buildConfig, namespace `org.koharu.miyo`
   - `app`: removed `externalNativeBuild` (native only in shared)
   - `gradle.properties`: MPP android source set layout v2, cinterop commonization, ignore disabled native targets

3. **Dedup**
   - Removed `app/src/main/res` and `app/src/main/cpp` (canonical copies under `shared/src/androidMain/`)
   - Copied assets/models into `shared/src/androidMain/assets/`

4. **commonMain hygiene**
   - Moved `javax.inject` qualifier annotations to `androidMain` (Hilt requires them)
   - Verified no `android.*` / `javax.inject` in commonMain

5. **Platform bridges**
   - `PlatformNativeImage` / `PlatformNativeZip` expect + android JNI + ios stubs
   - Fixed iOS `HttpClient`, `FileSystem`, `Preferences` observe defaults
   - `MiyoShared` entry + `RepositoryBridges` registry
   - `BaseApp` calls `MiyoShared.initialize()`

6. **iosApp**
   - SwiftUI shell, Info.plist, README, starter Xcode project

## Counts (after portable expand + iOS harden pass)

| Set | .kt |
|-----|-----|
| commonMain | ~138 (portable only) |
| androidMain | ~1067 (full Android app) |
| iosMain | 14 |
| app | 21 |

See also `docs/KMP_MASS_MIGRATION.md` for the hygiene pass that moved ~79 non-portable files out of commonMain and added shared domain types.
## expect / actual inventory

commonMain `core/di/expect/`:

- Clipboard, DateTime, DateUtils, FileSystem, HttpClient, ImageLoader, Logger, Network, Notification, Platform, Preferences, Share

Plus native:

- `core/nativeio/PlatformNativeImage`, `PlatformNativeZip`

Each has androidMain + iosMain actuals.

## Architecture limits (honest)

- **Not** a full Compose Multiplatform UI rewrite. ~900 androidMain files remain Android Views/XML/Hilt.
- **kotatsu-parsers / Keiyoushi plugins** stay Android/JVM.
- **iOS** is a host + platform actuals + shared DTOs/use-cases — not full reader parity yet.
- C++ (`miyo-native`) is Android NDK only; iOS native facades return unavailable.

## How to build later (when allowed)

```bash
# Android
./gradlew :app:assembleDebug

# iOS framework (macOS)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
# then open iosApp/ and link shared.framework — see iosApp/README.md
```

## Remaining future work

- Wire common `MangaRepository` bridges to Room/parsers with mappers (DTO ↔ parsers model)
- SQLDelight or multiplatform Room for real shared DB
- Ktor client in common instead of dual OkHttp/NSURLSession facades
- Compose Multiplatform for shared UI screens
- Expand iOS feature modules incrementally
- CI matrix: Android + iOS framework link

## Verification checklist

- [x] No `app/src/main/cpp`
- [x] No `app/src/main/res` (resources in shared)
- [x] Assets/models under shared androidMain
- [x] Catalog keys for compile/min SDK + okhttp-logging
- [x] commonMain free of android/javax.inject
- [x] expect APIs have android + ios actuals
- [x] iosApp shell present
- [x] app has no externalNativeBuild
- [x] Status doc written
- [ ] Gradle compile (intentionally not run)
