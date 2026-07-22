package org.koharu.miyo.core.usecase

import org.koharu.miyo.core.domain.SyncSettings

/**
 * Cross-platform use case for managing sync operations.
 */
class ManageSyncUseCase {
	suspend fun syncData(settings: SyncSettings): SyncResult {
		// Sync logic will be implemented per platform
		return SyncResult(
			success = true,
			syncedItems = 0,
			timestamp = System.currentTimeMillis()
		)
	}

	suspend fun syncHistory(settings: SyncSettings): SyncResult {
		return SyncResult(
			success = true,
			syncedItems = 0,
			timestamp = System.currentTimeMillis()
		)
	}

	suspend fun syncFavourites(settings: SyncSettings): SyncResult {
		return SyncResult(
			success = true,
			syncedItems = 0,
			timestamp = System.currentTimeMillis()
		)
	}

	suspend fun syncSettings(settings: SyncSettings): SyncResult {
		return SyncResult(
			success = true,
			syncedItems = 0,
			timestamp = System.currentTimeMillis()
		)
	}

	suspend fun getLastSyncTimestamp(): Long {
		return 0L
	}

	suspend fun isSyncNeeded(settings: SyncSettings): Boolean {
		if (!settings.isEnabled) return false
		val lastSync = getLastSyncTimestamp()
		val elapsed = System.currentTimeMillis() - lastSync
		return elapsed >= settings.syncIntervalMs
	}
}

data class SyncResult(
	val success: Boolean,
	val syncedItems: Int,
	val timestamp: Long,
	val error: String? = null
) {
	val hasError: Boolean
		get() = error != null

	val displayMessage: String
		get() = if (success) {
			"Synced $syncedItems items"
		} else {
			error ?: "Sync failed"
		}
}
