package org.koharu.miyo

import org.koharu.miyo.core.di.expect.Platform
import org.koharu.miyo.core.di.expect.initializePlatform
import org.koharu.miyo.core.model.Chapter
import org.koharu.miyo.core.model.HistoryEntry
import org.koharu.miyo.core.model.Manga
import org.koharu.miyo.core.nativeio.PlatformNativeImage
import org.koharu.miyo.core.nativeio.PlatformNativeZip

/**
 * Stable entry surface for host apps (Android shell, iOS app) and smoke tests.
 * Prefer [MiyoRuntime] for library/history operations.
 */
object MiyoShared {
	const val SHARED_API_VERSION: Int = 2

	fun initialize() {
		MiyoRuntime.start()
	}

	fun hello(): String = "Miyo shared KMP ready on ${platformName()}"

	fun platformName(): String = Platform.name

	fun platformVersion(): String = Platform.version

	fun isDebug(): Boolean = Platform.isDebug

	fun nativeImageAvailable(): Boolean = PlatformNativeImage.isAvailable

	fun nativeZipAvailable(): Boolean = PlatformNativeZip.isAvailable

	fun platformSummary(): String = MiyoRuntime.platformSummary()

	fun library(): List<Manga> = MiyoRuntime.librarySnapshot()

	fun favorites(): List<Manga> = MiyoRuntime.favoritesSnapshot()

	fun history(): List<HistoryEntry> = MiyoRuntime.historySnapshot()

	fun search(query: String): List<Manga> = MiyoRuntime.search(query)

	fun manga(id: Long): Manga? = MiyoRuntime.manga(id)

	fun chapters(mangaId: Long): List<Chapter> = MiyoRuntime.chapters(mangaId)

	fun toggleFavorite(mangaId: Long): Boolean = MiyoRuntime.toggleFavorite(mangaId)

	fun setFavorite(mangaId: Long, favorite: Boolean) = MiyoRuntime.setFavorite(mangaId, favorite)

	fun recordProgress(mangaId: Long, chapterId: Long, page: Int, totalPages: Int) =
		MiyoRuntime.recordProgress(mangaId, chapterId, page, totalPages)
}
