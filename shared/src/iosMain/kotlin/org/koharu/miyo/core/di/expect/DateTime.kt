package org.koharu.miyo.core.di.expect

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSISO8601DateFormatter
import platform.Foundation.NSTimeIntervalSince1970
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970

actual class DateTime(private val date: NSDate) {
	actual val epochMillis: Long
		get() = (date.timeIntervalSince1970 * 1000).toLong()
	actual val year: Int
		get() {
			val formatter = NSDateFormatter()
			formatter.dateFormat = "yyyy"
			return formatter.stringFromDate(date).toInt()
		}
	actual val month: Int
		get() {
			val formatter = NSDateFormatter()
			formatter.dateFormat = "MM"
			return formatter.stringFromDate(date).toInt()
		}
	actual val day: Int
		get() {
			val formatter = NSDateFormatter()
			formatter.dateFormat = "dd"
			return formatter.stringFromDate(date).toInt()
		}
	actual val hour: Int
		get() {
			val formatter = NSDateFormatter()
			formatter.dateFormat = "HH"
			return formatter.stringFromDate(date).toInt()
		}
	actual val minute: Int
		get() {
			val formatter = NSDateFormatter()
			formatter.dateFormat = "mm"
			return formatter.stringFromDate(date).toInt()
		}
	actual val second: Int
		get() {
			val formatter = NSDateFormatter()
			formatter.dateFormat = "ss"
			return formatter.stringFromDate(date).toInt()
		}

	actual fun toEpochMilliseconds(): Long = epochMillis

	actual fun toString(format: String): String {
		val formatter = NSDateFormatter()
		formatter.dateFormat = format
		return formatter.stringFromDate(date)
	}
}

actual fun currentDateTime(): DateTime {
	return DateTime(NSDate())
}

actual fun dateTimeFromEpochMillis(epochMillis: Long): DateTime {
	val date = NSDate.dateWithTimeIntervalSince1970(epochMillis / 1000.0)
	return DateTime(date)
}

actual fun parseDateTime(value: String, format: String): DateTime {
	val formatter = NSDateFormatter()
	formatter.dateFormat = format
	val date = formatter.dateFromString(value) ?: NSDate()
	return DateTime(date)
}
