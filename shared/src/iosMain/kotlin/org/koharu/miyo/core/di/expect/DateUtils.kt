package org.koharu.miyo.core.di.expect

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeIntervalSince1970
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970

actual object DateUtils {
	actual fun formatDate(epochMillis: Long, pattern: String): String {
		val formatter = NSDateFormatter()
		formatter.dateFormat = pattern
		val date = NSDate.dateWithTimeIntervalSince1970(epochMillis / 1000.0)
		return formatter.stringFromDate(date)
	}

	actual fun formatRelative(epochMillis: Long): String {
		val now = NSDate().timeIntervalSince1970 * 1000
		val diff = now - epochMillis

		return when {
			diff < 60 * 1000 -> "Just now"
			diff < 60 * 60 * 1000 -> "${(diff / (60 * 1000)).toInt()}m ago"
			diff < 24 * 60 * 60 * 1000 -> "${(diff / (60 * 60 * 1000)).toInt()}h ago"
			diff < 7 * 24 * 60 * 60 * 1000 -> "${(diff / (24 * 60 * 60 * 1000)).toInt()}d ago"
			else -> formatDate(epochMillis, "MMM dd, yyyy")
		}
	}

	actual fun parseDate(dateString: String, pattern: String): Long? {
		val formatter = NSDateFormatter()
		formatter.dateFormat = pattern
		val date = formatter.dateFromString(dateString) ?: return null
		return (date.timeIntervalSince1970 * 1000).toLong()
	}

	actual fun getCurrentEpochMillis(): Long {
		return (NSDate().timeIntervalSince1970 * 1000).toLong()
	}
}

actual fun createDateUtils(): DateUtils = DateUtils
