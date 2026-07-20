package org.koharu.miyo.core.data.memory

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.koharu.miyo.core.model.HistoryEntry
import org.koharu.miyo.core.repository.HistoryRepository

class InMemoryHistoryRepository(
	seed: List<HistoryEntry> = listOf(
		HistoryEntry(mangaId = 1L, chapterId = 1003L, timestamp = 1_720_000_100_000L, page = 8, totalPages = 20),
		HistoryEntry(mangaId = 4L, chapterId = 4010L, timestamp = 1_720_000_200_000L, page = 3, totalPages = 20),
	),
) : HistoryRepository {
	private val store = MutableStateFlow(seed.associateBy { it.mangaId })

	override suspend fun getHistory(): List<HistoryEntry> =
		store.value.values.sortedByDescending { it.timestamp }

	override suspend fun getHistory(mangaId: Long): HistoryEntry? = store.value[mangaId]

	override suspend fun getRecentHistory(limit: Int): List<HistoryEntry> =
		getHistory().take(limit)

	override fun observeHistory(): Flow<List<HistoryEntry>> =
		store.map { it.values.sortedByDescending { e -> e.timestamp } }

	override fun observeRecentHistory(limit: Int): Flow<List<HistoryEntry>> =
		observeHistory().map { it.take(limit) }

	override suspend fun addToHistory(entry: HistoryEntry) {
		store.update { it + (entry.mangaId to entry) }
	}

	override suspend fun updateHistory(entry: HistoryEntry) = addToHistory(entry)

	override suspend fun removeFromHistory(mangaId: Long) {
		store.update { it - mangaId }
	}

	override suspend fun clearHistory() {
		store.value = emptyMap()
	}
}
