package org.koharu.miyo.core.model

import kotlinx.serialization.Serializable

/**
 * Cross-platform backup entry model.
 */
@Serializable
data class BackupEntry(
	val id: String = "",
	val type: BackupType,
	val timestamp: Long = 0,
	val size: Long = 0,
	val mangaCount: Int = 0,
	val chapterCount: Int = 0,
	val source: String = "local",
	val format: BackupFormat = BackupFormat.JSON
) {
	val displaySize: String
		get() = org.koharu.miyo.core.util.FormatUtils.formatFileSize(size)

	val displayDate: String
		get() = org.koharu.miyo.core.util.DateTimeUtils.formatRelativeTime(timestamp)

	val isLocal: Boolean
		get() = source == "local"

	val isRemote: Boolean
		get() = source != "local"
}

@Serializable
enum class BackupType {
	MANGA,
	HISTORY,
	FAVOURITES,
	CATEGORIES,
	STATISTICS,
	TRACKING,
	FULL;

	companion object {
		fun fromString(value: String): BackupType {
			return when (value.lowercase()) {
				"manga" -> MANGA
				"history" -> HISTORY
				"favourites", "favorites" -> FAVOURITES
				"categories" -> CATEGORIES
				"statistics" -> STATISTICS
				"tracking" -> TRACKING
				"full", "complete" -> FULL
				else -> FULL
			}
		}
	}
}

@Serializable
enum class BackupFormat {
	JSON,
	PROTOBUF,
	SQL;

	companion object {
		fun fromString(value: String): BackupFormat {
			return when (value.lowercase()) {
				"json" -> JSON
				"protobuf", "proto" -> PROTOBUF
				"sql", "database" -> SQL
				else -> JSON
			}
		}
	}
}
