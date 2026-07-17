package org.koharu.miyo.core.model

import kotlinx.serialization.Serializable

/**
 * Cross-platform reading history entry.
 */
@Serializable
data class HistoryEntry(
	val mangaId: Long,
	val chapterId: Long,
	val timestamp: Long,
	val page: Int = 0,
	val totalPages: Int = 0,
	val scrollPosition: Float = 0f
) {
	val progress: Float
		get() = if (totalPages > 0) page.toFloat() / totalPages else 0f

	val displayProgress: String
		get() = "$page / $totalPages pages"
}
