package org.koharu.miyo.core.model

import kotlinx.serialization.Serializable

/**
 * Cross-platform filter model for manga search.
 */
@Serializable
data class Filter(
	val id: String,
	val name: String,
	val type: FilterType,
	val options: List<FilterOption> = emptyList(),
	val defaultValue: String? = null,
)

@Serializable
enum class FilterType {
	TEXT,
	SELECT,
	MULTI_SELECT,
	CHECKBOX,
	RANGE,
	GROUP;

	companion object {
		fun fromString(value: String): FilterType {
			return when (value.lowercase()) {
				"text", "search" -> TEXT
				"select", "dropdown" -> SELECT
				"multi_select", "multiselect" -> MULTI_SELECT
				"checkbox", "check" -> CHECKBOX
				"range", "slider" -> RANGE
				"group", "section" -> GROUP
				else -> TEXT
			}
		}
	}
}

@Serializable
data class FilterOption(
	val id: String,
	val name: String,
	val value: String = id,
	val isSelected: Boolean = false,
)
