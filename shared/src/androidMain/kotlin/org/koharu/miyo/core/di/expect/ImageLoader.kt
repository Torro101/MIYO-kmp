package org.koharu.miyo.core.di.expect

import android.content.Context
import android.graphics.BitmapFactory
import coil3.ImageLoader as CoilImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import okio.Path
import org.koharu.miyo.core.os.AndroidContextHolder
import java.io.File

actual class ImageLoader(private val context: Context) {
	private val loader = CoilImageLoader.Builder(context)
		.allowHardware(true)
		.build()

	actual suspend fun loadImage(url: String): ImageResult {
		return try {
			val request = ImageRequest.Builder(context)
				.data(url)
				.allowHardware(true)
				.build()
			val result = loader.execute(request)
			if (result is SuccessResult) {
				val bitmap = result.image.toBitmap()
				ImageResult(true, null, bitmap.width, bitmap.height)
			} else {
				ImageResult(false, "Failed to load image", 0, 0)
			}
		} catch (e: Exception) {
			ImageResult(false, e.message, 0, 0)
		}
	}

	actual suspend fun loadFile(path: Path): ImageResult {
		return try {
			val file = File(path.toString())
			val bitmap = BitmapFactory.decodeFile(file.absolutePath)
			if (bitmap != null) {
				ImageResult(true, null, bitmap.width, bitmap.height)
			} else {
				ImageResult(false, "Failed to decode image", 0, 0)
			}
		} catch (e: Exception) {
			ImageResult(false, e.message, 0, 0)
		}
	}

	actual fun clearCache() {
		loader.memoryCache?.clear()
	}
}

actual class ImageResult(
	actual val isSuccess: Boolean,
	actual val error: String?,
	actual val width: Int,
	actual val height: Int,
)

actual fun createImageLoader(): ImageLoader =
	ImageLoader(AndroidContextHolder.applicationContext)
