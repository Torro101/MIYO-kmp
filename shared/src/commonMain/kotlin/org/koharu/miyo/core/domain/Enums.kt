package org.koharu.miyo.core.domain

import kotlinx.serialization.Serializable

/**
 * Cross-platform sort order and direction enums.
 */
@Serializable
enum class SortOrder {
	POPULARITY,
	UPDATED,
.NEWEST,
	OLDEST,
.ALPHABETICAL,
.RATING;

	companion object {
		fun fromString(value: String): SortOrder {
			return when (value.lowercase()) {
				"popularity", "popular" -> POPULARITY
				"updated", "recent" -> UPDATED
				"newest", "new" -> NEWEST
				"oldest", "old" -> OLDEST
				"alphabetical", "alpha", "name" -> ALPHABETICAL
				"rating", "score" -> RATING
				else -> POPULARITY
			}
		}
	}

	val displayName: String
		get() = when (this) {
			POPULARITY -> "Popular"
			UPDATED -> "Updated"
			NEWEST -> "Newest"
			OLDEST -> "Oldest"
			ALPHABETICAL -> "A-Z"
			RATING -> "Rating"
		}
}

@Serializable
enum class SortDirection {
	ASCENDING,
	DESCENDING;

	companion object {
		fun fromString(value: String): SortDirection {
			return when (value.lowercase()) {
				"asc", "ascending", "up" -> ASCENDING
				"desc", "descending", "down" -> DESCENDING
				else -> DESCENDING
			}
		}
	}

	val displayName: String
		get() = when (this) {
			ASCENDING -> "Ascending"
			DESCENDING -> "Descending"
		}
}

@Serializable
enum class ContentRating {
	ALL,
	SAFE,
	SUGGESTIVE,
	EXPLICIT;

	companion object {
		fun fromString(value: String): ContentRating {
			return when (value.lowercase()) {
				"all", "any" -> ALL
				"safe", "s", "general" -> SAFE
				"suggestive", "su", "teen" -> SUGGESTIVE
				"explicit", "e", "mature" -> EXPLICIT
				else -> ALL
			}
		}
	}

	val displayName: String
		get() = when (this) {
			ALL -> "All"
			SAFE -> "Safe"
			SUGGESTIVE -> "Suggestive"
			EXPLICIT -> "Explicit"
		}
}

@Serializable
enum class MangaStatus {
	ONGOING,
	COMPLETED,
	HIATUS,
	CANCELLED,
	UNKNOWN;

	companion object {
		fun fromString(value: String): MangaStatus {
			return when (value.lowercase()) {
				"ongoing", "publishing", "running" -> ONGOING
				"completed", "finished", "complete" -> COMPLETED
				"hiatus", "on_hold", "paused" -> HIATUS
				"cancelled", "dropped" -> CANCELLED
				else -> UNKNOWN
			}
		}
	}

	val displayName: String
		get() = when (this) {
			ONGOING -> "Ongoing"
			COMPLETED -> "Completed"
			HIATUS -> "Hiatus"
			CANCELLED -> "Cancelled"
			UNKNOWN -> "Unknown"
		}
}
