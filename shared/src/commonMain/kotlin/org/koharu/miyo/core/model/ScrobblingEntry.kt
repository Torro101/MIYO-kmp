package org.koharu.miyo.core.model

import kotlinx.serialization.Serializable

/**
 * Cross-platform scrobbling entry for tracking services.
 */
@Serializable
data class ScrobblingEntry(
	val mangaId: Long,
	val scrobblerType: ScrobblerType,
	val externalId: String,
	val title: String = "",
	val status: ScrobblingStatus = ScrobblingStatus.PLANNING,
	val score: Float = 0f,
	val chaptersRead: Int = 0,
	val lastUpdated: Long = 0L
)

@Serializable
enum class ScrobblerType {
	ANILIST,
	MAL,
	KITSU,
	SHIKIMORI;

	companion object {
		fun fromString(value: String): ScrobblerType {
			return when (value.lowercase()) {
				"anilist" -> ANILIST
				"mal", "myanimelist" -> MAL
				"kitsu" -> KITSU
				"shikimori" -> SHIKIMORI
				else -> ANILIST
			}
		}
	}
}

@Serializable
enum class ScrobblingStatus {
	READING,
	COMPLETED,
	ON_HOLD,
	DROPPED,
	PLANNING;

	companion object {
		fun fromString(value: String): ScrobblingStatus {
			return when (value.lowercase()) {
				"reading", "currently_reading" -> READING
				"completed", "finished" -> COMPLETED
				"on_hold", "paused" -> ON_HOLD
				"dropped", "cancelled" -> DROPPED
				"planning", "plan_to_read" -> PLANNING
				else -> PLANNING
			}
		}
	}
}
