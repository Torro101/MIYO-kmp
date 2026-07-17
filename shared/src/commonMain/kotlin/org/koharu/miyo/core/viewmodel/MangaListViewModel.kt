package org.koharu.miyo.core.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koharu.miyo.core.model.Manga

/**
 * Cross-platform ViewModel for manga lists.
 */
open class MangaListViewModel : BaseViewModel() {
	private val _mangaList = MutableStateFlow<List<Manga>>(emptyList())
	val mangaList: StateFlow<List<Manga>> = _mangaList.asStateFlow()

	private val _searchQuery = MutableStateFlow("")
	val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

	private val _isEmpty = MutableStateFlow(true)
	val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()

	protected open fun updateList(mangaList: List<Manga>) {
		_mangaList.value = mangaList
		_isEmpty.value = mangaList.isEmpty()
	}

	open fun search(query: String) {
		_searchQuery.value = query
	}

	protected open fun clearList() {
		_mangaList.value = emptyList()
		_isEmpty.value = true
	}
}
