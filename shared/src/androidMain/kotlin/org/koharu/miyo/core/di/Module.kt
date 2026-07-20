package org.koharu.miyo.core.di

import org.koharu.miyo.core.repository.MangaRepository
import org.koharu.miyo.core.repository.ChapterRepository
import org.koharu.miyo.core.repository.HistoryRepository
import org.koharu.miyo.core.usecase.GetMangaDetailsUseCase
import org.koharu.miyo.core.usecase.SearchMangaUseCase
import org.koharu.miyo.core.usecase.ManageFavoritesUseCase
import org.koharu.miyo.core.database.DatabaseManager

/**
 * Cross-platform dependency injection module.
 * This provides a simple way to wire up dependencies.
 */
class Module(
	private val databaseManager: DatabaseManager,
	private val mangaRepository: MangaRepository,
	private val chapterRepository: ChapterRepository,
	private val historyRepository: HistoryRepository
) {
	// Repositories
	fun provideMangaRepository(): MangaRepository = mangaRepository
	fun provideChapterRepository(): ChapterRepository = chapterRepository
	fun provideHistoryRepository(): HistoryRepository = historyRepository
	fun provideDatabaseManager(): DatabaseManager = databaseManager

	// Use cases
	fun provideGetMangaDetailsUseCase(): GetMangaDetailsUseCase {
		return GetMangaDetailsUseCase(mangaRepository)
	}

	fun provideSearchMangaUseCase(): SearchMangaUseCase {
		return SearchMangaUseCase(mangaRepository)
	}

	fun provideManageFavoritesUseCase(): ManageFavoritesUseCase {
		return ManageFavoritesUseCase(mangaRepository)
	}
}
