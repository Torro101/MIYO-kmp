# Miyo iOS app (KMP host)

## Status

iOS MVP shell with Library / Favorites / History / Settings tabs.

Shared Kotlin exposes:

- `MiyoIosBootstrap.start()`
- `MiyoShared.library()`, `favorites()`, `history()`, `search()`, `toggleFavorite()`, …
- In-memory sample catalog (no Room/parsers required)

## Prerequisites

- macOS + Xcode 15+
- JDK 17+ to build the KMP framework

## Build shared framework

```bash
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
# device:
./gradlew :shared:linkDebugFrameworkIosArm64
```

Typical output:

```text
shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework
```

## Wire into Xcode

1. Open `iosApp/Miyo.xcodeproj` (or recreate App target and add `Miyo/` sources).
2. Link `shared.framework` (static — link only).
3. Framework Search Paths → Gradle output dir.
4. In `MiyoApp.swift` / `DemoStore.bootstrap()`:

```swift
import shared

MiyoIosBootstrap.shared.start()
let items = MiyoShared.shared.library()
```

5. Map Kotlin `Manga` → Swift UI models (or use generated types directly).

## What works today

| Layer | iOS |
|-------|-----|
| Project shape / framework targets | Yes |
| Platform actuals (HTTP, prefs, FS, …) | Yes (basic) |
| In-memory library / favorites / history | Yes (shared) |
| SwiftUI tabs demo | Yes (demo data until framework linked) |
| Remote sources / plugins | No (Android) |
| Full reader parity | No |

## Next

- Link framework and delete `DemoStore` sample arrays
- SQLDelight for durable iOS storage
- Real networking + source layer for iOS
