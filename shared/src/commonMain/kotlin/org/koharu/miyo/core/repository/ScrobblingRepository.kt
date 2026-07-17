package org.koharu.miyo.core.repository

import org.koharu.miyo.core.model.ScrobblingEntry
import org.koharu.miyo.core.model.ScrobblerType
import kotlinx.coroutines.flow.Flow

/**
 * Cross-platform scrobbling repository interface.
 */
interface ScrobblingRepository {
	suspend fun getScrobblingEntry(mangaId: Long, type: ScrobblerType): ScrobblingEntry?
	suspend fun getAllScrobblingEntries(mangaId: Long): List<ScrobblingEntry>
	suspend fun getAllScrobblingEntries(): List<ScrobblingEntry>

	fun observeScrobblingEntries(mangaId: Long): Flow<List<ScrobblingEntry>>
	fun observeAllScrobblingEntries(): Flow<List<ScrobblingEntry>>

	suspend fun updateScrobblingEntry(entry: ScrobblingEntry)
	suspend fun removeScrobblingEntry(mangaId: Long, type: ScrobblerType)

	suspend fun searchManga(query: String, type: ScrobblerType): List<ScrobblingSearchResult>
	suspend fun linkManga(mangaId: Long, type: ScrobblerType, externalId: String)
	suspend fun unlinkManga(mangaId: Long, type: ScrobblerType)

	suspend fun isLinked(mangaId: Long, type: ScrobblerType): Boolean
}

data class ScrobblingSearchResult(
	val id: String,
	val title: String,
	val coverUrl: String = "",
	val year: Int = 0,
	val score: Float = 0f,
	val episodes: Int = 0,
	val status: String = ""
)
