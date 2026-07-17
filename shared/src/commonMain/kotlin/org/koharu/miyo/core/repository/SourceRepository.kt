package org.koharu.miyo.core.repository

import org.koharu.miyo.core.model.Source
import kotlinx.coroutines.flow.Flow

/**
 * Cross-platform source repository interface.
 */
interface SourceRepository {
	suspend fun getSources(): List<Source>
	suspend fun getSource(id: String): Source?
	suspend fun getEnabledSources(): List<Source>
	suspend fun getPinnedSources(): List<Source>

	fun observeSources(): Flow<List<Source>>
	fun observeEnabledSources(): Flow<List<Source>>

	suspend fun enableSource(id: String)
	suspend fun disableSource(id: String)
	suspend fun pinSource(id: String)
	suspend fun unpinSource(id: String)

	suspend fun isSourceEnabled(id: String): Boolean
	suspend fun isSourcePinned(id: String): Boolean

	suspend fun searchSources(query: String): List<Source>
	suspend fun getSourcesByLanguage(language: String): List<Source>
}
