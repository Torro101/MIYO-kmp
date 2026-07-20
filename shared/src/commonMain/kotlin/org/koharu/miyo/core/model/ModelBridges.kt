package org.koharu.miyo.core.model

import org.koharu.miyo.details.data.ReadingTimeEstimate
import org.koharu.miyo.reader.ui.ReaderPosition

/** Pure helpers for shared DTO boundaries. */
object ModelBridges {
	fun readingTime(totalMinutes: Int, isContinue: Boolean): ReadingTimeEstimate {
		val safe = totalMinutes.coerceAtLeast(0)
		return ReadingTimeEstimate(
			minutes = safe % 60,
			hours = safe / 60,
			isContinue = isContinue,
		)
	}

	fun readerPosition(chapterId: Long, page: Int, scroll: Int = 0): ReaderPosition =
		ReaderPosition(chapterId = chapterId, page = page, scroll = scroll)
}
