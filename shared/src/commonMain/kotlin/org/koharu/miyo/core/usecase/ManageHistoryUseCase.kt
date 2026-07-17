package org.koharu.miyo.core.usecase

import org.koharu.miyo.core.model.HistoryEntry
import org.koharu.miyo.core.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Cross-platform use case for managing reading history.
 */
class ManageHistoryUseCase(
	private val historyRepository: HistoryRepository
) {
	suspend fun addToHistory(entry: HistoryEntry) {
		historyRepository.addToHistory(entry)
	}

	suspend fun updateHistory(entry: HistoryEntry) {
		historyRepository.updateHistory(entry)
	}

	suspend fun removeFromHistory(mangaId: Long) {
		historyRepository.removeFromHistory(mangaId)
	}

	suspend fun clearHistory() {
		historyRepository.clearHistory()
	}

	suspend fun getHistory(): List<HistoryEntry> {
		return historyRepository.getHistory()
	}

	suspend fun getRecentHistory(limit: Int = 50): List<HistoryEntry> {
		return historyRepository.getRecentHistory(limit)
	}

	fun observeHistory(): Flow<List<HistoryEntry>> {
		return historyRepository.observeHistory()
	}

	fun observeRecentHistory(limit: Int = 50): Flow<List<HistoryEntry>> {
		return historyRepository.observeRecentHistory(limit)
	}

	suspend fun getHistoryForManga(mangaId: Long): HistoryEntry? {
		return historyRepository.getHistory(mangaId)
	}
}
