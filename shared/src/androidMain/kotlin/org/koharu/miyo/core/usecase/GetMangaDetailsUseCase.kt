package org.koharu.miyo.core.usecase

import org.koharu.miyo.core.model.Manga
import org.koharu.miyo.core.repository.MangaRepository

/**
 * Cross-platform use case for getting manga details.
 */
class GetMangaDetailsUseCase(
	private val mangaRepository: MangaRepository
) {
	suspend operator fun invoke(mangaId: Long): Manga? {
		return mangaRepository.getManga(mangaId)
	}

	suspend operator fun invoke(url: String, source: String): Manga? {
		return mangaRepository.getMangaByUrl(url, source)
	}
}
