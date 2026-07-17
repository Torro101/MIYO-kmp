package org.koharu.miyo.core.repository

import org.koharu.miyo.core.model.HistoryEntry
import kotlinx.coroutines.flow.Flow

/**
 * Cross-platform history repository interface.
 */
interface HistoryRepository {
	suspend fun getHistory(): List<HistoryEntry>
	suspend fun getHistory(mangaId: Long): HistoryEntry?
	suspend fun getRecentHistory(limit: Int = 50): List<HistoryEntry>

	fun observeHistory(): Flow<List<HistoryEntry>>
	fun observeRecentHistory(limit: Int = 50): Flow<List<HistoryEntry>>

	suspend fun addToHistory(entry: HistoryEntry)
	suspend fun updateHistory(entry: HistoryEntry)
	suspend fun removeFromHistory(mangaId: Long)
	suspend fun clearHistory()
}
