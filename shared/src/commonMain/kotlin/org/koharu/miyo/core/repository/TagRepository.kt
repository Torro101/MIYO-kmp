package org.koharu.miyo.core.repository

import org.koharu.miyo.core.model.Tag
import kotlinx.coroutines.flow.Flow

/**
 * Cross-platform tag repository interface.
 */
interface TagRepository {
	suspend fun getTags(source: String? = null): List<Tag>
	suspend fun getPopularTags(source: String): List<Tag>
	suspend fun getRelatedTags(tagId: Long): List<Tag>
	suspend fun getTag(id: Long): Tag?

	fun observeTags(source: String? = null): Flow<List<Tag>>

	suspend fun addToFavorites(tagId: Long)
	suspend fun removeFromFavorites(tagId: Long)
	suspend fun isFavorite(tagId: Long): Boolean
}
