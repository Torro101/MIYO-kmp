package org.koharu.miyo.core.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koharu.miyo.core.model.DownloadState
import org.koharu.miyo.core.model.DownloadStatus

/**
 * Cross-platform ViewModel for downloads.
 */
open class DownloadViewModel : BaseViewModel() {
	private val _downloads = MutableStateFlow<List<DownloadState>>(emptyList())
	val downloads: StateFlow<List<DownloadState>> = _downloads.asStateFlow()

	private val _activeDownloads = MutableStateFlow<List<DownloadState>>(emptyList())
	val activeDownloads: StateFlow<List<DownloadState>> = _activeDownloads.asStateFlow()

	private val _downloadProgress = MutableStateFlow<Map<Long, Float>>(emptyMap())
	val downloadProgress: StateFlow<Map<Long, Float>> = _downloadProgress.asStateFlow()

	protected open fun updateDownloads(downloads: List<DownloadState>) {
		_downloads.value = downloads
		_activeDownloads.value = downloads.filter {
			it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.QUEUED
		}
		updateProgressMap(downloads)
	}

	private fun updateProgressMap(downloads: List<DownloadState>) {
		val progressMap = downloads.associate { it.chapterId to it.progress }
		_downloadProgress.value = progressMap
	}

	protected open fun updateDownloadProgress(chapterId: Long, progress: Float) {
		val current = _downloadProgress.value.toMutableMap()
		current[chapterId] = progress
		_downloadProgress.value = current
	}

	protected open fun removeDownload(chapterId: Long) {
		val current = _downloads.value.filter { it.chapterId != chapterId }
		_downloads.value = current
		updateProgressMap(current)
	}

	val totalDownloads: Int
		get() = _downloads.value.size

	val completedDownloads: Int
		get() = _downloads.value.count { it.status == DownloadStatus.COMPLETED }

	val failedDownloads: Int
		get() = _downloads.value.count { it.status == DownloadStatus.FAILED }

	val downloadingCount: Int
		get() = _activeDownloads.value.size
}
