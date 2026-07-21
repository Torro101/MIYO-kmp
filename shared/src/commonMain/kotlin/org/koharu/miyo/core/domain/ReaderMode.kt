package org.koharu.miyo.core.domain

import kotlinx.serialization.Serializable

/**
 * Cross-platform reader mode model.
 */
@Serializable
enum class ReaderMode {
	LEFT_TO_RIGHT,
	RIGHT_TO_LEFT,
	WEBTOON,
	VERTICAL,
	HORIZONTAL;

	companion object {
		fun fromString(value: String): ReaderMode {
			return when (value.lowercase()) {
				"ltr", "left_to_right", "left-to-right" -> LEFT_TO_RIGHT
				"rtl", "right_to_left", "right-to-left" -> RIGHT_TO_LEFT
				"webtoon", "long_strip" -> WEBTOON
				"vertical", "v" -> VERTICAL
				"horizontal", "h" -> HORIZONTAL
				else -> LEFT_TO_RIGHT
			}
		}
	}

	val displayName: String
		get() = when (this) {
			LEFT_TO_RIGHT -> "Left to Right"
			RIGHT_TO_LEFT -> "Right to Left"
			WEBTOON -> "Webtoon"
			VERTICAL -> "Vertical"
			HORIZONTAL -> "Horizontal"
		}
}
