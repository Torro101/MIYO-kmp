package org.koharu.miyo.details.data

import android.content.res.Resources
import org.koharu.miyo.shared.R
import org.koharu.miyo.core.util.ext.getQuantityStringSafe

data class ReadingTime(
	val minutes: Int,
	val hours: Int,
	val isContinue: Boolean,
) {
	fun toEstimate(): ReadingTimeEstimate =
		ReadingTimeEstimate(minutes = minutes, hours = hours, isContinue = isContinue)

	fun format(resources: Resources): String = when {
		hours == 0 && minutes == 0 -> resources.getString(R.string.less_than_minute)
		hours == 0 -> resources.getQuantityStringSafe(R.plurals.minutes, minutes, minutes)
		minutes == 0 -> resources.getQuantityStringSafe(R.plurals.hours, hours, hours)
		else -> resources.getString(
			R.string.remaining_time_pattern,
			resources.getQuantityStringSafe(R.plurals.hours, hours, hours),
			resources.getQuantityStringSafe(R.plurals.minutes, minutes, minutes),
		)
	}

	fun formatShort(resources: Resources): String? = when {
		hours == 0 && minutes == 0 -> null
		hours == 0 -> resources.getString(R.string.minutes_short, minutes)
		minutes == 0 -> resources.getString(R.string.hours_short, hours)
		else -> resources.getString(R.string.hours_minutes_short, hours, minutes)
	}

	companion object {
		fun fromEstimate(estimate: ReadingTimeEstimate): ReadingTime = ReadingTime(
			minutes = estimate.minutes,
			hours = estimate.hours,
			isContinue = estimate.isContinue,
		)
	}
}
