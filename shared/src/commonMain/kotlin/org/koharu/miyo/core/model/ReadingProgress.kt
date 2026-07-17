package org.koharu.miyo.core.model

import kotlinx.serialization.Serializable

/**
 * Cross-platform reading progress model.
 */
@Serializable
data class ReadingProgress(
	val mangaId: Long,
	val chapterId: Long,
	val page: Int = 0,
	val totalPages: Int = 0,
	val scrollPosition: Float = 0f,
	val timestamp: Long = 0,
	val chapterNumber: Float = 0f,
	val chapterTitle: String = ""
) {
	val progress: Float
		get() = if (totalPages > 0) page.toFloat() / totalPages else 0f

	val progressPercent: Int
		get() = (progress * 100).toInt()

	val isComplete: Boolean
		get() = totalPages > 0 && page >= totalPages

	val displayProgress: String
		get() = "$page / $totalPages pages"

	val displayPercentage: String
		get() = "$progressPercent%"
}
