package org.koharu.miyo.core.database

import org.koharu.miyo.core.model.Manga
import org.koharu.miyo.core.model.Chapter
import org.koharu.miyo.core.model.HistoryEntry
import kotlinx.coroutines.flow.Flow

/**
 * Cross-platform database manager interface.
 * Platform-specific implementations will handle the actual database operations.
 */
interface DatabaseManager {
	// Manga operations
	suspend fun insertManga(manga: Manga)
	suspend fun updateManga(manga: Manga)
	suspend fun deleteManga(id: Long)
	suspend fun getManga(id: Long): Manga?
	suspend fun getAllManga(): List<Manga>
	fun observeAllManga(): Flow<List<Manga>>

	// Chapter operations
	suspend fun insertChapter(chapter: Chapter)
	suspend fun updateChapter(chapter: Chapter)
	suspend fun deleteChapter(id: Long)
	suspend fun getChapter(id: Long): Chapter?
	suspend fun getChaptersByMangaId(mangaId: Long): List<Chapter>
	fun observeChaptersByMangaId(mangaId: Long): Flow<List<Chapter>>

	// History operations
	suspend fun insertHistoryEntry(entry: HistoryEntry)
	suspend fun updateHistoryEntry(entry: HistoryEntry)
	suspend fun deleteHistoryEntry(mangaId: Long)
	suspend fun getHistoryEntry(mangaId: Long): HistoryEntry?
	suspend fun getAllHistoryEntries(): List<HistoryEntry>
	fun observeAllHistoryEntries(): Flow<List<HistoryEntry>>

	// Database management
	suspend fun clearAll()
	suspend fun getDatabaseSize(): Long
	suspend fun optimize()
}
