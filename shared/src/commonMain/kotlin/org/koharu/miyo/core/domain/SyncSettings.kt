package org.koharu.miyo.core.domain

import kotlinx.serialization.Serializable

/**
 * Cross-platform sync settings model.
 */
@Serializable
data class SyncSettings(
	val isEnabled: Boolean = false,
	val serverUrl: String = "",
	val apiKey: String = "",
	val syncIntervalMinutes: Int = 60,
	val syncOnWifiOnly: Boolean = true,
	val syncHistory: Boolean = true,
	val syncFavourites: Boolean = true,
	val syncSettings: Boolean = true,
	val syncDownloads: Boolean = false,
	val lastSyncTimestamp: Long = 0,
	val deviceName: String = ""
) {
	val isConfigured: Boolean
		get() = serverUrl.isNotBlank() && apiKey.isNotBlank()

	val hasCredentials: Boolean
		get() = apiKey.isNotBlank()

	val syncIntervalMs: Long
		get() = syncIntervalMinutes * 60 * 1000L

	val shouldSync: Boolean
		get() = isEnabled && isConfigured

	val displaySyncInterval: String
		get() = when {
			syncIntervalMinutes < 60 -> "${syncIntervalMinutes}m"
			syncIntervalMinutes == 60 -> "1h"
			syncIntervalMinutes < 1440 -> "${syncIntervalMinutes / 60}h"
			else -> "${syncIntervalMinutes / 1440}d"
		}
}
