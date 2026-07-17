package org.koharu.miyo.core.di.expect

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

actual class DateTime(private val calendar: Calendar) {
	actual val epochMillis: Long
		get() = calendar.timeInMillis
	actual val year: Int
		get() = calendar.get(Calendar.YEAR)
	actual val month: Int
		get() = calendar.get(Calendar.MONTH) + 1
	actual val day: Int
		get() = calendar.get(Calendar.DAY_OF_MONTH)
	actual val hour: Int
		get() = calendar.get(Calendar.HOUR_OF_DAY)
	actual val minute: Int
		get() = calendar.get(Calendar.MINUTE)
	actual val second: Int
		get() = calendar.get(Calendar.SECOND)

	actual fun toEpochMilliseconds(): Long = calendar.timeInMillis

	actual fun toString(format: String): String {
		val sdf = SimpleDateFormat(format, Locale.US)
		sdf.timeZone = TimeZone.getTimeZone("UTC")
		return sdf.format(calendar.time)
	}
}

actual fun currentDateTime(): DateTime {
	return DateTime(Calendar.getInstance())
}

actual fun dateTimeFromEpochMillis(epochMillis: Long): DateTime {
	val calendar = Calendar.getInstance()
	calendar.timeInMillis = epochMillis
	return DateTime(calendar)
}

actual fun parseDateTime(value: String, format: String): DateTime {
	val sdf = SimpleDateFormat(format, Locale.US)
	sdf.timeZone = TimeZone.getTimeZone("UTC")
	val date = sdf.parse(value) ?: Date()
	val calendar = Calendar.getInstance()
	calendar.time = date
	return DateTime(calendar)
}
