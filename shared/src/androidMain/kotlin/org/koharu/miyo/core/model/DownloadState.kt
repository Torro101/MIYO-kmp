package org.koharu.miyo.core.model

import kotlinx.serialization.Serializable

/**
 * Cross-platform download state model.
 */
@Serializable
data class DownloadState(
	val mangaId: Long,
	val chapterId: Long,
	val status: DownloadStatus,
	val progress: Float = 0f,
	val totalBytes: Long = 0,
	val downloadedBytes: Long = 0,
	val error: String? = null,
	val startedAt: Long = 0,
	val completedAt: Long = 0
) {
	val isComplete: Boolean
		get() = status == DownloadStatus.COMPLETED

	val isFailed: Boolean
		get() = status == DownloadStatus.FAILED

	val isPaused: Boolean
		get() = status == DownloadStatus.PAUSED

	val isDownloading: Boolean
		get() = status == DownloadStatus.DOWNLOADING

	val downloadSpeed: Long
		get() = if (startedAt > 0 && downloadedBytes > 0) {
			val elapsed = kotlinx.datetime.Clock.System.now().toEpochMilliseconds() - startedAt
			if (elapsed > 0) downloadedBytes * 1000 / elapsed else 0
		} else {
			0
		}

	val estimatedTimeRemaining: Long
		get() = if (downloadSpeed > 0 && totalBytes > downloadedBytes) {
			(totalBytes - downloadedBytes) * 1000 / downloadSpeed
		} else {
			0
		}
}

@Serializable
enum class DownloadStatus {
	QUEUED,
	DOWNLOADING,
	PAUSED,
	COMPLETED,
	FAILED,
	CANCELLED;

	companion object {
		fun fromString(value: String): DownloadStatus {
			return when (value.lowercase()) {
				"queued", "pending" -> QUEUED
				"downloading", "in_progress" -> DOWNLOADING
				"paused", "suspended" -> PAUSED
				"completed", "done", "finished" -> COMPLETED
				"failed", "error" -> FAILED
				"cancelled", "canceled" -> CANCELLED
				else -> QUEUED
			}
		}
	}
}
