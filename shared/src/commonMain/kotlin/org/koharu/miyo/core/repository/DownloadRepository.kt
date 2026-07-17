package org.koharu.miyo.core.repository

import org.koharu.miyo.core.model.DownloadState
import kotlinx.coroutines.flow.Flow

/**
 * Cross-platform download repository interface.
 */
interface DownloadRepository {
	suspend fun getDownload(mangaId: Long, chapterId: Long): DownloadState?
	suspend fun getDownloads(mangaId: Long): List<DownloadState>
	suspend fun getAllDownloads(): List<DownloadState>

	fun observeDownloads(mangaId: Long): Flow<List<DownloadState>>
	fun observeAllDownloads(): Flow<List<DownloadState>>
	fun observeDownloading(): Flow<List<DownloadState>>

	suspend fun startDownload(mangaId: Long, chapterId: Long)
	suspend fun pauseDownload(mangaId: Long, chapterId: Long)
	suspend fun resumeDownload(mangaId: Long, chapterId: Long)
	suspend fun cancelDownload(mangaId: Long, chapterId: Long)
	suspend fun retryDownload(mangaId: Long, chapterId: Long)

	suspend fun deleteDownload(mangaId: Long, chapterId: Long)
	suspend fun deleteAllDownloads()

	suspend fun getDownloadedChapters(mangaId: Long): List<Long>
	suspend fun isChapterDownloaded(mangaId: Long, chapterId: Long): Boolean
}
