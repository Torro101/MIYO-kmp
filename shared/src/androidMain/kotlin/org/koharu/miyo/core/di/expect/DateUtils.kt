package org.koharu.miyo.core.di.expect

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

actual object DateUtils {
	actual fun formatDate(epochMillis: Long, pattern: String): String {
		val sdf = SimpleDateFormat(pattern, Locale.getDefault())
		return sdf.format(Date(epochMillis))
	}

	actual fun formatRelative(epochMillis: Long): String {
		val now = System.currentTimeMillis()
		val diff = now - epochMillis

		return when {
			diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
			diff < TimeUnit.HOURS.toMillis(1) -> "${diff / TimeUnit.MINUTES.toMillis(1)}m ago"
			diff < TimeUnit.DAYS.toMillis(1) -> "${diff / TimeUnit.HOURS.toMillis(1)}h ago"
			diff < TimeUnit.DAYS.toMillis(7) -> "${diff / TimeUnit.DAYS.toMillis(1)}d ago"
			else -> formatDate(epochMillis, "MMM dd, yyyy")
		}
	}

	actual fun parseDate(dateString: String, pattern: String): Long? {
		return try {
			val sdf = SimpleDateFormat(pattern, Locale.getDefault())
			sdf.parse(dateString)?.time
		} catch (e: Exception) {
			null
		}
	}

	actual fun getCurrentEpochMillis(): Long {
		return System.currentTimeMillis()
	}
}

actual fun createDateUtils(): DateUtils = DateUtils
