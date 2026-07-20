package org.koharu.miyo.core.nativeio

/**
 * iOS has no miyo-native JNI library. Callers must use pure-Kotlin / platform image APIs.
 */
actual object PlatformNativeImage {
	actual val isAvailable: Boolean = false

	actual fun probeMime(path: String): String? = null

	actual fun probeDimensions(path: String): Triple<Int, Int, Boolean>? = null
}

actual object PlatformNativeZip {
	actual val isAvailable: Boolean = false

	actual fun open(path: String, append: Boolean): Long = 0L

	actual fun close(handle: Long) = Unit

	actual fun finish(handle: Long): Boolean = false

	actual fun addDirectory(handle: Long, entryName: String): Boolean = false

	actual fun appendFile(handle: Long, entryName: String, sourcePath: String): Boolean = false
}
