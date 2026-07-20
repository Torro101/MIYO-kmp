package org.koharu.miyo.details.data

import kotlinx.serialization.Serializable

/**
 * Estimated remaining read time (formatting with resources stays Android).
 */
@Serializable
data class ReadingTimeEstimate(
	val minutes: Int,
	val hours: Int,
	val isContinue: Boolean,
) {
	val totalMinutes: Int get() = hours * 60 + minutes
	val isEmpty: Boolean get() = hours == 0 && minutes == 0
}
