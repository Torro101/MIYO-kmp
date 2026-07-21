package org.koharu.miyo.core.di.expect

import okio.Path

actual class ImageLoader {
	actual suspend fun loadImage(url: String): ImageResult =
		ImageResult(false, "stub", 0, 0)

	actual suspend fun loadFile(path: Path): ImageResult =
		ImageResult(false, "stub", 0, 0)

	actual fun clearCache() = Unit
}

actual class ImageResult(
	actual val isSuccess: Boolean,
	actual val error: String?,
	actual val width: Int,
	actual val height: Int,
)

actual fun createImageLoader(): ImageLoader = ImageLoader()
