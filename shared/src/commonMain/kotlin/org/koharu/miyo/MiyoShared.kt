package org.koharu.miyo

import org.koharu.miyo.core.di.expect.Platform
import org.koharu.miyo.core.di.expect.initializePlatform
import org.koharu.miyo.core.nativeio.PlatformNativeImage
import org.koharu.miyo.core.nativeio.PlatformNativeZip

/**
 * Small stable entry surface for host apps (Android shell, iOS app) and smoke tests.
 * Prefer feature-specific APIs for production work.
 */
object MiyoShared {
	const val SHARED_API_VERSION: Int = 1

	fun initialize() {
		initializePlatform()
	}

	fun hello(): String = "Miyo shared KMP ready on ${platformName()}"

	fun platformName(): String = Platform.name

	fun platformVersion(): String = Platform.version

	fun isDebug(): Boolean = Platform.isDebug

	fun nativeImageAvailable(): Boolean = PlatformNativeImage.isAvailable

	fun nativeZipAvailable(): Boolean = PlatformNativeZip.isAvailable
}
