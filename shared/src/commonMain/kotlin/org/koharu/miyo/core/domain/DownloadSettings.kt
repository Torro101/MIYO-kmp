package org.koharu.miyo.core.domain

import kotlinx.serialization.Serializable

/**
 * Cross-platform download settings model.
 */
@Serializable
data class DownloadSettings(
	val isDownloadOnWifiOnly: Boolean = true,
	val maxConcurrentDownloads: Int = 3,
	val downloadNonWifiLimit: Int = 5,
	val isAutoDownloadEnabled: Boolean = false,
	val autoDownloadChapters: Int = 5,
	val isDeleteChaptersAfterReading: Boolean = false,
	val downloadDirectory: String = "downloads",
	val isCompressDownloads: Boolean = false,
	val downloadFormat: DownloadFormat = DownloadFormat.CHAPTER,
	val isAutoCleanupEnabled: Boolean = false,
	val cleanupDaysOld: Int = 30,
	val minStorageRequiredMb: Int = 100,
	val isHighPriorityDownloads: Boolean = false
) {
	val concurrentDownloads: Int
		get() = if (isDownloadOnWifiOnly) maxConcurrentDownloads else downloadNonWifiLimit

	val hasAutoDownload: Boolean
		get() = isAutoDownloadEnabled && autoDownloadChapters > 0

	val hasAutoCleanup: Boolean
		get() = isAutoCleanupEnabled && cleanupDaysOld > 0

	val storageWarningThresholdMb: Long
		get() = minStorageRequiredMb * 2L

	val downloadDirectoryDisplay: String
		get() = downloadDirectory.ifBlank { "Default" }
}

@Serializable
enum class DownloadFormat {
	CHAPTER,
	VOLUME,
	COMPLETE;

	companion object {
		fun fromString(value: String): DownloadFormat {
			return when (value.lowercase()) {
				"chapter" -> CHAPTER
				"volume" -> VOLUME
				"complete", "full" -> COMPLETE
				else -> CHAPTER
			}
		}
	}

	val displayName: String
		get() = when (this) {
			CHAPTER -> "Chapter"
			VOLUME -> "Volume"
			COMPLETE -> "Complete Manga"
		}
}
