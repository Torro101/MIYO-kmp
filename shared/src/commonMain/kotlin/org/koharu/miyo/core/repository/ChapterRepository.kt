package org.koharu.miyo.core.repository

import org.koharu.miyo.core.model.Chapter
import kotlinx.coroutines.flow.Flow

/**
 * Cross-platform chapter repository interface.
 */
interface ChapterRepository {
	suspend fun getChapter(id: Long): Chapter?
	suspend fun getChapters(mangaId: Long): List<Chapter>
	suspend fun getChaptersByUrl(url: String): List<Chapter>

	fun observeChapters(mangaId: Long): Flow<List<Chapter>>
	fun observeUnreadChapters(mangaId: Long): Flow<List<Chapter>>

	suspend fun markAsRead(chapterId: Long)
	suspend fun markAsUnread(chapterId: Long)
	suspend fun markAllAsRead(mangaId: Long)

	suspend fun getReadChaptersCount(mangaId: Long): Int
	suspend fun getUnreadChaptersCount(mangaId: Long): Int
}
