package org.koharu.miyo.core.di.expect

import kotlin.system.getTimeMillis

actual class DateTime(private val millis: Long) {
	actual val epochMillis: Long get() = millis
	actual val year: Int get() = 1970
	actual val month: Int get() = 1
	actual val day: Int get() = 1
	actual val hour: Int get() = 0
	actual val minute: Int get() = 0
	actual val second: Int get() = 0

	actual fun toEpochMilliseconds(): Long = millis
	actual fun toString(format: String): String = millis.toString()
}

actual fun currentDateTime(): DateTime = DateTime(getTimeMillis())

actual fun dateTimeFromEpochMillis(epochMillis: Long): DateTime = DateTime(epochMillis)

actual fun parseDateTime(value: String, format: String): DateTime =
	DateTime(value.toLongOrNull() ?: 0L)
