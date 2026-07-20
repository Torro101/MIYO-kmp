package org.koharu.miyo.core.repository.bridge

import org.koharu.miyo.core.repository.ChapterRepository
import org.koharu.miyo.core.repository.DownloadRepository
import org.koharu.miyo.core.repository.HistoryRepository
import org.koharu.miyo.core.repository.MangaRepository
import org.koharu.miyo.core.repository.ScrobblingRepository
import org.koharu.miyo.core.repository.SourceRepository
import org.koharu.miyo.core.repository.TagRepository

/**
 * Host platforms register concrete implementations of common repository interfaces here.
 * Android wires Room / parsers-backed impls at Application startup.
 * iOS may register network/DTO-backed stubs until full feature parity.
 */
object RepositoryBridges {
	@Suppress("ObjectPropertyName")
	private var _manga: MangaRepository? = null
	private var _chapter: ChapterRepository? = null
	private var _history: HistoryRepository? = null
	private var _download: DownloadRepository? = null
	private var _scrobbling: ScrobblingRepository? = null
	private var _source: SourceRepository? = null
	private var _tag: TagRepository? = null

	fun install(
		manga: MangaRepository? = null,
		chapter: ChapterRepository? = null,
		history: HistoryRepository? = null,
		download: DownloadRepository? = null,
		scrobbling: ScrobblingRepository? = null,
		source: SourceRepository? = null,
		tag: TagRepository? = null,
	) {
		manga?.let { _manga = it }
		chapter?.let { _chapter = it }
		history?.let { _history = it }
		download?.let { _download = it }
		scrobbling?.let { _scrobbling = it }
		source?.let { _source = it }
		tag?.let { _tag = it }
	}

	val manga: MangaRepository
		get() = _manga ?: error("MangaRepository not installed — call RepositoryBridges.install() from the host app")
	val chapter: ChapterRepository
		get() = _chapter ?: error("ChapterRepository not installed")
	val history: HistoryRepository
		get() = _history ?: error("HistoryRepository not installed")
	val download: DownloadRepository
		get() = _download ?: error("DownloadRepository not installed")
	val scrobbling: ScrobblingRepository
		get() = _scrobbling ?: error("ScrobblingRepository not installed")
	val source: SourceRepository
		get() = _source ?: error("SourceRepository not installed")
	val tag: TagRepository
		get() = _tag ?: error("TagRepository not installed")

	fun isMangaInstalled(): Boolean = _manga != null
}
