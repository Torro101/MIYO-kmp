package org.koharu.miyo.core.util

/**
 * Cross-platform formatting utilities.
 */
object FormatUtils {
	fun formatNumber(number: Int): String {
		return when {
			number >= 1_000_000 -> "${trim1(number / 1_000_000.0)}M"
			number >= 1_000 -> "${trim1(number / 1_000.0)}K"
			else -> number.toString()
		}
	}

	fun formatNumber(number: Long): String {
		return when {
			number >= 1_000_000L -> "${trim1(number / 1_000_000.0)}M"
			number >= 1_000L -> "${trim1(number / 1_000.0)}K"
			else -> number.toString()
		}
	}

	fun formatFileSize(bytes: Long): String {
		return when {
			bytes < 1024 -> "$bytes B"
			bytes < 1024 * 1024 -> "${trim1(bytes / 1024.0)} KB"
			bytes < 1024L * 1024 * 1024 -> "${trim1(bytes / (1024.0 * 1024))} MB"
			else -> "${trim1(bytes / (1024.0 * 1024 * 1024))} GB"
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

	fun formatPercentage(value: Float): String = "${trim1(value * 100.0)}%"

	fun formatRating(rating: Float): String = trim1(rating.toDouble())

	fun formatChapterNumber(chapter: Float): String {
		return if (chapter == chapter.toInt().toFloat()) {
			chapter.toInt().toString()
		} else {
			trim1(chapter.toDouble())
		}
	}

	fun formatVolume(volume: String): String {
		return if (volume.isNotBlank()) "Vol. $volume" else ""
	}

	fun formatAuthor(author: String): String {
		return if (author.isNotBlank()) "by $author" else ""
	}

	private fun trim1(v: Double): String {
		val scaled = (v * 10.0).toLong() / 10.0
		val s = scaled.toString()
		return if (s.endsWith(".0")) s.dropLast(2) else s
	}
}
