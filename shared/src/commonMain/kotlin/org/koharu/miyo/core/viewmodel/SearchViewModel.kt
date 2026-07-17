package org.koharu.miyo.core.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koharu.miyo.core.model.Manga
import org.koharu.miyo.core.model.Source

/**
 * Cross-platform ViewModel for search.
 */
open class SearchViewModel : BaseViewModel() {
	private val _searchResults = MutableStateFlow<List<Manga>>(emptyList())
	val searchResults: StateFlow<List<Manga>> = _searchResults.asStateFlow()

	private val _searchQuery = MutableStateFlow("")
	val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

	private val _selectedSource = MutableStateFlow<Source?>(null)
	val selectedSource: StateFlow<Source?> = _selectedSource.asStateFlow()

	private val _sources = MutableStateFlow<List<Source>>(emptyList())
	val sources: StateFlow<List<Source>> = _sources.asStateFlow()

	private val _isEmpty = MutableStateFlow(true)
	val isEmpty: StateFlow<Boolean> = _isEmpty.asStateFlow()

	private val _hasError = MutableStateFlow(false)
	val hasError: StateFlow<Boolean> = _hasError.asStateFlow()

	protected open fun updateSearchResults(results: List<Manga>) {
		_searchResults.value = results
		_isEmpty.value = results.isEmpty()
		_hasError.value = false
	}

	protected open fun updateSearchQuery(query: String) {
		_searchQuery.value = query
	}

	protected open fun updateSelectedSource(source: Source?) {
		_selectedSource.value = source
	}

	protected open fun updateSources(sources: List<Source>) {
		_sources.value = sources
	}

	protected open fun setError(hasError: Boolean) {
		_hasError.value = hasError
		_isEmpty.value = false
	}

	protected open fun clearResults() {
		_searchResults.value = emptyList()
		_isEmpty.value = true
		_hasError.value = false
	}

	val searchResultsCount: Int
		get() = _searchResults.value.size

	val hasQuery: Boolean
		get() = _searchQuery.value.isNotBlank()
}
