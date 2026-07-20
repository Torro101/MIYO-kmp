# KMP mass-migration pass notes

## Strategy

1. **commonMain** = only code that compiles on all targets (no Android/JVM-only APIs).
2. **androidMain** = Views/XML UI, Room, Hilt, parsers (kotatsu), JNI, Parcelable models.
3. **iosMain** = platform actuals + stubs for native helpers.
4. Accidentally-migrated Android-coupled files were **moved back** to androidMain (~79 files).
5. Portable domain types were **added** to commonMain (progress modes, download format, history snapshot, chapter flags, reader position, scrobbler ids, stats periods, diff payloads).

## Counts (after this pass)

| Set | .kt files |
|-----|-----------|
| commonMain | ~165 |
| androidMain | ~1024 |
| iosMain | 13 |
| app | 21 |

## Hygiene results

- commonMain android/kotatsu/jsoup/R leaks: **0**
- common∩android FQCN collisions (non-expect/actual): **0**
- expect APIs with android+ios actuals: **13**

## Mass-migration follow-up

- Removed broken `koitharu/` stub tree from commonMain (relocated `MangaParserSource` stub under android `org/koitharu/...`).
- Moved pure prefs enums to commonMain: ListMode, ReaderAnimation, ReaderMode (prefs), ScreenshotsPolicy, TrackerDownloadStrategy, TriStateOption, NetworkPolicy, ReaderBackground.
- Android keeps platform extensions: `NetworkPolicy.isNetworkAllowed`, `ReaderBackground.resolve/isLight`.
- List UI models without androidx annotations moved to common: EmptyState/Hint, ErrorState, ButtonFooter, TipModel, InfoModel.
- Split `ReversibleHandle` (common fun interface + android `reverseAsync`).
- Added `ModelBridges`, `HistoryMappers` (HistorySnapshot ↔ MangaHistory, ReaderPosition ↔ ReaderState).
- `ReadingTime` bridges to `ReadingTimeEstimate`.

## Android enums deleted (now common)

- `ProgressIndicatorMode`
- `DownloadFormat`

Android still owns resource-backed `ListSortOrder`, `ScrobblerService`, `StatsPeriod`, Parcelable `MangaHistory` / `ReaderState` / `ReaderPage`.
