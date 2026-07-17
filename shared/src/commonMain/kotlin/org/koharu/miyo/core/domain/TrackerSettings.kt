package org.koharu.miyo.core.domain

import kotlinx.serialization.Serializable

/**
 * Cross-platform tracker settings model.
 */
@Serializable
data class TrackerSettings(
	val isAnilistEnabled: Boolean = false,
	val isMalEnabled: Boolean = false,
	val isKitsuEnabled: Boolean = false,
	val isShikimoriEnabled: Boolean = false,
	val isDiscordEnabled: Boolean = false,
	val discordWebhookUrl: String = "",
	val anilistToken: String = "",
	val malToken: String = "",
	val kitsuToken: String = "",
	val shikimoriToken: String = "",
	val scrobbleOnAddToFavourites: Boolean = true,
	val scrobbleOnChapterRead: Boolean = true,
	val askBeforeScrobbling: Boolean = false
) {
	val enabledTrackers: List<ScrobblerType>
		get() = buildList {
			if (isAnilistEnabled) add(ScrobblerType.ANILIST)
			if (isMalEnabled) add(ScrobblerType.MAL)
			if (isKitsuEnabled) add(ScrobblerType.KITSU)
			if (isShikimoriEnabled) add(ScrobblerType.SHIKIMORI)
		}

	val hasEnabledTrackers: Boolean
		get() = enabledTrackers.isNotEmpty()

	val hasDiscordWebhook: Boolean
		get() = discordWebhookUrl.isNotBlank()

	val isAnyTrackerEnabled: Boolean
		get() = isAnilistEnabled || isMalEnabled || isKitsuEnabled || isShikimoriEnabled || isDiscordEnabled

	val enabledTrackerCount: Int
		get() = enabledTrackers.size + if (isDiscordEnabled) 1 else 0
}

@Serializable
enum class ScrobblerType {
	ANILIST,
	MAL,
	KITSU,
	SHIKIMORI,
	DISCORD;

	companion object {
		fun fromString(value: String): ScrobblerType {
			return when (value.lowercase()) {
				"anilist" -> ANILIST
				"mal", "myanimelist" -> MAL
				"kitsu" -> KITSU
				"shikimori" -> SHIKIMORI
				"discord" -> DISCORD
				else -> ANILIST
			}
		}
	}

	val displayName: String
		get() = when (this) {
			ANILIST -> "AniList"
			MAL -> "MyAnimeList"
			KITSU -> "Kitsu"
			SHIKIMORI -> "Shikimori"
			DISCORD -> "Discord"
		}

	val baseUrl: String
		get() = when (this) {
			ANILIST -> "https://anilist.co"
			MAL -> "https://myanimelist.net"
			KITSU -> "https://kitsu.io"
			SHIKIMORI -> "https://shikimori.one"
			DISCORD -> "https://discord.com"
		}
}
