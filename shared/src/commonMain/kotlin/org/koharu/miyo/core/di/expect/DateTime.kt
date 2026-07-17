package org.koharu.miyo.core.di.expect

/**
 * Platform-agnostic date/time utilities.
 */
expect class DateTime {
	val epochMillis: Long
	val year: Int
	val month: Int
	val day: Int
	val hour: Int
	val minute: Int
	val second: Int

	fun toEpochMilliseconds(): Long
	fun toString(format: String): String
}

expect fun currentDateTime(): DateTime
expect fun dateTimeFromEpochMillis(epochMillis: Long): DateTime
expect fun parseDateTime(value: String, format: String): DateTime
