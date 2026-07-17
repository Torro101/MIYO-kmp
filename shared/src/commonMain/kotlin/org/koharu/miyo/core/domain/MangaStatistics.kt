package org.koharu.miyo.core.domain

import kotlinx.serialization.Serializable

/**
 * Cross-platform manga statistics model.
 */
@Serializable
data class MangaStatistics(
	val totalManga: Int = 0,
	val totalChapters: Int = 0,
	val totalReadChapters: Int = 0,
	val totalDownloading: Int = 0,
	val totalFavourites: Int = 0,
	val averageRating: Float = 0f,
	val readingTimeMinutes: Long = 0,
	val completedManga: Int = 0,
	val ongoingManga: Int = 0
) {
	val readProgress: Float
		get() = if (totalChapters > 0) totalReadChapters.toFloat() / totalChapters else 0f

	val readProgressPercent: Int
		get() = (readProgress * 100).toInt()

	val readingTimeHours: Float
		get() = readingTimeMinutes / 60f

	val readingTimeDisplay: String
		get() = when {
			readingTimeMinutes < 60 -> "${readingTimeMinutes}m"
			readingTimeMinutes < 1440 -> "${readingTimeMinutes / 60}h ${readingTimeMinutes % 60}m"
			else -> "${readingTimeMinutes / 1440}d ${(readingTimeMinutes % 1440) / 60}h"
		}

	val averageRatingDisplay: String
		get() = String.format("%.1f", averageRating)

	val completionRate: Float
		get() = if (totalManga > 0) completedManga.toFloat() / totalManga else 0f

	val completionRatePercent: Int
		get() = (completionRate * 100).toInt()
}
