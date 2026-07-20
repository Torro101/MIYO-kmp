package org.koharu.miyo.core.prefs

/**
 * Preferred on-disk format for downloaded chapters/manga.
 * Android UI maps titles via string resources.
 */
enum class DownloadFormat {
	AUTOMATIC,
	SINGLE_CBZ,
	MULTIPLE_CBZ,
}
