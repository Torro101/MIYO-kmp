package org.koharu.miyo.core.di.expect

/**
 * Platform-specific information and initialization.
 */
expect object Platform {
	val name: String
	val version: String
	val isDebug: Boolean
}

expect fun initializePlatform()
