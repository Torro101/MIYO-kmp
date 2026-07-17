package org.koharu.miyo.core.di.expect

import platform.Foundation.NSProcessInfo

actual object Platform {
	actual val name: String = "iOS"
	actual val version: String = NSProcessInfo.processInfo.operatingSystemVersionString
	actual val isDebug: Boolean = true // Will be set properly in production
}

actual fun initializePlatform() {
	// Platform-specific initialization will be done here
}
