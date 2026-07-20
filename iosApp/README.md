# Miyo iOS app (KMP host)

This is the iOS shell for the Kotlin Multiplatform `shared` module.

## Prerequisites

- macOS with Xcode 15+
- JDK 17+ and Android SDK (to build the KMP framework via Gradle)
- CocoaPods optional (framework is linked directly)

## Build the shared framework

From the repo root on a Mac:

```bash
# Simulator (Apple Silicon)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# Device
./gradlew :shared:linkDebugFrameworkIosArm64
```

Framework output (typical):

```
shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework
```

## Open in Xcode

1. Create or open an Xcode iOS App project using the sources under `iosApp/Miyo/`.
2. Add `shared.framework` under **Frameworks, Libraries, and Embedded Content**.
   The Gradle KMP config builds a **static** framework (`isStatic = true`) — link it; no embed copy required for static.
3. Set **Framework Search Paths** to the Gradle output directory for your target.
4. Optionally add a Run Script build phase that invokes Gradle before compile.

A starter `Miyo.xcodeproj` is included. Prefer regenerating with Xcode if the project file needs updates:

```text
File → New → Project → App (SwiftUI)
Bundle ID: org.koharu.miyo
Add existing files from iosApp/Miyo/
```

## Call into Kotlin

After linking:

```swift
import shared

// Example (exact ObjC names depend on Kotlin export):
// MiyoShared.shared.initialize()
// let msg = MiyoShared.shared.hello()
```

Export surface lives in `org.koharu.miyo.MiyoShared` (commonMain).

## Scope notes

- Android still owns the full Material/Views UI, Room, Hilt, WorkManager, parsers, and JNI.
- iOS currently has platform actuals (HTTP, prefs, FS, etc.) + domain DTOs.
- Feature parity (reader, downloads, plugins) is future work on top of this shell.
