package org.koharu.miyo.core.usecase

import org.koharu.miyo.core.model.Source
import org.koharu.miyo.core.repository.SourceRepository
import kotlinx.coroutines.flow.Flow

/**
 * Cross-platform use case for managing sources.
 */
class ManageSourcesUseCase(
	private val sourceRepository: SourceRepository
) {
	suspend fun getSources(): List<Source> {
		return sourceRepository.getSources()
	}

	suspend fun getSource(id: String): Source? {
		return sourceRepository.getSource(id)
	}

	suspend fun getEnabledSources(): List<Source> {
		return sourceRepository.getEnabledSources()
	}

	suspend fun getPinnedSources(): List<Source> {
		return sourceRepository.getPinnedSources()
	}

	suspend fun enableSource(id: String) {
		sourceRepository.enableSource(id)
	}

	suspend fun disableSource(id: String) {
		sourceRepository.disableSource(id)
	}

	suspend fun pinSource(id: String) {
		sourceRepository.pinSource(id)
	}

	suspend fun unpinSource(id: String) {
		sourceRepository.unpinSource(id)
	}

	suspend fun toggleSource(id: String): Boolean {
		val isEnabled = sourceRepository.isSourceEnabled(id)
		if (isEnabled) {
			sourceRepository.disableSource(id)
		} else {
			sourceRepository.enableSource(id)
		}
		return !isEnabled
	}

	suspend fun togglePin(id: String): Boolean {
		val isPinned = sourceRepository.isSourcePinned(id)
		if (isPinned) {
			sourceRepository.unpinSource(id)
		} else {
			sourceRepository.pinSource(id)
		}
		return !isPinned
	}

	suspend fun searchSources(query: String): List<Source> {
		return sourceRepository.searchSources(query)
	}

	suspend fun getSourcesByLanguage(language: String): List<Source> {
		return sourceRepository.getSourcesByLanguage(language)
	}

	fun observeSources(): Flow<List<Source>> {
		return sourceRepository.observeSources()
	}

	fun observeEnabledSources(): Flow<List<Source>> {
		return sourceRepository.observeEnabledSources()
	}
}
