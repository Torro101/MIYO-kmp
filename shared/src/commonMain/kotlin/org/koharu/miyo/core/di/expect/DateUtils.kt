package org.koharu.miyo.core.di.expect

/**
 * Platform-agnostic date formatting utilities.
 */
expect object DateUtils {
	fun formatDate(epochMillis: Long, pattern: String): String
	fun formatRelative(epochMillis: Long): String
	fun parseDate(dateString: String, pattern: String): Long?
	fun getCurrentEpochMillis(): Long
}

expect fun createDateUtils(): DateUtils
