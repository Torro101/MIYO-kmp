package org.koharu.miyo.core.prefs

/** Screenshot blocking policy. Do not rename entries (prefs keys). */
enum class ScreenshotsPolicy {
	ALLOW, BLOCK_NSFW, BLOCK_INCOGNITO, BLOCK_ALL;
}
