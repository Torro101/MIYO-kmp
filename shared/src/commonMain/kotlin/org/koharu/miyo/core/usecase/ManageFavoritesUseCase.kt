package org.koharu.miyo.core.usecase

import org.koharu.miyo.core.model.Manga
import org.koharu.miyo.core.repository.MangaRepository
import kotlinx.coroutines.flow.Flow

/**
 * Cross-platform use case for managing favorites.
 */
class ManageFavoritesUseCase(
	private val mangaRepository: MangaRepository
) {
	suspend fun addToFavorites(mangaId: Long, categoryId: Long = 0) {
		mangaRepository.addToFavorites(mangaId, categoryId)
	}

	suspend fun removeFromFavorites(mangaId: Long, categoryId: Long = 0) {
		mangaRepository.removeFromFavorites(mangaId, categoryId)
	}

	suspend fun isFavorite(mangaId: Long): Boolean {
		return mangaRepository.isFavorite(mangaId)
	}

	fun getFavorites(): Flow<List<Manga>> {
		return mangaRepository.observeFavorites()
	}

	suspend fun toggleFavorite(mangaId: Long, categoryId: Long = 0): Boolean {
		val isFavorite = isFavorite(mangaId)
		if (isFavorite) {
			removeFromFavorites(mangaId, categoryId)
		} else {
			addToFavorites(mangaId, categoryId)
		}
		return !isFavorite
	}
}
