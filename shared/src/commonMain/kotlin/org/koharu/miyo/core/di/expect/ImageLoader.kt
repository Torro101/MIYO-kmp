package org.koharu.miyo.core.di.expect

import okio.Path

/**
 * Platform-agnostic image loading interface.
 * Android: backed by Coil
 * iOS: backed by Kingfisher or custom implementation
 */
expect class ImageLoader {
	suspend fun loadImage(url: String): ImageResult
	suspend fun loadFile(path: Path): ImageResult
	fun clearCache()
}

expect class ImageResult {
	val isSuccess: Boolean
	val error: String?
	val width: Int
	val height: Int
}

expect fun createImageLoader(): ImageLoader
