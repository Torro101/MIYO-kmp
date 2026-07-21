package org.koharu.miyo.core.di.expect

actual object DateUtils {
	actual fun formatDate(epochMillis: Long, pattern: String): String = epochMillis.toString()

	actual fun formatRelative(epochMillis: Long): String = epochMillis.toString()

	actual fun parseDate(dateString: String, pattern: String): Long? = dateString.toLongOrNull()

	actual fun getCurrentEpochMillis(): Long = currentDateTime().toEpochMilliseconds()
}

actual fun createDateUtils(): DateUtils = DateUtils
