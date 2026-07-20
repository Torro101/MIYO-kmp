# KMP migration snapshot (push target)

## Structure
- `:shared` KMP — commonMain + androidMain + iosMain
- `:app` thin Android shell
- `iosApp/` SwiftUI host shell

## Counts (approx)
- commonMain: portable domain/prefs/list models/expect APIs
- androidMain: Views/XML UI, Room, Hilt, parsers, JNI
- iosMain: platform actuals

## Notes
- Full UI is not Compose Multiplatform; Android UI stays in androidMain.
- Native C++/JNI is Android-only; iOS uses stubs for native facades.
- Do not run secrets in git; revoke any PAT posted in chat.
