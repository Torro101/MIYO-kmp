package org.koharu.miyo.core.model

import kotlinx.serialization.Serializable

/**
 * Cross-platform manga data model.
 * This is a simplified version that works on all platforms.
 */
@Serializable
data class Manga(
	val id: Long,
	val title: String,
	val url: String,
	val source: String,
	val coverUrl: String = "",
	val rating: Float = 0f,
	val contentRating: ContentRating = ContentRating.UNKNOWN,
	val state: MangaState = MangaState.UNKNOWN,
	val author: String = "",
	val artist: String = "",
	val description: String = "",
	val tags: List<String> = emptyList(),
	val isFavorite: Boolean = false,
	val lastUpdated: Long = 0L,
	val totalChapters: Int = 0,
	val readChapters: Int = 0
) {
	val displayTitle: String
		get() = title.ifBlank { url }

	val progress: Float
		get() = if (totalChapters > 0) readChapters.toFloat() / totalChapters else 0f

	val isFullyRead: Boolean
		get() = totalChapters > 0 && readChapters >= totalChapters
}

@Serializable
enum class ContentRating {
	UNKNOWN,
	SAFE,
	SUGGESTIVE,
	EXPLICIT;

	companion object {
		fun fromString(value: String): ContentRating {
			return when (value.lowercase()) {
				"safe", "s", "general" -> SAFE
				"suggestive", "su", "teen" -> SUGGESTIVE
				"explicit", "e", "mature" -> EXPLICIT
				else -> UNKNOWN
			}
		}
	}
}

@Serializable
enum class MangaState {
	ONGOING,
	COMPLETED,
	HIATUS,
	CANCELLED,
	UNKNOWN;

	companion object {
		fun fromString(value: String): MangaState {
			return when (value.lowercase()) {
				"ongoing", "publishing", "running" -> ONGOING
				"completed", "finished", "complete" -> COMPLETED
				"hiatus", "on_hold", "paused" -> HIATUS
				"cancelled", "dropped" -> CANCELLED
				else -> UNKNOWN
			}
		}
	}
}
