package org.koharu.miyo.core.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koharu.miyo.core.model.HistoryEntry

/**
 * Cross-platform ViewModel for reading history.
 */
open class HistoryViewModel : BaseViewModel() {
	private val _history = MutableStateFlow<List<HistoryEntry>>(emptyList())
	val history: StateFlow<List<HistoryEntry>> = _history.asStateFlow()

	private val _recentHistory = MutableStateFlow<List<HistoryEntry>>(emptyList())
	val recentHistory: StateFlow<List<HistoryEntry>> = _recentHistory.asStateFlow()

	private val _isEmpty = MutableStateFlow(true)
	val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()

	protected open fun updateHistory(history: List<HistoryEntry>) {
		_history.value = history
		_isEmpty.value = history.isEmpty()
	}

	protected open fun updateRecentHistory(history: List<HistoryEntry>) {
		_recentHistory.value = history
	}

	protected open fun addToHistory(entry: HistoryEntry) {
		val current = _history.value.toMutableList()
		val existingIndex = current.indexOfFirst { it.mangaId == entry.mangaId }
		if (existingIndex >= 0) {
			current[existingIndex] = entry
		} else {
			current.add(0, entry)
		}
		_history.value = current
		_isEmpty.value = current.isEmpty()
	}

	protected open fun removeFromHistory(mangaId: Long) {
		val current = _history.value.filter { it.mangaId != mangaId }
		_history.value = current
		_isEmpty.value = current.isEmpty()
	}

	protected open fun clearHistory() {
		_history.value = emptyList()
		_recentHistory.value = emptyList()
		_isEmpty.value = true
	}

	val historyCount: Int
		get() = _history.value.size

	val recentHistoryCount: Int
		get() = _recentHistory.value.size
}
