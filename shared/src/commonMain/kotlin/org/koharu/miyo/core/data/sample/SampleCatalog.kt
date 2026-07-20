package org.koharu.miyo.core.data.sample

import org.koharu.miyo.core.model.Chapter
import org.koharu.miyo.core.model.ContentRating
import org.koharu.miyo.core.model.Manga
import org.koharu.miyo.core.model.MangaState

/**
 * Demo catalog so iOS (and Android debug) can exercise shared APIs
 * without parsers / Room.
 */
object SampleCatalog {
	val manga: List<Manga> = listOf(
		Manga(
			id = 1L,
			title = "Aurora Library",
			url = "https://example.local/manga/aurora",
			source = "demo",
			coverUrl = "",
			rating = 4.6f,
			contentRating = ContentRating.SAFE,
			state = MangaState.ONGOING,
			author = "Miyo Demo",
			artist = "Miyo Demo",
			description = "A sample ongoing series used to validate the shared KMP library layer on iOS.",
			tags = listOf("demo", "adventure"),
			isFavorite = true,
			lastUpdated = 1_720_000_000_000L,
			totalChapters = 12,
			readChapters = 3,
		),
		Manga(
			id = 2L,
			title = "Harbor Lights",
			url = "https://example.local/manga/harbor",
			source = "demo",
			rating = 4.2f,
			contentRating = ContentRating.SUGGESTIVE,
			state = MangaState.COMPLETED,
			author = "Coast Studio",
			description = "Completed demo title for list/details navigation.",
			tags = listOf("demo", "slice-of-life"),
			isFavorite = false,
			totalChapters = 24,
			readChapters = 24,
		),
		Manga(
			id = 3L,
			title = "Circuit Garden",
			url = "https://example.local/manga/circuit",
			source = "demo",
			rating = 3.9f,
			contentRating = ContentRating.SAFE,
			state = MangaState.HIATUS,
			author = "Pixel Grove",
			description = "Hiatus demo entry for status badges and filters.",
			tags = listOf("demo", "sci-fi"),
			totalChapters = 8,
			readChapters = 1,
		),
		Manga(
			id = 4L,
			title = "Paper Crane Express",
			url = "https://example.local/manga/crane",
			source = "demo",
			rating = 4.8f,
			contentRating = ContentRating.SAFE,
			state = MangaState.ONGOING,
			author = "Fold Works",
			description = "Another ongoing demo for search and favorites.",
			tags = listOf("demo", "drama"),
			isFavorite = true,
			totalChapters = 40,
			readChapters = 10,
		),
	)

	fun chaptersFor(mangaId: Long): List<Chapter> {
		val m = manga.find { it.id == mangaId } ?: return emptyList()
		return (1..m.totalChapters).map { n ->
			Chapter(
				id = mangaId * 1000 + n,
				mangaId = mangaId,
				title = "Chapter $n",
				url = "${m.url}/ch/$n",
				number = n.toFloat(),
				isRead = n <= m.readChapters,
				lastPageRead = if (n <= m.readChapters) 20 else 0,
				totalPages = 20,
				source = m.source,
			)
		}
	}
}
