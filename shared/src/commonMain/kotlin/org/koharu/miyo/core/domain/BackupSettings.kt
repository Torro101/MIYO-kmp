package org.koharu.miyo.core.domain

import kotlinx.serialization.Serializable

/**
 * Cross-platform backup settings model.
 */
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
	val cloudProvider: String = ""
) {
	val isBackupDue: Boolean
		get() {
			if (!isAutoBackupEnabled) return false
			if (lastBackupTimestamp == 0L) return true
			val elapsed = kotlinx.datetime.Clock.System.now().toEpochMilliseconds() - lastBackupTimestamp
			return elapsed >= autoBackupIntervalHours * 3600 * 1000L
		}

	val autoBackupIntervalDisplay: String
		get() = when {
			autoBackupIntervalHours < 24 -> "${autoBackupIntervalHours}h"
			autoBackupIntervalHours == 24 -> "Daily"
			autoBackupIntervalHours < 168 -> "${autoBackupIntervalHours / 24}d"
			else -> "${autoBackupIntervalHours / 168}w"
		}

	val lastBackupDisplay: String
		get() = if (lastBackupTimestamp > 0) {
			org.koharu.miyo.core.util.DateTimeUtils.formatRelativeTime(lastBackupTimestamp)
		} else {
			"Never"
		}
}
