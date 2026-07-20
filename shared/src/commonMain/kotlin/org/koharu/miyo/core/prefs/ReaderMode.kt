package org.koharu.miyo.core.prefs

/**
 * Android reader paging mode (persisted by id).
 * Distinct from [org.koharu.miyo.core.domain.ReaderMode] shared DTO enum.
 */
enum class ReaderMode(val id: Int) {
	STANDARD(1),
	REVERSED(3),
	VERTICAL(4),
	WEBTOON(2),
	;

	companion object {
		fun valueOf(id: Int): ReaderMode? = entries.firstOrNull { it.id == id }
	}
}
