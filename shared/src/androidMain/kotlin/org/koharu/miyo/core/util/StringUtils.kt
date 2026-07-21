package org.koharu.miyo.core.util

/**
 * Cross-platform string utilities.
 */
object StringUtils {
	fun capitalizeWords(input: String): String {
		return input.split(" ").joinToString(" ") { word ->
			word.replaceFirstChar { it.uppercase() }
		}
	}

	fun truncate(input: String, maxLength: Int, suffix: String = "..."): String {
		return if (input.length > maxLength) {
			input.take(maxLength - suffix.length) + suffix
		} else {
			input
		}
	}

	fun removeHtmlTags(input: String): String {
		return input.replace(Regex("<[^>]*>"), "")
	}

	fun normalizeWhitespace(input: String): String {
		return input.replace(Regex("\\s+"), " ").trim()
	}

	fun isNumeric(input: String): Boolean {
		return input.all { it.isDigit() }
	}

	fun extractNumbers(input: String): String {
		return input.filter { it.isDigit() }
	}

	fun formatFileSize(bytes: Long): String {
		return when {
			bytes < 1024 -> "$bytes B"
			bytes < 1024 * 1024 -> "${bytes / 1024} KB"
			bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
			else -> "${bytes / (1024 * 1024 * 1024)} GB"
		}
	}

	fun formatDuration(millis: Long): String {
		val seconds = millis / 1000
		val minutes = seconds / 60
		val hours = minutes / 60

		return when {
			hours > 0 -> "${hours}h ${minutes % 60}m"
			minutes > 0 -> "${minutes}m ${seconds % 60}s"
			else -> "${seconds}s"
		}
	}
}
