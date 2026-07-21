package org.koharu.miyo.core.domain

/**
 * Cross-platform event types for UI communication.
 * Not kotlinx-serializable (may carry lambdas / Throwables).
 */
sealed class Event {
	data class ShowToast(val message: String) : Event()
	data class ShowError(val error: String, val throwable: Throwable? = null) : Event()
	data class ShowSnackbar(val message: String, val action: EventAction? = null) : Event()
	data class ShowDialog(
		val title: String,
		val message: String,
		val actions: List<EventAction> = emptyList(),
	) : Event()
	data class Navigate(val route: String) : Event()
	data class NavigateBack(val result: String? = null) : Event()
	data class NavigateToManga(val mangaId: Long) : Event()
	data class NavigateToChapter(val mangaId: Long, val chapterId: Long) : Event()
	data class NavigateToReader(val mangaId: Long, val chapterId: Long, val page: Int = 0) : Event()
	data class NavigateToSource(val sourceId: String) : Event()
	data class ShowLoading(val message: String = "Loading...") : Event()
	data object HideLoading : Event()
	data object RefreshData : Event()
	data class DownloadChapter(val mangaId: Long, val chapterId: Long) : Event()
	data class ShareContent(val title: String, val url: String) : Event()
	data class OpenUrl(val url: String) : Event()
	data class CopyToClipboard(val text: String, val label: String = "Copied") : Event()
}

data class EventAction(
	val id: String,
	val label: String,
)
