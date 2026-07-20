# Consumer ProGuard rules for :shared (merged into app release builds)

-keep class org.koharu.miyo.MiyoShared { *; }
-keep class org.koharu.miyo.core.nativeio.** { *; }
-keepclasseswithmembernames class * {
    native <methods>;
}
