package org.koharu.miyo.core.model

import kotlinx.serialization.Serializable

/**
 * Cross-platform manga source model.
 */
@Serializable
data class Source(
	val id: String,
	val name: String,
	val baseUrl: String,
	val language: String = "",
	val isNsfw: Boolean = false,
	val isEnabled: Boolean = true,
	val isPinned: Boolean = false,
	val hasMore: Boolean = true,
	val iconUrl: String = ""
) {
	val displayName: String
		get() = name.ifBlank { id }

	val locale: String
		get() = language.ifBlank { "en" }

	val isExternal: Boolean
		get() = baseUrl.isBlank()
}

@Serializable
enum class SourceType {
	BUILTIN,
	PLUGIN,
	EXTERNAL,
	TACHIYOMI;

	companion object {
		fun fromString(value: String): SourceType {
			return when (value.lowercase()) {
				"builtin", "built-in" -> BUILTIN
				"plugin" -> PLUGIN
				"external" -> EXTERNAL
				"tachiyomi", "keiyoushi" -> TACHIYOMI
				else -> BUILTIN
			}
		}
	}
}
