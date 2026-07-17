package org.koharu.miyo.core.repository

import org.koharu.miyo.core.model.Manga
import kotlinx.coroutines.flow.Flow

/**
 * Cross-platform manga repository interface.
 */
interface MangaRepository {
	suspend fun getManga(id: Long): Manga?
	suspend fun getMangaByUrl(url: String, source: String): Manga?
	suspend fun searchManga(query: String, source: String? = null): List<Manga>
	suspend fun getPopularManga(source: String): List<Manga>
	suspend fun getLatestManga(source: String): List<Manga>
	suspend fun getRelatedManga(mangaId: Long): List<Manga>

	fun observeManga(id: Long): Flow<Manga?>
	fun observeFavorites(): Flow<List<Manga>>
	fun observeRecentlyRead(): Flow<List<Manga>>

	suspend fun addToFavorites(mangaId: Long, categoryId: Long = 0)
	suspend fun removeFromFavorites(mangaId: Long, categoryId: Long = 0)
	suspend fun isFavorite(mangaId: Long): Boolean

	suspend fun updateReadingProgress(mangaId: Long, chapterId: Long, page: Int, totalPages: Int)
}
