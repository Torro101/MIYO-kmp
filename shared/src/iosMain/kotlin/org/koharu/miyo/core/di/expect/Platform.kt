package org.koharu.miyo.core.di.expect

import platform.Foundation.NSProcessInfo
import platform.UIKit.UIDevice
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform as NativePlatform

actual object Platform {
	actual val name: String = "iOS"
	actual val version: String =
		runCatching { UIDevice.currentDevice.systemVersion }.getOrNull()
			?: runCatching { NSProcessInfo.processInfo.operatingSystemVersionString }.getOrNull()
			?: "unknown"

	@OptIn(ExperimentalNativeApi::class)
	actual val isDebug: Boolean = NativePlatform.isDebugBinary
}

actual fun initializePlatform() = Unit
