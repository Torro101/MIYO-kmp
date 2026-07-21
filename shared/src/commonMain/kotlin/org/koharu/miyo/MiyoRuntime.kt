package org.koharu.miyo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koharu.miyo.core.data.memory.InMemoryChapterRepository
import org.koharu.miyo.core.data.memory.InMemoryHistoryRepository
import org.koharu.miyo.core.data.memory.InMemoryMangaRepository
import org.koharu.miyo.core.di.expect.Platform
import org.koharu.miyo.core.di.expect.createLogger
import org.koharu.miyo.core.di.expect.initializePlatform
import org.koharu.miyo.core.model.Chapter
import org.koharu.miyo.core.model.HistoryEntry
import org.koharu.miyo.core.model.Manga
import org.koharu.miyo.core.nativeio.PlatformNativeImage
import org.koharu.miyo.core.nativeio.PlatformNativeZip
import org.koharu.miyo.core.repository.bridge.RepositoryBridges
import org.koharu.miyo.core.prefs.ListMode
import org.koharu.miyo.core.prefs.ProgressIndicatorMode
import org.koharu.miyo.core.prefs.ReaderMode as PrefsReaderMode

/**
 * Cross-platform runtime used by Android (optional debug) and iOS host apps.
 * Installs in-memory repositories so shared UI/domain works without Room/parsers.
 */
object MiyoRuntime {
	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
	private val log get() = createLogger()

	private lateinit var mangaRepo: InMemoryMangaRepository
	private lateinit var chapterRepo: InMemoryChapterRepository
	private lateinit var historyRepo: InMemoryHistoryRepository

	private val _library = MutableStateFlow<List<Manga>>(emptyList())
	val library: StateFlow<List<Manga>> = _library.asStateFlow()

	private val _favorites = MutableStateFlow<List<Manga>>(emptyList())
	val favorites: StateFlow<List<Manga>> = _favorites.asStateFlow()

	private val _history = MutableStateFlow<List<HistoryEntry>>(emptyList())
	val history: StateFlow<List<HistoryEntry>> = _history.asStateFlow()

	private val _ready = MutableStateFlow(false)
	val isReady: StateFlow<Boolean> = _ready.asStateFlow()

	// Plain flag — single-call bootstrap is enough for host apps.
	private var started = false

	fun isStarted(): Boolean = started

	fun start(useSampleData: Boolean = true) {
		if (started) return
		initializePlatform()
		mangaRepo = InMemoryMangaRepository()
		chapterRepo = InMemoryChapterRepository()
		historyRepo = InMemoryHistoryRepository()
		RepositoryBridges.install(
			manga = mangaRepo,
			chapter = chapterRepo,
			history = historyRepo,
		)
		refreshBlocking()
		started = true
		_ready.value = true
		log.i("MiyoRuntime", "started on ${Platform.name} sample=$useSampleData")
	}

	fun ensureStarted() {
		if (!started) start()
	}

	fun platformSummary(): String =
		"Miyo ${MiyoShared.SHARED_API_VERSION} · ${Platform.name} ${Platform.version} · " +
			"nativeImage=${PlatformNativeImage.isAvailable} nativeZip=${PlatformNativeZip.isAvailable}"

	fun defaultListMode(): String = ListMode.GRID.name
	fun defaultProgressMode(): String = ProgressIndicatorMode.PERCENT_READ.name
	fun defaultReaderMode(): String = PrefsReaderMode.STANDARD.name

	/** Synchronous snapshots for Swift / simple hosts (in-memory = instant). */
	fun librarySnapshot(): List<Manga> {
		ensureStarted()
		return _library.value
	}

	fun favoritesSnapshot(): List<Manga> {
		ensureStarted()
		return _favorites.value
	}

	fun historySnapshot(): List<HistoryEntry> {
		ensureStarted()
		return _history.value
	}

	fun search(query: String): List<Manga> = runBlocking {
		ensureStarted()
		mangaRepo.searchManga(query)
	}

	fun manga(id: Long): Manga? = runBlocking {
		ensureStarted()
		mangaRepo.getManga(id)
	}

	fun chapters(mangaId: Long): List<Chapter> = runBlocking {
		ensureStarted()
		chapterRepo.getChapters(mangaId)
	}

	fun setFavorite(mangaId: Long, favorite: Boolean) {
		ensureStarted()
		runBlocking {
			if (favorite) mangaRepo.addToFavorites(mangaId) else mangaRepo.removeFromFavorites(mangaId)
		}
		refreshBlocking()
	}

	fun toggleFavorite(mangaId: Long): Boolean {
		ensureStarted()
		val now = runBlocking { mangaRepo.isFavorite(mangaId) }
		setFavorite(mangaId, !now)
		return !now
	}

	fun recordProgress(mangaId: Long, chapterId: Long, page: Int, totalPages: Int) {
		ensureStarted()
		runBlocking {
			mangaRepo.updateReadingProgress(mangaId, chapterId, page, totalPages)
			historyRepo.addToHistory(
				HistoryEntry(
					mangaId = mangaId,
					chapterId = chapterId,
					timestamp = currentTimeMillis(),
					page = page,
					totalPages = totalPages,
				),
			)
			chapterRepo.markAsRead(chapterId)
		}
		refreshBlocking()
	}

	fun refreshAsync() {
		ensureStarted()
		scope.launch { refresh() }
	}

	private suspend fun refresh() {
		_library.value = mangaRepo.snapshot()
		_favorites.value = mangaRepo.favoritesSnapshot()
		_history.value = historyRepo.getHistory()
	}

	private fun refreshBlocking() = runBlocking { refresh() }

	private fun currentTimeMillis(): Long =
		org.koharu.miyo.core.di.expect.currentDateTime().toEpochMilliseconds()
}
