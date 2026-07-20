import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin)
	alias(libs.plugins.ksp)
	alias(libs.plugins.hilt)
	alias(libs.plugins.room)
	alias(libs.plugins.kotlinx.serizliation)
	id("kotlin-parcelize")
}

val localProperties = java.util.Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
	localPropertiesFile.inputStream().use { localProperties.load(it) }
}

fun propertyOrDefault(name: String, defaultValue: String): String {
	val value = providers.gradleProperty(name).orNull
		?: localProperties.getProperty(name)
	return if (value.isNullOrBlank()) defaultValue else value
}

fun buildConfigString(value: String): String {
	val escaped = value
		.replace("\\", "\\\\")
		.replace("\"", "\\\"")
	return "\"$escaped\""
}

val appApplicationId = propertyOrDefault("app.applicationId", "org.koharu.miyo")
val appDisplayName = propertyOrDefault("app.displayName", "Miyo")
val appUpdatesRepo = propertyOrDefault("app.updatesRepo", "Torro101/MIYO")
val appSourceUrl = propertyOrDefault("app.sourceUrl", "https://github.com/$appUpdatesRepo")
val appIconUrl = propertyOrDefault(
	"app.iconUrl",
	"https://raw.githubusercontent.com/$appUpdatesRepo/refs/heads/main/.github/assets/icon.png"
)
val appReleaseCertSha256 = propertyOrDefault(
	"app.releaseCertSha256",
	"4C0BD9188836B7279DCF91123AC99DC4F54FE221FB66FB9203803D167E066FC9"
)
val releaseStoreFile = propertyOrDefault("release.storeFile", "")
val releaseStorePassword = propertyOrDefault("release.storePassword", "")
val releaseKeyAlias = propertyOrDefault("release.keyAlias", "")
val releaseKeyPassword = propertyOrDefault("release.keyPassword", "")
val hasReleaseSigning = listOf(
	releaseStoreFile, releaseStorePassword, releaseKeyAlias, releaseKeyPassword
).all { it.isNotBlank() }

android {
	compileSdk = 36
	buildToolsVersion = "36.1.0"
	namespace = "org.koharu.miyo"

	defaultConfig {
		applicationId = appApplicationId
		minSdk = 21
		targetSdk = 36
		versionCode = 13
		versionName = "0.0.6-beta"
		generatedDensities = emptySet()
		testInstrumentationRunner = "org.koharu.miyo.HiltTestRunner"
		ksp {
			arg("room.generateKotlin", "true")
		}
		androidResources {
			generateLocaleConfig = false
		}
		resValue("string", "app_name", appDisplayName)
		resValue("string", "url_github", appSourceUrl)
		resValue("string", "github_updates_repo", appUpdatesRepo)
		resValue("string", "app_icon_url", appIconUrl)
		resValue("string", "account_type_sync", "$appApplicationId.sync")
		resValue("string", "sync_authority_history", "$appApplicationId.history")
		resValue("string", "sync_authority_favourites", "$appApplicationId.favourites")
		resValue("string", "tg_backup_bot_token", localProperties.getProperty("tg_backup_bot_token", ""))
		buildConfigField("String", "UPDATES_REPO", buildConfigString(appUpdatesRepo))
		buildConfigField("String", "RELEASE_CERT_SHA256", buildConfigString(appReleaseCertSha256))
	}
	signingConfigs {
		if (hasReleaseSigning) {
			create("release") {
				storeFile = file(releaseStoreFile)
				storePassword = releaseStorePassword
				keyAlias = releaseKeyAlias
				keyPassword = releaseKeyPassword
			}
		}
	}
	buildTypes {
		debug {
			applicationIdSuffix = ".debug"
		}
		release {
			if (hasReleaseSigning) {
				signingConfig = signingConfigs.getByName("release")
			}
			isMinifyEnabled = true
			isShrinkResources = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	buildFeatures {
		viewBinding = true
		buildConfig = true
	}
	packaging {
		jniLibs {
			useLegacyPackaging = true
		}
		resources {
			excludes += listOf(
				"META-INF/README.md",
				"META-INF/NOTICE.md",
				"META-INF/LICENSE",
				"META-INF/LICENSE.md",
				"DebugProbesKt.bin"
			)
		}
	}
	sourceSets {
		getByName("androidTest").assets.srcDirs("schemas")
		getByName("main").java.srcDirs("src/main/kotlin/")
	}
	compileOptions {
		isCoreLibraryDesugaringEnabled = true
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	lint {
		abortOnError = true
		disable += listOf(
			"MissingTranslation", "PrivateResource", "SetJavaScriptEnabled", "SimpleDateFormat"
		)
	}
	// Native C++ lives in :shared (androidMain/cpp). App is a thin Android shell.
	testOptions {
		isIncludeAndroidResources = true
		isReturnDefaultValues = false
	}
}

kotlin {
	compilerOptions {
		jvmTarget.set(JvmTarget.JVM_17)
		freeCompilerArgs.addAll(
			"-opt-in=kotlin.ExperimentalStdlibApi",
			"-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
			"-opt-in=kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi",
			"-opt-in=kotlinx.coroutines.InternalForInheritanceCoroutinesApi",
			"-opt-in=kotlinx.coroutines.FlowPreview",
			"-opt-in=kotlin.contracts.ExperimentalContracts",
			"-opt-in=coil3.annotation.ExperimentalCoilApi",
			"-opt-in=coil3.annotation.InternalCoilApi",
			"-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
			"-Xjspecify-annotations=strict",
			"-Xannotation-default-target=first-only",
			"-Xtype-enhancement-improvements-strict-mode"
		)
	}
}

room {
	schemaDirectory("schemas")
}

dependencies {
	implementation(project(":shared"))

	implementation(libs.jsoup)
	implementation(libs.core.exts)
	coreLibraryDesugaring(libs.desugar.jdk.libs)
	implementation(libs.kotlin.stdlib)
	implementation(libs.kotlinx.coroutines.android)
	implementation(libs.kotlinx.coroutines.guava)

	implementation(libs.androidx.appcompat)
	implementation(libs.androidx.core)
	implementation(libs.androidx.activity)
	implementation(libs.androidx.fragment)
	implementation(libs.androidx.transition)
	implementation(libs.androidx.collection)
	implementation(libs.lifecycle.viewmodel)
	implementation(libs.lifecycle.service)
	implementation(libs.lifecycle.process)
	implementation(libs.androidx.constraintlayout)
	implementation(libs.androidx.documentfile)
	implementation(libs.androidx.swiperefreshlayout)
	implementation(libs.androidx.recyclerview)
	implementation(libs.androidx.viewpager2)
	implementation(libs.androidx.preference)
	implementation(libs.androidx.biometric)
	implementation(libs.material)
	implementation(libs.androidx.lifecycle.common.java8)
	implementation(libs.androidx.webkit)

	implementation(libs.androidx.work.runtime)
	implementation(libs.guava)

	implementation(libs.androidx.window)

	implementation(libs.androidx.room.runtime)
	implementation(libs.androidx.room.ktx)
	ksp(libs.androidx.room.compiler)

	implementation(libs.okhttp)
	implementation(libs.okhttp.tls)
	implementation(libs.okhttp.dnsoverhttps)
	implementation(libs.okio)
	implementation(libs.kotlinx.serialization.json)

	implementation(libs.adapterdelegates)
	implementation(libs.adapterdelegates.viewbinding)

	implementation(libs.hilt.android)
	ksp(libs.hilt.compiler)
	implementation(libs.androidx.hilt.work)
	ksp(libs.androidx.hilt.compiler)

	implementation(libs.coil.core)
	implementation(libs.coil.network)
	implementation(libs.coil.gif)
	implementation(libs.coil.svg)
	implementation(libs.avif.decoder)
	implementation(libs.ssiv)
	implementation(libs.disk.lru.cache)
	implementation(libs.markwon)
	implementation(libs.kizzyrpc)

	implementation(libs.acra.http)
	implementation(libs.acra.dialog)

	implementation(libs.conscrypt.android)

	debugImplementation(libs.leakcanary.android)
	debugImplementation(libs.workinspector)

	testImplementation(libs.junit)
	testImplementation(libs.json)
	testImplementation(libs.kotlinx.coroutines.test)

	androidTestImplementation(libs.androidx.runner)
	androidTestImplementation(libs.androidx.rules)
	androidTestImplementation(libs.androidx.test.core)
	androidTestImplementation(libs.androidx.junit)

	androidTestImplementation(libs.kotlinx.coroutines.test)

	androidTestImplementation(libs.androidx.room.testing)
	androidTestImplementation(libs.moshi.kotlin)

	androidTestImplementation(libs.hilt.android.testing)
	kspAndroidTest(libs.hilt.android.compiler)

	implementation(files("libs/keiyoushi-extensions-lib.aar"))
	implementation(libs.rxjava)
	implementation(libs.injekt.core)
	implementation(libs.quickjs)
}
