package org.koharu.miyo.reader.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.core.net.toFile
import androidx.core.net.toUri
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import okio.source
import org.koharu.miyo.core.image.BitmapDecoderCompat
import org.koharu.miyo.core.nativeio.NativeImageEnhancer
import org.koharu.miyo.core.nativeio.NativeImageProbe
import org.koharu.miyo.core.prefs.AppSettings
import org.koharu.miyo.core.util.FileSize
import org.koharu.miyo.core.util.MimeTypes
import org.koharu.miyo.core.util.ext.MimeType
import org.koharu.miyo.core.util.ext.isFileUri
import org.koharu.miyo.core.util.ext.isImage
import org.koharu.miyo.core.util.ext.printStackTraceDebug
import org.koharu.miyo.core.util.ext.ramAvailable
import org.koharu.miyo.core.util.ext.toMimeTypeOrNull
import org.koharu.miyo.local.data.LocalStorageCache
import org.koharu.miyo.local.data.PageCache
import org.koitharu.kotatsu.parsers.util.runCatchingCancellable
import java.io.File
import java.util.UUID
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@Reusable
class ImageEnhancementProcessor @Inject constructor(
	@ApplicationContext private val context: Context,
	@PageCache private val cache: LocalStorageCache,
	private val settings: AppSettings,
	private val nativeImageProbe: NativeImageProbe,
	private val nativeImageEnhancer: NativeImageEnhancer,
	private val bundledModel: BundledImageRefinementModel,
) {

	suspend fun enhanceForReader(sourceUri: android.net.Uri, stableKey: String): android.net.Uri {
		if (!settings.isReaderImageEnhancementEnabled || !sourceUri.isFileUri()) {
			return sourceUri
		}
		val sourceFile = sourceUri.toFile()
		if (!sourceFile.isReadableImage()) {
			return sourceUri
		}
		val cacheKey = buildCacheKey("reader", stableKey, sourceFile)
		cache[cacheKey]?.let { cached ->
			if (cached.isReadableImage()) {
				return cached.toUri()
			}
			cache.remove(cacheKey)
		}
		val enhancedImage = createEnhancedTempImage(
			sourceFile = sourceFile,
			destination = sourceFile.parentFile ?: context.cacheDir,
			allowFormatChange = true,
		)
			?: return sourceUri
		return try {
			val cached = enhancedImage.file.source().use { source ->
				cache.set(cacheKey, source, enhancedImage.mimeType)
			}
			if (cached.isReadableImage()) {
				cached.toUri()
			} else {
				cache.remove(cacheKey)
				sourceUri
			}
		} catch (e: Exception) {
			e.printStackTraceDebug()
			sourceUri
		} finally {
			enhancedImage.file.delete()
		}
	}

	suspend fun enhanceForDownload(sourceFile: File, destination: File): File? {
		if (!settings.isDownloadImageEnhancementEnabled || !sourceFile.isReadableImage()) {
			return null
		}
		return createEnhancedTempImage(sourceFile, destination, allowFormatChange = true)?.file
	}

	suspend fun refineLocalImage(sourceFile: File, destination: File): File? {
		return createEnhancedTempImage(sourceFile, destination, allowFormatChange = false)?.file
	}

	fun isReadableImageFile(file: File): Boolean = file.isReadableImage()

	private suspend fun createEnhancedTempImage(
		sourceFile: File,
		destination: File,
		allowFormatChange: Boolean,
	): EnhancedImage? = withContext(Dispatchers.IO) {
		val tempFile = File(destination, "${UUID.randomUUID()}.enhanced.tmp")
		try {
			val encoding = runInterruptible {
				enhanceToFile(sourceFile, tempFile, allowFormatChange)
			}
			if (encoding != null && tempFile.isReadableImage()) {
				EnhancedImage(tempFile, encoding.mimeType)
			} else {
				tempFile.delete()
				null
			}
		} catch (e: Exception) {
			e.printStackTraceDebug()
			tempFile.delete()
			null
		}
	}

	private fun enhanceToFile(sourceFile: File, outputFile: File, allowFormatChange: Boolean): OutputEncoding? {
		val sourceMimeType = detectMimeType(sourceFile) ?: return null
		val encodings = sourceMimeType.outputEncodings(allowFormatChange)
		if (encodings.isEmpty()) {
			return null
		}
		val bounds = readBounds(sourceFile) ?: return null
		if (!bounds.hasUsefulSize() || !hasMemoryBudget(bounds.width, bounds.height)) {
			return null
		}
		val sourceBitmap = BitmapFactory.decodeFile(
			sourceFile.absolutePath,
			BitmapFactory.Options().apply {
				inPreferredConfig = Bitmap.Config.ARGB_8888
			},
		) ?: return null
		var scaledBitmap: Bitmap? = null
		var enhancedBitmap: Bitmap? = null
		return try {
			val workingBitmap = sourceBitmap.scaleIfUseful().also {
				if (it !== sourceBitmap) {
					scaledBitmap = it
				}
			}
			enhancedBitmap = workingBitmap.applyMildEnhancement()
			val resultBitmap = enhancedBitmap ?: return null
			val maxOutputBytes = sourceFile.maxEnhancedOutputBytes()
			for (encoding in encodings) {
				outputFile.delete()
				val success = outputFile.outputStream().use { output ->
					resultBitmap.compress(encoding.format, encoding.quality, output)
				}
				if (success && outputFile.isFile && outputFile.length() in 1L..maxOutputBytes) {
					return encoding
				}
			}
			outputFile.delete()
			null
		} finally {
			enhancedBitmap?.recycle()
			scaledBitmap?.recycle()
			sourceBitmap.recycle()
		}
	}

	private fun detectMimeType(file: File): MimeType? {
		val nativeInfo = if (nativeImageProbe.isAvailable) {
			nativeImageProbe.probe(file)
		} else {
			null
		}
		if (nativeInfo?.isCorrupt == true) {
			return null
		}
		return nativeInfo?.mimeType?.toMimeTypeOrNull()
			?: MimeTypes.probeMimeType(file)
			?: BitmapDecoderCompat.probeMimeType(file)
	}

	private fun readBounds(file: File): ImageBounds? {
		val nativeInfo = if (nativeImageProbe.isAvailable) {
			nativeImageProbe.probe(file)
		} else {
			null
		}
		if (nativeInfo?.isCorrupt == true) {
			return null
		}
		if (nativeInfo != null && nativeInfo.width > 0 && nativeInfo.height > 0) {
			return ImageBounds(nativeInfo.width, nativeInfo.height)
		}
		val options = BitmapFactory.Options().apply {
			inJustDecodeBounds = true
		}
		BitmapFactory.decodeFile(file.absolutePath, options)?.recycle()
		return ImageBounds(options.outWidth, options.outHeight)
	}

	private fun activeProfile(): BundledImageRefinementModel.Profile {
		return bundledModel.profileFor(settings.imageEnhancementModelId)
	}

	private fun ImageBounds.hasUsefulSize(): Boolean {
		val model = activeProfile()
		val pixels = width.toLong() * height.toLong()
		return width > 0 && height > 0 && pixels in model.minSourcePixels..model.maxSourcePixels
	}

	private fun hasMemoryBudget(width: Int, height: Int): Boolean {
		val model = activeProfile()
		val pixels = width.toLong() * height.toLong()
		val requiredBytes = pixels * BYTES_PER_ARGB_PIXEL * WORKING_BITMAP_COUNT
		val availableRam = context.ramAvailable
		val ramLimit = if (availableRam > 0L) availableRam / 2L else Long.MAX_VALUE
		val maxAllowed = minOf(
			FileSize.MEGABYTES.convert(model.maxWorkingMemoryMb, FileSize.BYTES),
			ramLimit,
		)
		return requiredBytes <= maxAllowed
	}

	private fun Bitmap.scaleIfUseful(): Bitmap {
		val model = activeProfile()
		val maxSide = maxOf(width, height)
		if (maxSide >= model.targetLongSidePx) {
			return this
		}
		val scale = (model.targetLongSidePx.toFloat() / maxSide.toFloat()).coerceAtMost(model.maxUpscaleFactor)
		if (scale <= model.minUpscaleFactor) {
			return this
		}
		val scaledWidth = (width * scale).roundToInt().coerceAtLeast(1)
		val scaledHeight = (height * scale).roundToInt().coerceAtLeast(1)
		val scaledPixels = scaledWidth.toLong() * scaledHeight.toLong()
		if (scaledPixels > model.maxOutputPixels || !hasMemoryBudget(scaledWidth, scaledHeight)) {
			return this
		}
		return Bitmap.createScaledBitmap(this, scaledWidth, scaledHeight, true)
	}

	private fun Bitmap.applyMildEnhancement(): Bitmap {
		val model = activeProfile()
		val result = copy(Bitmap.Config.ARGB_8888, true)
		if (nativeImageEnhancer.enhance(
				bitmap = result,
				contrast = model.contrast,
				brightness = model.brightness,
				saturation = model.saturation,
				sharpen = model.sharpen,
			)
		) {
			return result
		}
		val translate = ((1f - model.contrast) * 128f) + model.brightness
		val contrastMatrix = ColorMatrix(
			floatArrayOf(
				model.contrast, 0f, 0f, 0f, translate,
				0f, model.contrast, 0f, 0f, translate,
				0f, 0f, model.contrast, 0f, translate,
				0f, 0f, 0f, 1f, 0f,
			),
		)
		val saturationMatrix = ColorMatrix().apply {
			setSaturation(model.saturation)
		}
		contrastMatrix.postConcat(saturationMatrix)
		val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG).apply {
			colorFilter = ColorMatrixColorFilter(contrastMatrix)
		}
		Canvas(result).drawBitmap(this, 0f, 0f, paint)
		return result
	}

	private fun File.isReadableImage(): Boolean = runCatchingCancellable {
		if (!isFile || length() == 0L) {
			return@runCatchingCancellable false
		}
		val nativeInfo = if (nativeImageProbe.isAvailable) {
			nativeImageProbe.probe(this)
		} else {
			null
		}
		if (nativeInfo?.isCorrupt == true) {
			return@runCatchingCancellable false
		}
		val detectedType = nativeInfo?.mimeType?.toMimeTypeOrNull()
			?: MimeTypes.probeMimeType(this)
			?: BitmapDecoderCompat.probeMimeType(this)
		detectedType?.isImage == true
	}.getOrDefault(false)

	private fun buildCacheKey(namespace: String, stableKey: String, sourceFile: File): String {
		val modelKey = activeProfile().cacheKey
		return "$CACHE_PREFIX:$namespace:$CACHE_VERSION:$modelKey:$stableKey:" +
			"${sourceFile.absolutePath}:${sourceFile.length()}:${sourceFile.lastModified()}"
	}

	private fun MimeType.outputEncodings(allowFormatChange: Boolean): List<OutputEncoding> {
		return when (toString()) {
			"image/jpeg", "image/jpg" -> jpegEncodings()
			"image/png" -> if (allowFormatChange) jpegEncodings() else listOf(pngEncoding())
			else -> if (allowFormatChange) jpegEncodings() else emptyList()
		}
	}

	private fun pngEncoding() = OutputEncoding(Bitmap.CompressFormat.PNG, PNG_QUALITY, MIME_TYPE_PNG)

	private fun jpegEncodings(): List<OutputEncoding> {
		val quality = activeProfile().jpegQuality
		return listOf(
			OutputEncoding(Bitmap.CompressFormat.JPEG, quality, MIME_TYPE_JPEG),
			OutputEncoding(Bitmap.CompressFormat.JPEG, (quality - 6).coerceAtLeast(MIN_JPEG_QUALITY), MIME_TYPE_JPEG),
			OutputEncoding(Bitmap.CompressFormat.JPEG, (quality - 12).coerceAtLeast(MIN_JPEG_QUALITY), MIME_TYPE_JPEG),
		).distinctBy { it.quality }
	}

	private fun File.maxEnhancedOutputBytes(): Long {
		val sourceSize = length().coerceAtLeast(MIN_OUTPUT_BYTES)
		val profileLimit = (sourceSize.toDouble() * activeProfile().maxOutputSizeRatio.toDouble()).roundToLong()
		return profileLimit.coerceAtLeast(MIN_OUTPUT_BYTES)
	}

	private data class ImageBounds(
		val width: Int,
		val height: Int,
	)

	private data class EnhancedImage(
		val file: File,
		val mimeType: MimeType,
	)

	private data class OutputEncoding(
		val format: Bitmap.CompressFormat,
		val quality: Int,
		val mimeType: MimeType,
	)

	private companion object {
		private const val CACHE_PREFIX = "image-enhancement"
		private const val CACHE_VERSION = 2
		private const val PNG_QUALITY = 100
		private val MIME_TYPE_PNG = MimeType("image/png")
		private val MIME_TYPE_JPEG = MimeType("image/jpeg")
		private const val MIN_JPEG_QUALITY = 76
		private const val MIN_OUTPUT_BYTES = 32L * 1024L
		private const val BYTES_PER_ARGB_PIXEL = 4L
		private const val WORKING_BITMAP_COUNT = 3L
	}
}
