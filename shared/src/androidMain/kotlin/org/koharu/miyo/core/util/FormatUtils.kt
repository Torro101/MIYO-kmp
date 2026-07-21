package org.koharu.miyo.core.util

/**
 * Cross-platform formatting utilities.
 */
object FormatUtils {
	fun formatNumber(number: Int): String {
		return when {
			number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
			number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
			else -> number.toString()
		}
	}

	fun formatNumber(number: Long): String {
		return when {
			number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
			number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
			else -> number.toString()
		}
	}

	fun formatFileSize(bytes: Long): String {
		return when {
			bytes < 1024 -> "$bytes B"
			bytes < 1024 * 1024 -> String.format("%.1f KB", bytes / 1024.0)
			bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024))
			else -> String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024))
		}
	}

	fun formatDuration(millis: Long): String {
		val seconds = millis / 1000
		val minutes = seconds / 60
		val hours = minutes / 60

		return when {
			hours > 0 -> String.format("%dh %dm", hours, minutes % 60)
			minutes > 0 -> String.format("%dm %ds", minutes, seconds % 60)
			else -> String.format("%ds", seconds)
		}
	}

	fun formatPercentage(value: Float): String {
		return String.format("%.1f%%", value * 100)
	}

	fun formatRating(rating: Float): String {
		return String.format("%.1f", rating)
	}

	fun formatChapterNumber(chapter: Float): String {
		return if (chapter == chapter.toInt().toFloat()) {
			chapter.toInt().toString()
		} else {
			String.format("%.1f", chapter)
		}
	}

	fun formatVolume(volume: String): String {
		return if (volume.isNotBlank()) "Vol. $volume" else ""
	}

	fun formatAuthor(author: String): String {
		return if (author.isNotBlank()) "by $author" else ""
	}
}
