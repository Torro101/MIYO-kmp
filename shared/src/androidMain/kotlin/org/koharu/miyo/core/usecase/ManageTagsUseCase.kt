package org.koharu.miyo.core.usecase

import org.koharu.miyo.core.model.Tag
import org.koharu.miyo.core.repository.TagRepository
import kotlinx.coroutines.flow.Flow

/**
 * Cross-platform use case for managing tags.
 */
class ManageTagsUseCase(
	private val tagRepository: TagRepository
) {
	suspend fun getTags(source: String? = null): List<Tag> {
		return tagRepository.getTags(source)
	}

	suspend fun getPopularTags(source: String): List<Tag> {
		return tagRepository.getPopularTags(source)
	}

	suspend fun getRelatedTags(tagId: Long): List<Tag> {
		return tagRepository.getRelatedTags(tagId)
	}

	suspend fun getTag(id: Long): Tag? {
		return tagRepository.getTag(id)
	}

	suspend fun addToFavorites(tagId: Long) {
		tagRepository.addToFavorites(tagId)
	}

	suspend fun removeFromFavorites(tagId: Long) {
		tagRepository.removeFromFavorites(tagId)
	}

	suspend fun toggleFavorite(tagId: Long): Boolean {
		val isFavorite = tagRepository.isFavorite(tagId)
		if (isFavorite) {
			tagRepository.removeFromFavorites(tagId)
		} else {
			tagRepository.addToFavorites(tagId)
		}
		return !isFavorite
	}

	suspend fun isFavorite(tagId: Long): Boolean {
		return tagRepository.isFavorite(tagId)
	}

	fun observeTags(source: String? = null): Flow<List<Tag>> {
		return tagRepository.observeTags(source)
	}
}
