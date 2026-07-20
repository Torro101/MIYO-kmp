package org.koharu.miyo.core.data.memory

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.koharu.miyo.core.data.sample.SampleCatalog
import org.koharu.miyo.core.model.Manga
import org.koharu.miyo.core.repository.MangaRepository

/**
 * In-memory [MangaRepository] for iOS MVP and Android debug without Room/parsers.
 */
class InMemoryMangaRepository(
	seed: List<Manga> = SampleCatalog.manga,
) : MangaRepository {
	private val store = MutableStateFlow(seed.associateBy { it.id })

	private fun all(): List<Manga> = store.value.values.sortedBy { it.title.lowercase() }

	override suspend fun getManga(id: Long): Manga? = store.value[id]

	override suspend fun getMangaByUrl(url: String, source: String): Manga? =
		store.value.values.find { it.url == url && it.source == source }

	override suspend fun searchManga(query: String, source: String?): List<Manga> {
		val q = query.trim().lowercase()
		if (q.isEmpty()) return all().filter { source == null || it.source == source }
		return all().filter { m ->
			(source == null || m.source == source) &&
				(m.title.lowercase().contains(q) ||
					m.author.lowercase().contains(q) ||
					m.tags.any { it.lowercase().contains(q) })
		}
	}

	override suspend fun getPopularManga(source: String): List<Manga> =
		all().filter { it.source == source || source.isBlank() }.sortedByDescending { it.rating }

	override suspend fun getLatestManga(source: String): List<Manga> =
		all().filter { it.source == source || source.isBlank() }.sortedByDescending { it.lastUpdated }

	override suspend fun getRelatedManga(mangaId: Long): List<Manga> {
		val base = store.value[mangaId] ?: return emptyList()
		return all().filter { it.id != mangaId && it.tags.any { t -> t in base.tags } }.take(5)
	}

	override fun observeManga(id: Long): Flow<Manga?> = store.map { it[id] }

	override fun observeFavorites(): Flow<List<Manga>> =
		store.map { map -> map.values.filter { it.isFavorite }.sortedBy { it.title.lowercase() } }

	override fun observeRecentlyRead(): Flow<List<Manga>> =
		store.map { map -> map.values.filter { it.readChapters > 0 }.sortedByDescending { it.lastUpdated } }

	override suspend fun addToFavorites(mangaId: Long, categoryId: Long) {
		store.update { cur ->
			val m = cur[mangaId] ?: return@update cur
			cur + (mangaId to m.copy(isFavorite = true))
		}
	}

	override suspend fun removeFromFavorites(mangaId: Long, categoryId: Long) {
		store.update { cur ->
			val m = cur[mangaId] ?: return@update cur
			cur + (mangaId to m.copy(isFavorite = false))
		}
	}

	override suspend fun isFavorite(mangaId: Long): Boolean = store.value[mangaId]?.isFavorite == true

	override suspend fun updateReadingProgress(mangaId: Long, chapterId: Long, page: Int, totalPages: Int) {
		store.update { cur ->
			val m = cur[mangaId] ?: return@update cur
			val read = (m.readChapters).coerceAtLeast(1)
			cur + (mangaId to m.copy(readChapters = read, lastUpdated = m.lastUpdated))
		}
	}

	fun snapshot(): List<Manga> = all()

	fun favoritesSnapshot(): List<Manga> = all().filter { it.isFavorite }
}
