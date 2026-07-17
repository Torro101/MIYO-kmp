package org.koharu.miyo.core.di.expect

import okio.Path
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.UIKit.UIImage
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents

actual class ImageLoader {
	@OptIn(ExperimentalForeignApi::class)
	actual suspend fun loadImage(url: String): ImageResult {
		return try {
			val nsUrl = NSURL(string = url)
			val data = NSData.dataWithContentsOfURL(nsUrl)
			if (data != null) {
				val image = UIImage(data = data)
				if (image != null) {
					val size = image.size.useContents { Pair(width, height) }
					ImageResult(
						isSuccess = true,
						error = null,
						width = size.first.toInt(),
						height = size.second.toInt()
					)
				} else {
					ImageResult(
						isSuccess = false,
						error = "Failed to create image from data",
						width = 0,
						height = 0
					)
				}
			} else {
				ImageResult(
					isSuccess = false,
					error = "Failed to load data from URL",
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
			val nsUrl = NSURL(fileURLWithPath = path.toString())
			val data = NSData.dataWithContentsOfURL(nsUrl)
			if (data != null) {
				val image = UIImage(data = data)
				if (image != null) {
					val size = image.size.useContents { Pair(width, height) }
					ImageResult(
						isSuccess = true,
						error = null,
						width = size.first.toInt(),
						height = size.second.toInt()
					)
				} else {
					ImageResult(
						isSuccess = false,
						error = "Failed to create image from file",
						width = 0,
						height = 0
					)
				}
			} else {
				ImageResult(
					isSuccess = false,
					error = "Failed to load file",
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
		// iOS cache clearing would be implemented here
		// For now, this is a no-op
	}
}

actual class ImageResult actual constructor(
	actual val isSuccess: Boolean,
	actual val error: String?,
	actual val width: Int,
	actual val height: Int,
)

actual fun createImageLoader(): ImageLoader {
	return ImageLoader()
}
