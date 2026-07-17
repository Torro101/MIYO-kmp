package org.koharu.miyo.core.usecase

import org.koharu.miyo.core.model.ScrobblingEntry
import org.koharu.miyo.core.model.ScrobblerType
import org.koharu.miyo.core.repository.ScrobblingRepository
import kotlinx.coroutines.flow.Flow

/**
 * Cross-platform use case for managing scrobbling (tracking) entries.
 */
class ManageScrobblingUseCase(
	private val scrobblingRepository: ScrobblingRepository
) {
	suspend fun updateScrobblingEntry(entry: ScrobblingEntry) {
		scrobblingRepository.updateScrobblingEntry(entry)
	}

	suspend fun removeScrobblingEntry(mangaId: Long, type: ScrobblerType) {
		scrobblingRepository.removeScrobblingEntry(mangaId, type)
	}

	suspend fun isLinked(mangaId: Long, type: ScrobblerType): Boolean {
		return scrobblingRepository.isLinked(mangaId, type)
	}

	suspend fun linkManga(mangaId: Long, type: ScrobblerType, externalId: String) {
		scrobblingRepository.linkManga(mangaId, type, externalId)
	}

	suspend fun unlinkManga(mangaId: Long, type: ScrobblerType) {
		scrobblingRepository.unlinkManga(mangaId, type)
	}

	suspend fun searchManga(query: String, type: ScrobblerType): List<org.koharu.miyo.core.repository.ScrobblingSearchResult> {
		return scrobblingRepository.searchManga(query, type)
	}

	suspend fun getScrobblingEntry(mangaId: Long, type: ScrobblerType): ScrobblingEntry? {
		return scrobblingRepository.getScrobblingEntry(mangaId, type)
	}

	suspend fun getAllScrobblingEntries(mangaId: Long): List<ScrobblingEntry> {
		return scrobblingRepository.getAllScrobblingEntries(mangaId)
	}

	fun observeScrobblingEntries(mangaId: Long): Flow<List<ScrobblingEntry>> {
		return scrobblingRepository.observeScrobblingEntries(mangaId)
	}

	fun observeAllScrobblingEntries(): Flow<List<ScrobblingEntry>> {
		return scrobblingRepository.observeAllScrobblingEntries()
	}

	suspend fun updateReadingProgress(mangaId: Long, chaptersRead: Int, type: ScrobblerType) {
		val entry = scrobblingRepository.getScrobblingEntry(mangaId, type) ?: return
		scrobblingRepository.updateScrobblingEntry(
			entry.copy(
				chaptersRead = chaptersRead,
				lastUpdated = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
			)
		)
	}
}
