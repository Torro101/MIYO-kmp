# Miyo KMP Finish Plan (Android + iOS)

**Constraint:** Do NOT run Gradle, JDK, `./gradlew`, or any build/compile task. Source/layout edits only.

## Current state (as of branch feature/kmp-migration)

| Area | Count / note |
|------|----------------|
| shared commonMain .kt | ~237 (domain stubs, expect APIs, simplified models) |
| shared androidMain .kt | ~941 (full Android app code moved here) |
| shared iosMain .kt | 12 (expect actuals only) |
| app .kt | ~21 (debug/release MiyoApp + tests) |
| Resources | DUPLICATED app/src/main/res AND shared/src/androidMain/res (~6.5M each) |
| C++ | DUPLICATED app/src/main/cpp AND shared/src/androidMain/cpp |
| Assets/models | in app only; shared assets incomplete |
| iosApp | MISSING |
| Build catalog | missing `android.compileSdk`, `android.minSdk`, `okhttp-logging` used by shared |

Architecture target:

```
:shared  (kotlin multiplatform)
  commonMain   — pure Kotlin domain, interfaces, expect declarations, portable utils
  androidMain  — Android UI (Views/XML), Room, Hilt, WorkManager, OkHttp, JNI
  iosMain      — iOS actuals + optional cinterop/native helpers
:app     — thin Android application shell (Application class variants, signing, tests)
:iosApp  — Xcode/SwiftUI shell embedding shared.framework
```

UI stays Android Views in androidMain for now (not a full Compose Multiplatform rewrite).
iOS gets a real app shell + working platform actuals consuming shared domain APIs.

## Phase 1 — Build system correctness (no gradle run)

1. `gradle/libs.versions.toml`
   - Add `[versions] android-compileSdk = "36"`, `android-minSdk = "21"` (or nested keys matching `libs.versions.android.compileSdk` usage).
   - Prefer:
     ```
     android-compileSdk = "36"
     android-minSdk = "21"
     ```
     and in `[versions]` also expose via:
     ```
     # If build uses libs.versions.android.compileSdk:
     ```
     Fix `shared/build.gradle.kts` to use either catalog or literals consistently with `app` (compileSdk 36, minSdk 21).
   - Add okhttp logging: `okhttp-logging = { module = "com.squareup.okhttp3:logging-interceptor", version.ref = "okhttp" }`
   - Add KMP-friendly deps if missing: ktor (optional), multiplatform settings, datetime — only if code needs them.
   - Fix `kotlinx-serialization-json` to multiplatform artifact (`kotlinx-serialization-json` not `-jvm`) for commonMain.
   - Add compose/ios plugins only if introducing CMP UI (out of scope unless needed for iosApp).

2. `shared/build.gradle.kts`
   - Ensure androidTarget + iosX64/iosArm64/iosSimulatorArm64 framework `shared` static.
   - Fix compileSdk/minSdk references.
   - commonMain: coroutines-core, okio, serialization-json (mpp).
   - androidMain: keep Android stack; ksp/room/hilt configuration.
   - iosMain: coroutines-core; add ktor-client-darwin OR keep custom NSURLSession actuals.
   - Point CMake only at `src/androidMain/cpp/CMakeLists.txt`.
   - Ensure AndroidManifest/res/assets/jni from androidMain.
   - Add `kotlin.mpp.androidSourceSetLayoutVersion=2` in gradle.properties if needed.

3. `app/build.gradle.kts`
   - Remove `externalNativeBuild` (native lives in shared).
   - Remove duplicate heavy deps that are already `api`/`implementation` from shared — prefer `implementation(project(":shared"))` and only keep app-entry needs (hilt for MiyoApp, leakcanary debug, test deps).
   - Keep applicationId, signing, buildConfig, debug/release source sets.
   - Do not remove room/hilt from app if Application/tests still need annotation processing at app level — keep minimal set.

4. `settings.gradle.kts`
   - `include(":app", ":shared")`
   - Optionally document iosApp as Xcode project (not always a Gradle module). For KMP iOS, either:
     - `iosApp/` Xcode project consuming shared framework, OR
     - Gradle `iosApp` with `org.jetbrains.kotlin.native.cocoapods` / compose app.
   - Use Xcode-first iosApp structure (works without Mac gradle ios build here).

## Phase 2 — Deduplicate files (single source of truth)

1. **Resources:** Keep `shared/src/androidMain/res` as canonical. Remove `app/src/main/res` tree AFTER verifying shared has full copy (`diff -rq`). App manifest may still reference `@mipmap`/`@string` — those must resolve via library merge from shared, OR keep only app-specific overlays in app (launcher name overrides already via resValue).
   - If manifest merge needs app-level resources for backup xml etc., keep minimal app res OR ensure shared has them (shared already has full res).
   - Delete duplicate app res to avoid merge conflicts/doubled assets.

2. **C++:** Keep `shared/src/androidMain/cpp` only. Delete `app/src/main/cpp` after verifying identical. Ensure jpeg-bridge.h and libjpeg-turbo present under shared.

3. **Assets:** Copy `app/src/main/assets/**` (models + pem) into `shared/src/androidMain/assets/` if missing. Then remove from app OR leave empty keepers.

4. **Schemas:** If Room schemas lived under app, move to `shared/schemas` matching room { schemaDirectory }.

## Phase 3 — commonMain hygiene

1. Remove `javax.inject.Qualifier` from commonMain files; replace with:
   ```kotlin
   @Retention(AnnotationRetention.BINARY)
   @Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
   annotation class QualifierName
   ```
   or move those qualifier files to androidMain only.

2. Files using Qualifier today:
   - local/data/Qualifiers.kt, Caches.kt
   - core/network/HttpClients.kt
   - core/LocalizedAppContext.kt
   - scrobbling/.../ScrobblerType.kt

3. Ensure no `android.*` / `androidx.*` imports in commonMain.

4. **Manga model strategy:**
   - commonMain `org.koharu.miyo.core.model.Manga` is a simplified serializable DTO for shared domain/VMs.
   - androidMain `Manga.kt` is extension helpers on `org.koitharu.kotatsu.parsers.model.Manga` — KEEP filename/package OK (no class clash).
   - Rename common DTO package OR type to avoid developer confusion if needed:
     - Prefer rename common to `MangaDto` / package `org.koharu.miyo.shared.model` OR keep and document.
   - Wire repositories: common interfaces; android actual implementations delegating to existing Room/repos.
   - Add `androidMain` classes implementing common `MangaRepository` etc. bridging parsers models ↔ DTO (mappers).

5. Expand common domain only where portable:
   - Keep simplified ViewModels/use cases if they compile on common.
   - Do not force-move Android Fragments/Adapters/Room DAOs.

6. Fix broken common stubs that reference missing types.

## Phase 4 — Platform expect/actual completion

### common expect set (already present)
Platform, Logger, Preferences, FileSystem, HttpClient, ImageLoader, Network, Notification, Clipboard, Share, DateTime, DateUtils

### Android actuals
Ensure each matches expect signatures exactly; fix any drift.

### iOS actuals
- Fix compile issues (e.g. HttpClient: import NSMutableURLRequest; consistent NSURLSession).
- Preferences → NSUserDefaults
- FileSystem → NSFileManager paths + okio
- Logger → NSLog / os_log
- Network → NWPathMonitor if available else stub
- Notification → UNUserNotificationCenter stubs
- ImageLoader → reasonable stub or platform image load
- Clipboard / Share → UIKit/UIPasteboard/UIActivityViewController (use `platform.UIKit` carefully for kotlinx)
- DateTime → NSDate / kotlinx-datetime if added

### Native image/zip expect API (new)
commonMain:
```kotlin
expect object NativeImageProbe {
  fun probe(path: String): ImageProbeResult?
}
expect object NativeZipWriter { ... }
```
androidMain: JNI to existing native_image_probe / native_zip_writer
iosMain: stub returning null / pure Kotlin fallback (document)

Move JNI Kotlin wrappers under androidMain `core/nativeio` if not already.

## Phase 5 — iosApp shell

Create `iosApp/`:
```
iosApp/
  README.md
  Miyo/
    MiyoApp.swift
    ContentView.swift
    Info.plist
  Miyo.xcodeproj/project.pbxproj  (or Package.swift + instructions)
```

Minimal SwiftUI app that:
- Documents how to embed `shared.framework` from Gradle `linkReleaseFrameworkIosArm64` etc.
- Shows placeholder UI: "Miyo" + note that shared domain loads via Kotlin/Native
- Optional Kotlin bridge header notes

Also add `shared/src/iosMain/kotlin/.../MiyoIosBridge.kt` exporting a simple `class MiyoShared { fun platformName(): String }` for smoke test.

Add root `README-KMP.md` or section in main README.

## Phase 6 — App module thin shell

- Keep debug/release `MiyoApp.kt`, BaseService stubs, CurlLoggingInterceptor variants.
- Manifest stays minimal application tag; components from shared library manifest.
- Tests: leave androidTest in app; unit tests can stay or move with code.
- Remove obsolete `build.gradle.old` only if safe (optional).
- proguard rules: keep in app; ensure shared consumer proguard if needed (`consumer-rules.pro`).

## Phase 7 — Documentation & inventory

Write `docs/KMP_MIGRATION_STATUS.md`:
- Module map
- What is common vs android vs ios
- How to build Android / iOS (user will build later)
- Known limitations (UI not CMP; parsers Android/JVM; plugins)
- Dedup actions taken
- Remaining future work (Compose Multiplatform UI, SQLDelight, Ktor everywhere)

## Verification checklist (manual, no gradle)

- [ ] No duplicate res trees
- [ ] No duplicate cpp under app
- [ ] Assets/models available to shared Android
- [ ] libs.versions.toml has all keys referenced by build scripts
- [ ] commonMain has zero android/javax.inject imports
- [ ] Every common `expect` has android + ios `actual`
- [ ] iosApp exists with Swift entry
- [ ] settings includes shared + app
- [ ] app does not declare externalNativeBuild
- [ ] MIGRATION status doc written
- [ ] File counts documented

## Explicit non-goals (this pass)

- Running gradle/tests/lint
- Full Compose Multiplatform UI rewrite of 900+ Android UI files
- Making kotatsu-parsers work on iOS (keep source parsing Android-first; iOS uses shared DTOs + future API)
- Publishing frameworks to Maven

## Execution order for agent

1. Fix version catalog + build.gradle.kts files
2. Deduplicate cpp/res/assets
3. commonMain hygiene + repository bridges skeleton
4. Fix/complete ios actuals + native expect stubs
5. Create iosApp
6. Thin app gradle + remove app native build
7. Docs + final inventory
