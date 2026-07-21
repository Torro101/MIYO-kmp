package org.koharu.miyo.core.usecase

import org.koharu.miyo.core.model.Manga
import org.koharu.miyo.core.repository.MangaRepository

/**
 * Cross-platform use case for searching manga.
 */
class SearchMangaUseCase(
	private val mangaRepository: MangaRepository
) {
	suspend operator fun invoke(query: String, source: String? = null): List<Manga> {
		return mangaRepository.searchManga(query, source)
	}

	suspend fun getPopular(source: String): List<Manga> {
		return mangaRepository.getPopularManga(source)
	}

	suspend fun getLatest(source: String): List<Manga> {
		return mangaRepository.getLatestManga(source)
	}

	suspend fun getRelated(mangaId: Long): List<Manga> {
		return mangaRepository.getRelatedManga(mangaId)
	}
}
