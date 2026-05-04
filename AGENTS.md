# Repository Instructions

## Kotlin Fix Verification

After making a Kotlin fix, verify it without running Gradle, Java, Android SDK tools, Android Studio, `kotlinc`, or installing dependencies.

Use only file inspection and shell text tools such as `rg`, `sed`, and `ls`.

Verify:

1. The edited code matches the intended bug/fix path.
2. All affected call sites were searched with `rg`.
3. Imports, package names, renamed symbols, and function signatures are consistent.
4. No stale references to old names remain.
5. Nullability, coroutine/threading, lifecycle, and error handling are still correct.
6. Android resource IDs, manifest entries, navigation routes, DI bindings, or XML references affected by the change still line up.
7. The fix does not create obvious compile errors from missing imports, wrong types, bad override signatures, or unreachable variables.
8. Report what could not be verified without a real Gradle/CI build.

Useful static checks:

```bash
rg -n "ChangedClass|changedFunction|oldName|newName" /path/to/project
rg -n "TODO|FIXME|!!|lateinit|GlobalScope|runBlocking" /path/to/project/app/src
rg -n "R\\.id\\.|R\\.layout\\.|R\\.string\\.|android:name|nav_graph" /path/to/project/app/src
```

Key phrase: verify by static analysis and call-site tracing only; explicitly state remaining compile/build risk.

## Manifest And Integration Deep Fixes

Before starting a deep fix workflow, ask whether the user will actively oversee the process or wants autonomous progress. If the user already gave an explicit execution instruction, proceed while keeping updates concise.

For any edit that touches `AndroidManifest.xml`, Gradle dependency declarations, Android permissions, exported components, package visibility, foreground services, file/storage access, account/sync integration, navigation/deep links, app widgets, notifications, or feature integrations that might require manifest entries or new dependencies:

1. Inspect what the manifest or dependency currently declares.
2. Trace what the app actually uses with `rg`: permissions, APIs, component classes, services, receivers, providers, activities, intent actions, metadata, resource references, DI bindings, and call sites.
3. Compare declarations against usage. Remove broad or stale entries when a narrower declaration covers the real use case.
4. Check whether each relevant API, manifest attribute, permission, dependency version, or Gradle DSL is deprecated, replaced, restricted, or target-SDK-sensitive.
5. When the behavior is current-policy or version dependent, verify against current official documentation, release notes, or dependency changelogs before choosing the fix. Prefer official Android, AndroidX, Gradle, and library sources.
6. For new dependencies, confirm the app really needs them, check their minSdk/targetSdk impact, manifest merge impact, transitive permissions, package size/native library effects, and whether an existing dependency already provides the needed API.
7. For new manifest entries, confirm the matching code path exists and the entry is the narrowest correct declaration. For removed entries, confirm no code still depends on them.
8. Verify by static analysis and call-site tracing before finishing. Do not assume a manifest edit is correct just because it looks syntactically valid.

Useful manifest/integration checks:

```bash
rg -n "android:name|uses-permission|queries|provider|service|receiver|activity|meta-data|foregroundServiceType" /path/to/project/app/src/main/AndroidManifest.xml
rg -n "ChangedPermission|ChangedService|ChangedActivity|ChangedReceiver|ChangedProvider|ChangedAction|ChangedDependency" /path/to/project/app/src /path/to/project/app/build.gradle /path/to/project/gradle
rg -n "extractNativeLibs|QUERY_ALL_PACKAGES|requestLegacyExternalStorage|FOREGROUND_SERVICE|READ_EXTERNAL_STORAGE|WRITE_EXTERNAL_STORAGE|POST_NOTIFICATIONS" /path/to/project/app/src /path/to/project/app/build.gradle
```

## Work Boundaries

Do not blindly agree to a requested change when it is unsafe, impossible, deprecated, policy-incompatible, or unverifiable in the current environment. State the constraint clearly, propose the safest workable path, and ask for confirmation when external verification or a product decision is required.

If context limits or missing external access prevent a reliable finish, make a safe cut: stop before risky edits, summarize what is known, identify the exact unresolved risk, and ask the user for the next action or the needed verification source.

Before ending a pass, report:

1. What was changed.
2. What was verified locally by static inspection.
3. What could not be verified without Gradle, CI, Android SDK tooling, device testing, credentials, or external review.
