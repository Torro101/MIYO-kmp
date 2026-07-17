package org.koharu.miyo.core.domain

import kotlinx.serialization.Serializable

/**
 * Cross-platform theme model.
 */
@Serializable
enum class Theme {
	LIGHT,
	DARK,
	BLACK,
	SYSTEM;

	companion object {
		fun fromString(value: String): Theme {
			return when (value.lowercase()) {
				"light", "day" -> LIGHT
				"dark", "night" -> DARK
				"black", "amoled" -> BLACK
				"system", "auto" -> SYSTEM
				else -> SYSTEM
			}
		}
	}

	val displayName: String
		get() = when (this) {
			LIGHT -> "Light"
			DARK -> "Dark"
			BLACK -> "Black (AMOLED)"
			SYSTEM -> "System"
		}

	val isDark: Boolean
		get() = this == DARK || this == BLACK
}

@Serializable
enum class ColorScheme {
	DEFAULT,
	BLUE,
	GREEN,
	PURPLE,
	RED,
	ORANGE;

	companion object {
		fun fromString(value: String): ColorScheme {
			return when (value.lowercase()) {
				"default", "material" -> DEFAULT
				"blue" -> BLUE
				"green" -> GREEN
				"purple" -> PURPLE
				"red" -> RED
				"orange" -> ORANGE
				else -> DEFAULT
			}
		}
	}
}
