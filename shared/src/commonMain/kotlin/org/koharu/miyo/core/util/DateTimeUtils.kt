package org.koharu.miyo.core.util

import org.koharu.miyo.core.di.expect.currentDateTime

/**
 * Cross-platform date/time utilities.
 */
object DateTimeUtils {
	fun formatRelativeTime(timestamp: Long): String {
		val now = currentDateTime().toEpochMilliseconds()
		val diff = now - timestamp

		return when {
			diff < 60 * 1000 -> "Just now"
			diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} minutes ago"
			diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} hours ago"
			diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)} days ago"
			else -> "Over a week ago"
		}
	}

	fun formatChapterDate(timestamp: Long): String {
		val now = currentDateTime()
		val date = org.koharu.miyo.core.di.expect.dateTimeFromEpochMillis(timestamp)

		return when {
			now.year != date.year -> "${date.month}/${date.day}/${date.year}"
			now.month != date.month || now.day != date.day -> "${date.month}/${date.day}"
			else -> "Today"
		}
	}

	fun isToday(timestamp: Long): Boolean {
		val now = currentDateTime()
		val date = org.koharu.miyo.core.di.expect.dateTimeFromEpochMillis(timestamp)

		return now.year == date.year && now.month == date.month && now.day == date.day
	}

	fun isYesterday(timestamp: Long): Boolean {
		val now = currentDateTime()
		val date = org.koharu.miyo.core.di.expect.dateTimeFromEpochMillis(timestamp)

		val yesterday = org.koharu.miyo.core.di.expect.dateTimeFromEpochMillis(
			now.toEpochMilliseconds() - 24 * 60 * 60 * 1000
		)

		return yesterday.year == date.year && yesterday.month == date.month && yesterday.day == date.day
	}

	fun isThisWeek(timestamp: Long): Boolean {
		val now = currentDateTime()
		val date = org.koharu.miyo.core.di.expect.dateTimeFromEpochMillis(timestamp)

		val weekAgo = org.koharu.miyo.core.di.expect.dateTimeFromEpochMillis(
			now.toEpochMilliseconds() - 7 * 24 * 60 * 60 * 1000
		)

		return date.toEpochMilliseconds() >= weekAgo.toEpochMilliseconds()
	}
}
