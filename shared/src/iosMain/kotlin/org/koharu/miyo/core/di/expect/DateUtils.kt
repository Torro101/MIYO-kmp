package org.koharu.miyo.core.di.expect

import kotlin.system.getTimeMillis

actual object DateUtils {
	actual fun formatDate(epochMillis: Long, pattern: String): String = epochMillis.toString()

	actual fun formatRelative(epochMillis: Long): String = epochMillis.toString()

	actual fun parseDate(dateString: String, pattern: String): Long? = dateString.toLongOrNull()

	actual fun getCurrentEpochMillis(): Long = getTimeMillis()
}

actual fun createDateUtils(): DateUtils = DateUtils
