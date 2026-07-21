package org.koharu.miyo.core.di.expect

actual object Platform {
	actual val name: String = "iOS"
	actual val version: String = "unknown"
	actual val isDebug: Boolean = true
}

actual fun initializePlatform() = Unit
