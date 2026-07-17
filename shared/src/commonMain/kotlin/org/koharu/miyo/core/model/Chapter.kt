package org.koharu.miyo.core.model

import kotlinx.serialization.Serializable

/**
 * Cross-platform chapter data model.
 */
@Serializable
data class Chapter(
	val id: Long,
	val mangaId: Long,
	val title: String,
	val url: String,
	val number: Float = 0f,
	val volume: String = "",
	val scanlator: String = "",
	val uploadDate: Long = 0L,
	val isRead: Boolean = false,
	val isBookmarked: Boolean = false,
	val lastPageRead: Int = 0,
	val totalPages: Int = 0,
	val source: String = ""
) {
	val displayNumber: String
		get() = if (volume.isNotBlank()) {
			"Vol. $volume Ch. $number"
		} else {
			"Ch. $number"
		}

	val displayTitle: String
		get() = title.ifBlank { displayNumber }

	val readProgress: Float
		get() = if (totalPages > 0) lastPageRead.toFloat() / totalPages else 0f

	val isFullyRead: Boolean
		get() = totalPages > 0 && lastPageRead >= totalPages
}
