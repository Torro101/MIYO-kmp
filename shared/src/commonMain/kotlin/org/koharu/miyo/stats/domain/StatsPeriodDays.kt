package org.koharu.miyo.stats.domain

import kotlinx.serialization.Serializable

/**
 * Stats window sizes (title strings remain Android resources).
 */
@Serializable
enum class StatsPeriodDays(val days: Int) {
	DAY(1),
	WEEK(7),
	MONTH(30),
	MONTHS_3(90),
	ALL(Int.MAX_VALUE),
}
