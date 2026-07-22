package org.koharu.miyo.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class BackupSettings(
	val isAutoBackupEnabled: Boolean = false,
	val autoBackupIntervalHours: Int = 24,
	val maxBackupCount: Int = 5,
	val backupDirectory: String = "backups",
	val includeManga: Boolean = true,
	val includeHistory: Boolean = true,
	val includeFavourites: Boolean = true,
	val includeSettings: Boolean = false,
	val includeDownloads: Boolean = false,
	val lastBackupTimestamp: Long = 0,
	val isCloudBackupEnabled: Boolean = false,
	val cloudProvider: String = "",
) {
	fun isBackupDue(nowMs: Long = org.koharu.miyo.core.di.expect.currentDateTime().toEpochMilliseconds()): Boolean {
		if (!isAutoBackupEnabled) return false
		if (lastBackupTimestamp == 0L) return true
		val elapsed = nowMs - lastBackupTimestamp
		return elapsed >= autoBackupIntervalHours * 3600 * 1000L
	}

	val autoBackupIntervalDisplay: String
		get() = when {
			autoBackupIntervalHours < 24 -> "${autoBackupIntervalHours}h"
			autoBackupIntervalHours == 24 -> "Daily"
			autoBackupIntervalHours < 168 -> "${autoBackupIntervalHours / 24}d"
			else -> "${autoBackupIntervalHours / 168}w"
		}
}
