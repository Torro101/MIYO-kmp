package org.koharu.miyo.core.di.expect

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual class DateTime(private val millis: Long) {
	actual val epochMillis: Long get() = millis
	actual val year: Int get() = components().year
	actual val month: Int get() = components().month
	actual val day: Int get() = components().day
	actual val hour: Int get() = components().hour
	actual val minute: Int get() = components().minute
	actual val second: Int get() = components().second

	actual fun toEpochMilliseconds(): Long = millis
	actual fun toString(format: String): String = millis.toString()

	private data class Parts(
		val year: Int,
		val month: Int,
		val day: Int,
		val hour: Int,
		val minute: Int,
		val second: Int,
	)

	private fun components(): Parts {
		// Lightweight UTC breakdown without Foundation calendar bridging complexity.
		val totalSeconds = millis / 1000L
		val days = totalSeconds / 86_400L
		val sod = (totalSeconds % 86_400L).toInt().let { if (it < 0) it + 86_400 else it }
		val hour = sod / 3600
		val minute = (sod % 3600) / 60
		val second = sod % 60
		// Civil date from days since Unix epoch (Howard Hinnant algorithm).
		var z = days + 719_468L
		if (millis < 0 && (millis % 1000L != 0L || totalSeconds % 86_400L != 0L)) {
			// floor division adjustment already handled via totalSeconds
		}
		val era = (if (z >= 0) z else z - 146_096) / 146_097
		val doe = z - era * 146_097
		val yoe = (doe - doe / 1460 + doe / 36_524 - doe / 146_096) / 365
		val y = yoe + era * 400
		val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
		val mp = (5 * doy + 2) / 153
		val d = doy - (153 * mp + 2) / 5 + 1
		val m = mp + (if (mp < 10) 3 else -9)
		val yy = y + (if (m <= 2) 1 else 0)
		return Parts(yy.toInt(), m.toInt(), d.toInt(), hour, minute, second)
	}
}

actual fun currentDateTime(): DateTime =
	DateTime((NSDate().timeIntervalSince1970 * 1000.0).toLong())

actual fun dateTimeFromEpochMillis(epochMillis: Long): DateTime = DateTime(epochMillis)

actual fun parseDateTime(value: String, format: String): DateTime =
	DateTime(value.toLongOrNull() ?: 0L)
