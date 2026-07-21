package org.koharu.miyo.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class DownloadStatus {
	QUEUED,
	RUNNING,
	DOWNLOADING,
	PAUSED,
	COMPLETED,
	FAILED,
	CANCELLED,
	;

	companion object {
		fun fromString(value: String): DownloadStatus = when (value.lowercase()) {
			"queued", "pending" -> QUEUED
			"running" -> RUNNING
			"downloading", "in_progress" -> DOWNLOADING
			"paused", "suspended" -> PAUSED
			"completed", "done", "finished" -> COMPLETED
			"failed", "error" -> FAILED
			"cancelled", "canceled" -> CANCELLED
			else -> QUEUED
		}
	}
}

@Serializable
data class DownloadState(
	val id: Long = 0L,
	val mangaId: Long,
	val chapterId: Long,
	val title: String = "",
	val status: DownloadStatus = DownloadStatus.QUEUED,
	val progress: Float = 0f,
	val totalBytes: Long = 0,
	val downloadedBytes: Long = 0,
	val error: String? = null,
	val startedAt: Long = 0,
	val completedAt: Long = 0,
	val updatedAt: Long = 0,
) {
	val isComplete: Boolean get() = status == DownloadStatus.COMPLETED
	val isFailed: Boolean get() = status == DownloadStatus.FAILED
	val isPaused: Boolean get() = status == DownloadStatus.PAUSED
	val isDownloading: Boolean
		get() = status == DownloadStatus.DOWNLOADING || status == DownloadStatus.RUNNING
}
