package org.koharu.miyo.core.domain

import kotlinx.serialization.Serializable

/**
 * Cross-platform network policy model.
 */
@Serializable
enum class NetworkPolicy {
	WIFI_ONLY,
	MOBILE_DATA,
	ALWAYS,
	ASK;

	companion object {
		fun fromString(value: String): NetworkPolicy {
			return when (value.lowercase()) {
				"wifi", "wifi_only", "wifi-only" -> WIFI_ONLY
				"mobile", "mobile_data", "cellular" -> MOBILE_DATA
				"always", "any" -> ALWAYS
				"ask", "prompt" -> ASK
				else -> WIFI_ONLY
			}
		}
	}

	val displayName: String
		get() = when (this) {
			WIFI_ONLY -> "Wi-Fi Only"
			MOBILE_DATA -> "Mobile Data"
			ALWAYS -> "Always"
			ASK -> "Ask"
		}
}

@Serializable
enum class ContentLanguage {
	ALL,
	JAPANESE,
	ENGLISH,
	KOREAN,
	CHINESE;

	companion object {
		fun fromString(value: String): ContentLanguage {
			return when (value.lowercase()) {
				"all", "any" -> ALL
				"japanese", "ja", "jp" -> JAPANESE
				"english", "en" -> ENGLISH
				"korean", "ko", "kr" -> KOREAN
				"chinese", "zh", "cn" -> CHINESE
				else -> ALL
			}
		}
	}

	val displayName: String
		get() = when (this) {
			ALL -> "All Languages"
			JAPANESE -> "Japanese"
			ENGLISH -> "English"
			KOREAN -> "Korean"
			CHINESE -> "Chinese"
		}
}
