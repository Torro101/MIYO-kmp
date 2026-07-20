package org.koharu.miyo.core.nativeio

/**
 * Cross-platform facade over native image probing.
 *
 * Android: JNI (`miyo-native` / [org.koharu.miyo.core.nativeio.NativeImageProbe]).
 * iOS: stub — callers should fall back to pure Kotlin/decoder paths.
 */
expect object PlatformNativeImage {
	val isAvailable: Boolean

	/** MIME type guess, or null/empty if unknown or native unavailable. */
	fun probeMime(path: String): String?

	/**
	 * @return width, height, isCorrupt — or null if unavailable.
	 */
	fun probeDimensions(path: String): Triple<Int, Int, Boolean>?
}

/**
 * Cross-platform facade over native ZIP/CBZ writing.
 *
 * Android: JNI (`NativeZipWriter`).
 * iOS: stub — use pure Kotlin zip libraries instead.
 */
expect object PlatformNativeZip {
	val isAvailable: Boolean

	/** Opens a zip at [path]; returns opaque handle or 0 on failure / unsupported. */
	fun open(path: String, append: Boolean = false): Long

	fun close(handle: Long)

	fun finish(handle: Long): Boolean

	fun addDirectory(handle: Long, entryName: String): Boolean

	fun appendFile(handle: Long, entryName: String, sourcePath: String): Boolean
}
