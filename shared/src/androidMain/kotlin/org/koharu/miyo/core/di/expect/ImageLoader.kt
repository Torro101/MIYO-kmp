package org.koharu.miyo.core.di.expect

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import okio.Path
import okio.Path.Companion.toPath
import java.io.File

actual class ImageLoader(private val context: Context) {
	private val loader = ImageLoader.Builder(context)
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
				ImageResult(
					isSuccess = true,
					error = null,
					width = bitmap.width,
					height = bitmap.height
				)
			} else {
				ImageResult(
					isSuccess = false,
					error = "Failed to load image",
					width = 0,
					height = 0
				)
			}
		} catch (e: Exception) {
			ImageResult(
				isSuccess = false,
				error = e.message,
				width = 0,
				height = 0
			)
		}
	}

	actual suspend fun loadFile(path: Path): ImageResult {
		return try {
			val file = File(path.toString())
			val bitmap = BitmapFactory.decodeFile(file.absolutePath)
			if (bitmap != null) {
				ImageResult(
					isSuccess = true,
					error = null,
					width = bitmap.width,
					height = bitmap.height
				)
			} else {
				ImageResult(
					isSuccess = false,
					error = "Failed to decode image",
					width = 0,
					height = 0
				)
			}
		} catch (e: Exception) {
			ImageResult(
				isSuccess = false,
				error = e.message,
				width = 0,
				height = 0
			)
		}
	}

	actual fun clearCache() {
		loader.memoryCache?.clear()
	}
}

actual class ImageResult actual constructor(
	actual val isSuccess: Boolean,
	actual val error: String?,
	actual val width: Int,
	actual val height: Int,
)

actual fun createImageLoader(): ImageLoader {
	return ImageLoader(org.koharu.miyo.core.os.AndroidContextHolder.applicationContext)
}
