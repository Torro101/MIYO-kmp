package org.koharu.miyo.core.domain

/**
 * Cross-platform application constants.
 */
object AppConstants {
	// App info
	const val APP_NAME = "Miyo"
	const val APP_VERSION = "1.0.0"
	const val APP_BUILD_NUMBER = 1

	// Database
	const val DATABASE_NAME = "miyo_database"
	const val DATABASE_VERSION = 1

	// Network
	const val DEFAULT_TIMEOUT_MS = 30_000L
	const val MAX_RETRIES = 3
	const val RATE_LIMIT_REQUESTS = 10
	const val RATE_LIMIT_WINDOW_MS = 60_000L

	// Cache
	const val MAX_CACHE_SIZE = 1000
	const val CACHE_EXPIRATION_MS = 30 * 60 * 1000L // 30 minutes
	const val DISK_CACHE_SIZE = 100L * 1024 * 1024 // 100 MB

	// Download
	const val MAX_CONCURRENT_DOWNLOADS = 3
	const val DOWNLOAD_BUFFER_SIZE = 8192
	const val MIN_STORAGE_MB = 100

	// Reader
	const val MIN_BRIGHTNESS = 0
	const val MAX_BRIGHTNESS = 100
	const val DEFAULT_BRIGHTNESS = 50
	const val PAGE_PRELOAD_COUNT = 2
	const val ZOOM_MIN = 0.5f
	const val ZOOM_MAX = 3.0f
	const val ZOOM_DEFAULT = 1.0f

	// History
	const val MAX_HISTORY_SIZE = 10000
	const val HISTORY_CLEANUP_DAYS = 90

	// Search
	const val MIN_SEARCH_LENGTH = 2
	const val MAX_SEARCH_RESULTS = 100
	const val SEARCH_DEBOUNCE_MS = 300L

	// Backup
	const val MAX_BACKUP_SIZE_MB = 100
	const val BACKUP_RETENTION_DAYS = 30

	// Notification
	const val NOTIFICATION_CHANNEL_ID = "miyo_notifications"
	const val NOTIFICATION_CHANNEL_NAME = "Miyo Notifications"

	// Widget
	const val WIDGET_UPDATE_INTERVAL_MS = 15 * 60 * 1000L // 15 minutes

	// File paths
	const val MANGA_DIR = "manga"
	const val CHAPTER_DIR = "chapters"
	const val COVER_DIR = "covers"
	const val BACKUP_DIR = "backups"
	const val LOG_DIR = "logs"
	const val CACHE_DIR = "cache"
	const val TEMP_DIR = "temp"
}
