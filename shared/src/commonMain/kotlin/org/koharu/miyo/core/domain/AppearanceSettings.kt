package org.koharu.miyo.core.domain

import kotlinx.serialization.Serializable

/**
 * Cross-platform appearance settings model.
 */
@Serializable
data class AppearanceSettings(
	val theme: Theme = Theme.SYSTEM,
	val colorScheme: ColorScheme = ColorScheme.DEFAULT,
	val isDynamicColorEnabled: Boolean = true,
	val isAmoledBlackEnabled: Boolean = false,
	val isCompactModeEnabled: Boolean = false,
	val isGridModeEnabled: Boolean = true,
	val gridColumns: Int = 3,
	val isShowCoverArts: Boolean = true,
	val isShowChapterNumbers: Boolean = true,
	val isShowSourceNames: Boolean = true,
	val isShowLastUpdated: Boolean = true,
	val isShowUnreadBadge: Boolean = true,
	val isShowFavoriteBadge: Boolean = true,
	val isShowDownloadBadge: Boolean = true,
	val isShowRatingBadge: Boolean = false,
	val language: String = "en",
	val dateFormat: String = "MMM dd, yyyy",
	val timeFormat: String = "HH:mm"
) {
	val isDarkTheme: Boolean
		get() = theme.isDark

	val effectiveColumns: Int
		get() = if (isGridModeEnabled) gridColumns else 1

	val hasCustomDateFormat: Boolean
		get() = dateFormat != "MMM dd, yyyy"

	val hasCustomTimeFormat: Boolean
		get() = timeFormat != "HH:mm"

	val isNightMode: Boolean
		get() = theme == Theme.DARK || theme == Theme.BLACK || theme == Theme.SYSTEM

	val displayLanguage: String
		get() = when (language) {
			"en" -> "English"
			"ja" -> "Japanese"
			"ko" -> "Korean"
			"zh" -> "Chinese"
			else -> language
		}
}
