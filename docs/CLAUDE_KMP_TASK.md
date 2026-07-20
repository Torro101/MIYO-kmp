You are finishing the Kotlin Multiplatform migration of the Miyo manga reader at /data/data/com.termux/files/home/MIYO on branch feature/kmp-migration.

CRITICAL CONSTRAINTS:
- Do NOT run gradle, ./gradlew, jdk, kotlinc, or any build/compile/test task.
- Do NOT commit unless asked.
- Read docs/KMP_FINISH_PLAN.md and execute it completely.
- Work until the verification checklist in that plan is done.
- Prefer editing/moving files with the shell and file tools; be thorough.
- Do not stop after planning — implement every phase.

EXECUTE ALL PHASES in order:

PHASE 1 — Build system
1. Fix gradle/libs.versions.toml:
   - Add android-compileSdk = "36" and android-minSdk = "21" under [versions]
   - shared/build.gradle.kts currently uses libs.versions.android.compileSdk and libs.versions.android.minSdk — change shared/build.gradle.kts to use compileSdk = 36 and minSdk = 21 literals (match app).
   - Add okhttp logging library: com.squareup.okhttp3:logging-interceptor with okhttp version, alias okhttp-logging
   - Change kotlinx-serialization-json dependency from kotlinx-serialization-json-jvm to multiplatform kotlinx-serialization-json for commonMain compatibility
2. Fix shared/build.gradle.kts compileSdk/minSdk and any broken refs.
3. Fix app/build.gradle.kts: REMOVE externalNativeBuild and ndk cmake block (native lives fully in shared). Keep app as thin shell with implementation(project(":shared")). You may slim redundant deps but do not break Hilt Application / tests — keep ksp/hilt/room if app sources need them.

PHASE 2 — Deduplicate
1. Verify shared/src/androidMain/res is complete vs app/src/main/res (diff). If shared is complete, delete app/src/main/res entirely (app gets resources via library merge from :shared).
2. Ensure jpeg-bridge.h and libjpeg-turbo exist under shared/src/androidMain/cpp. Sync any missing files from app/src/main/cpp, then DELETE app/src/main/cpp entirely.
3. Copy app/src/main/assets/* into shared/src/androidMain/assets/ (create dirs). Prefer shared as source of truth; remove app assets if duplicated.
4. Ensure shared AndroidManifest remains the component host; app manifest stays minimal application tag only.

PHASE 3 — commonMain hygiene
1. Remove all javax.inject usage from commonMain. Replace Qualifier annotations with plain Kotlin annotations in common, or move qualifier-only files to androidMain if they're only used there.
2. Confirm zero `import android.` / `import androidx.` in commonMain.
3. Add androidMain bridge stubs implementing common repository interfaces where missing (MangaRepository, etc.) with TODO or delegation — package org.koharu.miyo.core.repository.bridge or similar — only if no existing impl. Don't break existing android code.
4. Add KDoc on common Manga DTO that this is the KMP DTO; androidMain Manga.kt remains parsers extension helpers.

PHASE 4 — expect/actual + native
1. Fix iOS HttpClient and any broken actuals (NSMutableURLRequest import, NSURLSession).
2. Ensure every expect in commonMain/core/di/expect has matching actual in androidMain and iosMain.
3. Add common expect for native helpers:
   - org.koharu.miyo.core.nativeio.NativeImageProbe (expect object)
   - org.koharu.miyo.core.nativeio.NativeZipWriter
   androidMain: wire to existing JNI if wrappers exist under core/nativeio; else thin stubs calling external funs.
   iosMain: stubs returning null/false with KDoc that pure Kotlin fallback is used.
4. Add MiyoShared bridge in commonMain: object MiyoShared { fun hello(): String; fun platformName(): String using Platform expect }

PHASE 5 — iosApp
Create iosApp/ with:
- README.md (how to link shared.framework, open in Xcode on Mac)
- Miyo/MiyoApp.swift, ContentView.swift, Info.plist
- Minimal valid Xcode project.pbxproj OR clear SPM instructions
SwiftUI placeholder titled Miyo that explains shared KMP framework integration.

PHASE 6 — Docs
Write docs/KMP_MIGRATION_STATUS.md with module map, what moved, limitations, how to build later, checklist results.

PHASE 7 — Final inventory printed at end:
- counts of kt files per source set
- confirm no app/src/main/cpp
- confirm res location
- list expect/actual files
- any remaining known issues

Start immediately. Implement everything. Do not ask questions.
