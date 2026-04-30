package org.draken.usagi.download.ui.worker

import android.os.SystemClock
import androidx.collection.MutableObjectLongMap
import kotlinx.coroutines.delay
import org.draken.usagi.core.parser.MangaRepository
import org.draken.usagi.core.parser.ParserMangaRepository
import org.koitharu.kotatsu.parsers.model.MangaSource
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Singleton
class DownloadSlowdownDispatcher @Inject constructor(
	private val mangaRepositoryFactory: MangaRepository.Factory,
) {
	private data class SourceState(
		val lastRequestTime: Long = 0L,
		val currentDelay: Long = INITIAL_DELAY_MS,
		val consecutiveFailures: Int = 0,
	)

	private val stateMap = mutableMapOf<MangaSource, SourceState>()
	private val lock = Any()

	suspend fun delay(source: MangaSource) {
		val repo = mangaRepositoryFactory.create(source) as? ParserMangaRepository ?: return
		if (!repo.isSlowdownEnabled()) {
			return
		}

		val (lastRequest, delayMs) = synchronized(lock) {
			val state = stateMap.getOrPut(source) { SourceState() }
			val elapsed = SystemClock.elapsedRealtime()
			val lastReq = state.lastRequestTime
			stateMap[source] = state.copy(lastRequestTime = elapsed)
			Pair(lastReq, state.currentDelay)
		}

		if (lastRequest != 0L) {
			delay(max(0L, lastRequest + delayMs - SystemClock.elapsedRealtime()))
		}
	}

	fun onSuccess(source: MangaSource) {
		synchronized(lock) {
			val state = stateMap[source] ?: return
			stateMap[source] = state.copy(
				currentDelay = max(MIN_DELAY_MS, (state.currentDelay * 0.9).toLong()),
				consecutiveFailures = 0,
			)
		}
	}

	fun onRateLimited(source: MangaSource) {
		synchronized(lock) {
			val state = stateMap[source] ?: return
			val failures = state.consecutiveFailures + 1
			val multiplier = min(4.0, 1.0 + failures * 0.5)
			stateMap[source] = state.copy(
				currentDelay = min(MAX_DELAY_MS, (state.currentDelay * multiplier).toLong()),
				consecutiveFailures = failures,
			)
		}
	}

	companion object {
		private const val INITIAL_DELAY_MS = 800L
		private const val MIN_DELAY_MS = 200L
		private const val MAX_DELAY_MS = 3000L
	}
}
