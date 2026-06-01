package org.koharu.miyo.reader.domain

import android.content.Context
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import org.koharu.miyo.core.util.ext.printStackTraceDebug
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.math.roundToInt

@Reusable
class BundledImageRefinementModel @Inject constructor(
	@ApplicationContext private val context: Context,
) {

	val profile: Profile by lazy(LazyThreadSafetyMode.PUBLICATION) {
		loadBundledProfile() ?: Profile()
	}

	private val externalProfiles = ConcurrentHashMap<String, Profile>()

	fun profileFor(modelId: String): Profile {
		val safeModelId = modelId.takeIf { it in MODEL_IDS } ?: DEFAULT_MODEL_ID
		if (safeModelId == DEFAULT_MODEL_ID) {
			return profile
		}
		return externalProfiles.getOrPut(safeModelId) {
			loadExternalProfile(safeModelId) ?: profile
		}
	}

	private fun loadBundledProfile(): Profile? {
		return try {
			context.assets.open(DEFAULT_ASSET_PATH).bufferedReader().use { reader ->
				JSONObject(reader.readText()).toProfile()
			}
		} catch (e: Exception) {
			e.printStackTraceDebug()
			null
		}
	}

	private fun loadExternalProfile(modelId: String): Profile? {
		return try {
			val file = File(context.filesDir, "$EXTERNAL_MODEL_DIR/$modelId.json")
			if (!file.isFile || file.length() == 0L) {
				return null
			}
			file.bufferedReader().use { reader ->
				JSONObject(reader.readText()).toProfile().takeIf { it.id == modelId }
			}
		} catch (e: Exception) {
			e.printStackTraceDebug()
			null
		}
	}

	private fun JSONObject.toProfile(): Profile {
		return Profile(
			id = optString("id", Profile().id).ifBlank { Profile().id },
			version = optIntInRange("version", Profile().version, 1, 10_000),
			targetLongSidePx = optIntInRange("targetLongSidePx", Profile().targetLongSidePx, 512, 4096),
			minUpscaleFactor = optFloatInRange("minUpscaleFactor", Profile().minUpscaleFactor, 1f, 2f),
			maxUpscaleFactor = optFloatInRange("maxUpscaleFactor", Profile().maxUpscaleFactor, 1f, 3f),
			contrast = optFloatInRange("contrast", Profile().contrast, 0.5f, 1.5f),
			brightness = optFloatInRange("brightness", Profile().brightness, -32f, 32f),
			saturation = optFloatInRange("saturation", Profile().saturation, 0.5f, 1.5f),
			sharpen = optFloatInRange("sharpen", Profile().sharpen, 0f, 1f),
			jpegQuality = optIntInRange("jpegQuality", Profile().jpegQuality, 72, 95),
			maxOutputSizeRatio = optFloatInRange("maxOutputSizeRatio", Profile().maxOutputSizeRatio, 0.75f, 1.5f),
			minSourcePixels = optLongInRange("minSourcePixels", Profile().minSourcePixels, 1L, 1_000_000L),
			maxSourcePixels = optLongInRange("maxSourcePixels", Profile().maxSourcePixels, 1_000_000L, 64_000_000L),
			maxOutputPixels = optLongInRange("maxOutputPixels", Profile().maxOutputPixels, 1_000_000L, 64_000_000L),
			maxWorkingMemoryMb = optLongInRange("maxWorkingMemoryMb", Profile().maxWorkingMemoryMb, 64L, 1024L),
		).normalized()
	}

	private fun Profile.normalized(): Profile {
		return if (minUpscaleFactor <= maxUpscaleFactor && minSourcePixels <= maxSourcePixels) {
			this
		} else {
			Profile()
		}
	}

	private fun JSONObject.optFloatInRange(name: String, fallback: Float, min: Float, max: Float): Float {
		if (!has(name)) return fallback
		val value = optDouble(name, fallback.toDouble()).toFloat()
		return if (value in min..max) value else fallback
	}

	private fun JSONObject.optIntInRange(name: String, fallback: Int, min: Int, max: Int): Int {
		if (!has(name)) return fallback
		val value = optDouble(name, fallback.toDouble()).roundToInt()
		return if (value in min..max) value else fallback
	}

	private fun JSONObject.optLongInRange(name: String, fallback: Long, min: Long, max: Long): Long {
		if (!has(name)) return fallback
		val value = optLong(name, fallback)
		return if (value in min..max) value else fallback
	}

	data class Profile(
		val id: String = "general-x4v3",
		val version: Int = 1,
		val targetLongSidePx: Int = 1800,
		val minUpscaleFactor: Float = 1.08f,
		val maxUpscaleFactor: Float = 1.35f,
		val contrast: Float = 1.07f,
		val brightness: Float = 3f,
		val saturation: Float = 1.04f,
		val sharpen: Float = 0.3f,
		val jpegQuality: Int = 88,
		val maxOutputSizeRatio: Float = 1.15f,
		val minSourcePixels: Long = 64L * 64L,
		val maxSourcePixels: Long = 18_000_000L,
		val maxOutputPixels: Long = 20_000_000L,
		val maxWorkingMemoryMb: Long = 256L,
	) {
		val cacheKey: String
			get() = "$id:v$version"
	}

	companion object {
		const val DEFAULT_MODEL_ID = "general-x4v3"
		const val DEFAULT_ASSET_PATH = "models/general-x4v3.json"
		const val EXTERNAL_MODEL_DIR = "refinement-models"
		val MODEL_IDS = setOf(
			DEFAULT_MODEL_ID,
			"fast-x2v1",
			"sharp-x4v1",
			"webtoon-x4v1",
		)
	}
}
