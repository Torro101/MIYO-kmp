package org.koharu.miyo.core.model

import kotlinx.serialization.Serializable

/**
 * Cross-platform tag model for manga categorization.
 */
@Serializable
data class Tag(
	val id: Long = 0,
	val key: String,
	val title: String,
	val source: String = "",
	val isPinned: Boolean = false,
	val mangaCount: Int = 0
) {
	val displayName: String
		get() = title.ifBlank { key }

	val isLocal: Boolean
		get() = source.isBlank()

	val isRemote: Boolean
		get() = source.isNotBlank()
}

@Serializable
data class TagGroup(
	val name: String,
	val tags: List<Tag>,
	val isExpanded: Boolean = true
) {
	val count: Int
		get() = tags.size
}
