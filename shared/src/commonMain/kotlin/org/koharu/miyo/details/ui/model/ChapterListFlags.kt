package org.koharu.miyo.details.ui.model

/**
 * Bit flags for chapter rows (shared between platforms).
 * Full [ChapterListItem] UI model remains Android (parsers + Resources).
 */
object ChapterListFlags {
	const val FLAG_UNREAD: Byte = 2
	const val FLAG_CURRENT: Byte = 4
	const val FLAG_NEW: Byte = 8
	const val FLAG_BOOKMARKED: Byte = 16
	const val FLAG_DOWNLOADED: Byte = 32
	const val FLAG_GRID: Byte = 64

	fun pack(
		isCurrent: Boolean = false,
		isUnread: Boolean = false,
		isNew: Boolean = false,
		isDownloaded: Boolean = false,
		isBookmarked: Boolean = false,
		isGrid: Boolean = false,
	): Byte {
		var flags: Byte = 0
		if (isCurrent) flags = (flags.toInt() or FLAG_CURRENT.toInt()).toByte()
		if (isUnread) flags = (flags.toInt() or FLAG_UNREAD.toInt()).toByte()
		if (isNew) flags = (flags.toInt() or FLAG_NEW.toInt()).toByte()
		if (isBookmarked) flags = (flags.toInt() or FLAG_BOOKMARKED.toInt()).toByte()
		if (isDownloaded) flags = (flags.toInt() or FLAG_DOWNLOADED.toInt()).toByte()
		if (isGrid) flags = (flags.toInt() or FLAG_GRID.toInt()).toByte()
		return flags
	}

	fun has(flags: Byte, flag: Byte): Boolean = (flags.toInt() and flag.toInt()) == flag.toInt()
}
