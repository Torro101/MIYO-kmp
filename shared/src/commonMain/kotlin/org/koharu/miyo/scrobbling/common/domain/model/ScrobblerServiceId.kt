package org.koharu.miyo.scrobbling.common.domain.model

import kotlinx.serialization.Serializable

/**
 * Tracking services available in Miyo.
 * Android maps [titleKey]/[iconKey] to string/drawable resources.
 */
@Serializable
enum class ScrobblerServiceId(val id: Int, val titleKey: String, val iconKey: String) {
	SHIKIMORI(1, "shikimori", "ic_shikimori"),
	ANILIST(2, "anilist", "ic_anilist"),
	MAL(3, "mal", "ic_mal"),
	KITSU(4, "kitsu", "ic_kitsu"),
	;

	companion object {
		fun fromId(id: Int): ScrobblerServiceId? = entries.find { it.id == id }
	}
}
