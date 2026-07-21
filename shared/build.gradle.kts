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

			implementation(libs.okhttp)
			implementation(libs.okhttp.tls)
			implementation(libs.okhttp.dnsoverhttps)
			implementation(libs.okhttp.logging)

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

			implementation(libs.lifecycle.viewmodel)
			implementation(libs.lifecycle.service)
			implementation(libs.lifecycle.process)
			implementation(libs.androidx.lifecycle.common.java8)

			implementation(libs.material)

			implementation(libs.androidx.room.runtime)
			implementation(libs.androidx.room.ktx)

			implementation(libs.hilt.android)
			implementation(libs.androidx.hilt.work)

			implementation(libs.coil.core)
			implementation(libs.coil.network)
			implementation(libs.coil.gif)
			implementation(libs.coil.svg)
			implementation(libs.avif.decoder)
			implementation(libs.ssiv)

			implementation(libs.androidx.work.runtime)
			implementation(libs.androidx.window)
			implementation(libs.conscrypt.android)
			implementation(libs.disk.lru.cache)
			implementation(libs.markwon)
			implementation(libs.kizzyrpc)
			implementation(libs.acra.http)
			implementation(libs.acra.dialog)
			implementation(libs.moshi.kotlin)
			implementation(libs.adapterdelegates)
			implementation(libs.adapterdelegates.viewbinding)
			implementation(libs.kotlin.stdlib)
			coreLibraryDesugaring(libs.desugar.jdk.libs)

			implementation(files("${rootProject.projectDir}/app/libs/keiyoushi-extensions-lib.aar"))
			implementation(libs.rxjava)
			implementation(libs.injekt.core)
			implementation(libs.quickjs)
		}

		iosMain.dependencies {
			implementation(libs.kotlinx.coroutines.core)
			implementation(libs.okio)
		}
	}
}

// KSP targets for KMP android source set
dependencies {
	add("kspAndroid", libs.androidx.room.compiler)
	add("kspAndroid", libs.hilt.compiler)
	add("kspAndroid", libs.androidx.hilt.compiler)
}

android {
	// Distinct from :app namespace to avoid AGP R/BuildConfig clashes.
	namespace = "org.koharu.miyo.shared"
	compileSdk = 36
	ndkVersion = "27.0.12077973"
	defaultConfig {
		minSdk = 21
		externalNativeBuild {
			cmake {
				cppFlags += listOf("-std=c++20", "-Ofast")
				arguments += "-DANDROID_STL=c++_shared"
			}
		}
		ndk {
			abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
		}
		consumerProguardFiles("consumer-rules.pro")
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
				"DebugProbesKt.bin",
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
		abortOnError = false
		checkReleaseBuilds = false
		disable += listOf(
			"MissingTranslation", "PrivateResource", "SetJavaScriptEnabled", "SimpleDateFormat",
		)
	}
	buildFeatures {
		buildConfig = true
		viewBinding = true
	}
}

room {
	schemaDirectory("$projectDir/schemas")
}
