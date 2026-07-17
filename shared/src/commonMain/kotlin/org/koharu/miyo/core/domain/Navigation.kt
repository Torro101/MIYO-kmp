package org.koharu.miyo.core.domain

import kotlinx.serialization.Serializable

/**
 * Cross-platform navigation route definitions.
 */
@Serializable
sealed class Route {
	@Serializable
	data object Home : Route()

	@Serializable
	data class MangaDetails(val mangaId: Long) : Route()

	@Serializable
	data class Reader(val mangaId: Long, val chapterId: Long, val page: Int = 0) : Route()

	@Serializable
	data class Search(val query: String = "", val sourceId: String? = null) : Route()

	@Serializable
	data class Source(val sourceId: String) : Route()

	@Serializable
	data class Category(val categoryId: Long) : Route()

	@Serializable
	data object History : Route()

	@Serializable
	data object Favourites : Route()

	@Serializable
	data object Downloads : Route()

	@Serializable
	data object Settings : Route()

	@Serializable
	data object About : Route()

	@Serializable
	data object Backup : Route()

	@Serializable
	data object Sync : Route()

	@Serializable
	data object Tracker : Route()

	@Serializable
	data object Browser : Route()

	@Serializable
	data object Suggestions : Route()

	@Serializable
	data class ExternalUrl(val url: String) : Route()

	val displayName: String
		get() = when (this) {
			is Home -> "Home"
			is MangaDetails -> "Manga Details"
			is Reader -> "Reader"
			is Search -> "Search"
			is Source -> "Source"
			is Category -> "Category"
			is History -> "History"
			is Favourites -> "Favourites"
			is Downloads -> "Downloads"
			is Settings -> "Settings"
			is About -> "About"
			is Backup -> "Backup"
			is Sync -> "Sync"
			is Tracker -> "Tracker"
			is Browser -> "Browser"
			is Suggestions -> "Suggestions"
			is ExternalUrl -> "External Link"
		}
}
