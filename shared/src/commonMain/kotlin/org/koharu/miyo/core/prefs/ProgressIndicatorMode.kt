package org.koharu.miyo.core.prefs

/**
 * How reading progress is shown in lists.
 * Android UI maps titles via string resources; values are stable for prefs/sync.
 */
enum class ProgressIndicatorMode {
	NONE,
	PERCENT_READ,
	PERCENT_LEFT,
	CHAPTERS_READ,
	CHAPTERS_LEFT,
}
