package org.koharu.miyo.core.model

import kotlinx.serialization.Serializable

/**
 * Cross-platform page data model for manga reader.
 */
@Serializable
data class Page(
	val index: Int,
	val url: String,
	val previewUrl: String = "",
	val state: PageState = PageState.NONE,
	val progress: Int = 0,
	val error: String? = null
) {
	val isLoaded: Boolean
		get() = state == PageState.READY

	val isLoading: Boolean
		get() = state == PageState.LOADING

	val hasError: Boolean
		get() = state == PageState.ERROR

	val displayProgress: String
		get() = "$progress%"
}

@Serializable
enum class PageState {
	NONE,
	LOADING,
	READY,
	ERROR;

	companion object {
		fun fromString(value: String): PageState {
			return when (value.lowercase()) {
				"loading" -> LOADING
				"ready", "loaded" -> READY
				"error", "failed" -> ERROR
				else -> NONE
			}
		}
	}
}
