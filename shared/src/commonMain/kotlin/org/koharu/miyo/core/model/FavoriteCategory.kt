package org.koharu.miyo.core.model

import kotlinx.serialization.Serializable

/**
 * Cross-platform favorite category model.
 */
@Serializable
data class FavoriteCategory(
	val id: Long,
	val title: String,
	val order: Int = 0,
	val isHidden: Boolean = false,
	val mangaCount: Int = 0
) {
	val displayTitle: String
		get() = if (isHidden) "$title (Hidden)" else title
}
