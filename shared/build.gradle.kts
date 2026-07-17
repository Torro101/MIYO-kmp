import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kmp)
	alias(libs.plugins.kotlinx.serizliation)
	alias(libs.plugins.ksp)
	alias(libs.plugins.hilt)
	alias(libs.plugins.room)
}

kotlin {
	androidTarget {
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_17)
		}
	}

	listOf(
		iosX64(),
		iosArm64(),
		iosSimulatorArm64(),
	).forEach {
		it.binaries.framework {
			baseName = "shared"
			isStatic = true
		}
	}

	sourceSets {
		commonMain.dependencies {
			implementation(libs.kotlinx.serialization.json)
			implementation(libs.kotlinx.coroutines.core)
			implementation(libs.okio)
		}

		androidMain.dependencies {
			implementation(libs.kotlinx.coroutines.android)
			implementation(libs.kotlinx.coroutines.guava)
			implementation(libs.json)
			implementation(libs.jsoup)

			// OkHttp
			implementation(libs.okhttp)
			implementation(libs.okhttp.tls)
			implementation(libs.okhttp.dnsoverhttps)
			implementation(libs.okhttp.logging)

			// AndroidX Core
			implementation(libs.androidx.appcompat)
			implementation(libs.androidx.core)
			implementation(libs.androidx.activity)
			implementation(libs.androidx.fragment)
			implementation(libs.androidx.constraintlayout)
			implementation(libs.androidx.recyclerview)
			implementation(libs.androidx.viewpager2)
			implementation(libs.androidx.swiperefreshlayout)
			implementation(libs.androidx.documentfile)
			implementation(libs.androidx.transition)
			implementation(libs.androidx.collection)
			implementation(libs.androidx.preference)
			implementation(libs.androidx.biometric)
			implementation(libs.androidx.webkit)

			// Lifecycle
			implementation(libs.lifecycle.viewmodel)
			implementation(libs.lifecycle.service)
			implementation(libs.lifecycle.process)
			implementation(libs.androidx.lifecycle.common.java8)

			// Material
			implementation(libs.material)

			// Room
			implementation(libs.androidx.room.runtime)
			implementation(libs.androidx.room.ktx)
			ksp(libs.androidx.room.compiler)

			// Hilt
			implementation(libs.hilt.android)
			ksp(libs.hilt.compiler)
			implementation(libs.androidx.hilt.work)
			ksp(libs.androidx.hilt.compiler)

			// Coil
			implementation(libs.coil.core)
			implementation(libs.coil.network)
			implementation(libs.coil.gif)
			implementation(libs.coil.svg)
			implementation(libs.avif.decoder)
			implementation(libs.ssiv)

			// Work Manager
			implementation(libs.androidx.work.runtime)

			// Window
			implementation(libs.androidx.window)

			// Network / Security
			implementation(libs.conscrypt.android)

			// Disk Cache
			implementation(libs.disk.lru.cache)

			// Markdown
			implementation(libs.markwon)

			// Discord RPC
			implementation(libs.kizzyrpc)

			// ACRA
			implementation(libs.acra.http)
			implementation(libs.acra.dialog)

			// Serialization
			implementation(libs.moshi.kotlin)

			// Adapter Delegates
			implementation(libs.adapterdelegates)
			implementation(libs.adapterdelegates.viewbinding)

			// Kotlin stdlib
			implementation(libs.kotlin.stdlib)

			// Core desugar
			coreLibraryDesugaring(libs.desugar.jdk.libs)

			// Keiyoushi extensions
			implementation(files("${rootProject.projectDir}/app/libs/keiyoushi-extensions-lib.aar"))
			implementation(libs.rxjava)
			implementation(libs.injekt.core)
			implementation(libs.quickjs)
		}

		iosMain.dependencies {
		}
	}
}

android {
	namespace = "org.koharu.miyo.shared"
	compileSdk = libs.versions.android.compileSdk.get().toInt()
	defaultConfig {
		minSdk = libs.versions.android.minSdk.get().toInt()
		externalNativeBuild {
			cmake {
				cppFlags += listOf("-std=c++20", "-Ofast")
				arguments += "-DANDROID_STL=c++_shared"
			}
		}
		ndk {
			abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
		}
	}
	compileOptions {
		isCoreLibraryDesugaringEnabled = true
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	externalNativeBuild {
		cmake {
			path = file("src/androidMain/cpp/CMakeLists.txt")
			version = "3.22.1"
		}
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
		getByName("main") {
			manifest.srcFile("src/androidMain/AndroidManifest.xml")
			res.srcDirs("src/androidMain/res")
			assets.srcDirs("src/androidMain/assets")
			jniLibs.srcDirs("src/androidMain/jniLibs")
			aidl.srcDirs("src/androidMain/aidl")
		}
	}
	lint {
		abortOnError = true
		disable += listOf(
			"MissingTranslation", "PrivateResource", "SetJavaScriptEnabled", "SimpleDateFormat"
		)
	}
}

room {
	schemaDirectory("$projectDir/schemas")
}
