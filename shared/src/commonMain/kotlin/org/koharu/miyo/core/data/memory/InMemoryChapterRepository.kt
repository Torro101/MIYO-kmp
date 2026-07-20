package org.koharu.miyo.core.data.memory

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.koharu.miyo.core.data.sample.SampleCatalog
import org.koharu.miyo.core.model.Chapter
import org.koharu.miyo.core.repository.ChapterRepository

class InMemoryChapterRepository : ChapterRepository {
	private val byManga = MutableStateFlow(
		SampleCatalog.manga.associate { it.id to SampleCatalog.chaptersFor(it.id) },
	)

	override suspend fun getChapter(id: Long): Chapter? =
		byManga.value.values.flatten().find { it.id == id }

	override suspend fun getChapters(mangaId: Long): List<Chapter> =
		byManga.value[mangaId].orEmpty().sortedBy { it.number }

	override suspend fun getChaptersByUrl(url: String): List<Chapter> =
		byManga.value.values.flatten().filter { it.url == url }

	override fun observeChapters(mangaId: Long): Flow<List<Chapter>> =
		byManga.map { it[mangaId].orEmpty().sortedBy { c -> c.number } }

	override fun observeUnreadChapters(mangaId: Long): Flow<List<Chapter>> =
		observeChapters(mangaId).map { list -> list.filter { !it.isRead } }

	override suspend fun markAsRead(chapterId: Long) = setRead(chapterId, true)

	override suspend fun markAsUnread(chapterId: Long) = setRead(chapterId, false)

	override suspend fun markAllAsRead(mangaId: Long) {
		byManga.update { cur ->
			val list = cur[mangaId].orEmpty().map { it.copy(isRead = true, lastPageRead = it.totalPages) }
			cur + (mangaId to list)
		}
	}

	override suspend fun getReadChaptersCount(mangaId: Long): Int =
		getChapters(mangaId).count { it.isRead }

	override suspend fun getUnreadChaptersCount(mangaId: Long): Int =
		getChapters(mangaId).count { !it.isRead }

	private fun setRead(chapterId: Long, read: Boolean) {
		byManga.update { cur ->
			cur.mapValues { (_, list) ->
				list.map {
					if (it.id == chapterId) {
						it.copy(
							isRead = read,
							lastPageRead = if (read) it.totalPages else 0,
						)
					} else it
				}
			}
		}
	}
}
