# KMPify adaptation notes (MIYO)

## Short version

**[MahmoudRH/kmpify](https://github.com/MahmoudRH/kmpify) is Compose-only.**  
It assumes a Compose Multiplatform UI stack and rewrites/moves sources toward that model.

**MIYO does not use Compose Multiplatform for the product UI.**  
The Android shell remains **Views + XML + ViewBinding**, with **Hilt**, **Room**, **WorkManager**, **OkHttp**, and **kotatsu-parsers** living in `shared/src/androidMain`.  
iOS is a **SwiftUI host** over the KMP `shared` framework (`MiyoShared` / `MiyoRuntime` / `MiyoIosBootstrap`), not a Compose iOS app.

Therefore we **do not run upstream kmpify** against this tree. Migration is driven by:

| Tool / process | Role |
|----------------|------|
| Manual + agent passes | expect/actual surface, portable DTOs, runtime bootstrap |
| `tools/miyo_kmpify.py` | Audit expects, list portable androidMain candidates, hygiene checks |
| `docs/KMP_*.md` | Status, inventory, CI policy |

## What upstream kmpify would break here

1. **Compose assumptions** — MIYO list/detail/reader screens are RecyclerView/Fragments, not `@Composable`.
2. **Hilt / Room / Work** — androidMain is intentionally platform-heavy; bulk “move to common” without facades fails metadata + iOS link.
3. **Parser AAR / JNI** — Keiyoushi/kotatsu and `miyo-native` stay Android-only.
4. **Resource / R / BuildConfig** — `org.koharu.miyo.shared` namespace; XML layouts stay under androidMain.

## MIYO KMP layout

```
shared/
  commonMain/   portable models, repos interfaces, VMs, expects, MiyoShared/MiyoRuntime
  androidMain/  full Android app body (Views, DI, DB, network, parsers, JNI, res)
  iosMain/      actuals + MiyoIosBootstrap (+ lightweight Foundation/UIKit hooks)
iosApp/         SwiftUI shell
app/            thin Android Application module
tools/miyo_kmpify.py
```

### expect / actual (required trio)

`core/di/expect/`: Clipboard, DateTime, DateUtils, FileSystem, HttpClient, ImageLoader, Logger, Network, Notification, Platform, Preferences, Share  

`core/nativeio/`: PlatformNativeImage, PlatformNativeZip  

Each has **androidMain + iosMain** actuals. CI `verify-structure` enforces the files exist.

### Host entry points

| Surface | Purpose |
|---------|---------|
| `MiyoShared.initialize()` | Platform hooks only (production Android `BaseApp`) |
| `MiyoShared.startSampleRuntime()` / `MiyoRuntime.start()` | In-memory sample stack for iOS / debug |
| `MiyoIosBootstrap.start()` | Swift launch → sample runtime |

## Portable expansion rules

**Safe to move androidMain → commonMain** when the file has:

- No `android.*` / `androidx.*` / `java.*` / `javax.*` / OkHttp / Hilt / Room / Work / Jsoup / Coil / kotatsu-parsers
- No `String.format`, `System.currentTimeMillis`, `javaClass` (patch or keep on Android)
- No Activity/Fragment/Adapter/Worker implementations that subclass Android types

**Keep on androidMain:** UI widgets, Room entities/DAOs, Hilt modules, WorkManager, OkHttp stacks, Parcelable models tied to parsers, backup models that construct from Room entities.

After moves, re-run:

```bash
python3 tools/miyo_kmpify.py audit
```

## CI policy (Android soft gate)

See `.github/workflows/kmp-ci.yml` and `docs/KMP_CI.md`.

| Job | Gate |
|-----|------|
| verify-structure | **HARD** |
| compileKotlinMetadata (common) | **HARD** |
| linkDebugFrameworkIosSimulatorArm64 | **HARD** |
| compileDebugKotlinAndroid / assemble shared AAR | **SOFT** (`continue-on-error: true`) — still runs for signal |
| :app assembleDebug | **SOFT** |

**Why Android stays soft:** androidMain is still a large Views/Hilt/Room surface (~1000+ kt). Making it a hard required check is **not yet plausible** until shared Android compile is green in CI for a sustained window. When `:shared:compileDebugKotlinAndroid` is reliably green on `main`, flip `continue-on-error` to false and fold the job into `kmp-required`.

## Related docs

- `docs/KMP_MIGRATION_STATUS.md` — module map and architecture limits  
- `docs/KMP_MASS_MIGRATION.md` — earlier hygiene / rollback of non-portable common  
- `docs/KMP_CI.md` — workflow job details  
- `iosApp/README.md` — framework link + Swift usage  
