package org.koharu.miyo.core.usecase

import org.koharu.miyo.core.model.DownloadState
import org.koharu.miyo.core.model.DownloadStatus
import org.koharu.miyo.core.repository.DownloadRepository
import kotlinx.coroutines.flow.Flow

/**
 * Cross-platform use case for managing downloads.
 */
class ManageDownloadsUseCase(
	private val downloadRepository: DownloadRepository
) {
	suspend fun startDownload(mangaId: Long, chapterId: Long) {
		downloadRepository.startDownload(mangaId, chapterId)
	}

	suspend fun pauseDownload(mangaId: Long, chapterId: Long) {
		downloadRepository.pauseDownload(mangaId, chapterId)
	}

	suspend fun resumeDownload(mangaId: Long, chapterId: Long) {
		downloadRepository.resumeDownload(mangaId, chapterId)
	}

	suspend fun cancelDownload(mangaId: Long, chapterId: Long) {
		downloadRepository.cancelDownload(mangaId, chapterId)
	}

	suspend fun retryDownload(mangaId: Long, chapterId: Long) {
		downloadRepository.retryDownload(mangaId, chapterId)
	}

	suspend fun deleteDownload(mangaId: Long, chapterId: Long) {
		downloadRepository.deleteDownload(mangaId, chapterId)
	}

	suspend fun deleteAllDownloads() {
		downloadRepository.deleteAllDownloads()
	}

	suspend fun isChapterDownloaded(mangaId: Long, chapterId: Long): Boolean {
		return downloadRepository.isChapterDownloaded(mangaId, chapterId)
	}

	suspend fun getDownloadedChapters(mangaId: Long): List<Long> {
		return downloadRepository.getDownloadedChapters(mangaId)
	}

	fun observeDownloads(mangaId: Long): Flow<List<DownloadState>> {
		return downloadRepository.observeDownloads(mangaId)
	}

	fun observeAllDownloads(): Flow<List<DownloadState>> {
		return downloadRepository.observeAllDownloads()
	}

	fun observeDownloading(): Flow<List<DownloadState>> {
		return downloadRepository.observeDownloading()
	}

	suspend fun pauseAllDownloads() {
		val downloading = downloadRepository.getAllDownloads()
			.filter { it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.QUEUED }
		downloading.forEach { download ->
			downloadRepository.pauseDownload(download.mangaId, download.chapterId)
		}
	}

	suspend fun resumeAllDownloads() {
		val paused = downloadRepository.getAllDownloads()
			.filter { it.status == DownloadStatus.PAUSED }
		paused.forEach { download ->
			downloadRepository.resumeDownload(download.mangaId, download.chapterId)
		}
	}
}
