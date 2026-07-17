package org.koharu.miyo.core.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koharu.miyo.core.model.Chapter
import org.koharu.miyo.core.model.Manga

/**
 * Cross-platform ViewModel for manga details.
 */
open class DetailsViewModel : BaseViewModel() {
	private val _manga = MutableStateFlow<Manga?>(null)
	val manga: StateFlow<Manga?> = _manga.asStateFlow()

	private val _chapters = MutableStateFlow<List<Chapter>>(emptyList())
	val chapters: StateFlow<List<Chapter>> = _chapters.asStateFlow()

	private val _isFavorite = MutableStateFlow(false)
	val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

	protected open fun updateManga(manga: Manga?) {
		_manga.value = manga
	}

	protected open fun updateChapters(chapters: List<Chapter>) {
		_chapters.value = chapters
	}

	protected open fun updateFavoriteStatus(isFavorite: Boolean) {
		_isFavorite.value = isFavorite
	}

	protected open fun updateReadingProgress(chapterId: Long, page: Int) {
		_chapters.value = _chapters.value.map { chapter ->
			if (chapter.id == chapterId) {
				chapter.copy(lastPageRead = page)
			} else {
				chapter
			}
		}
	}
}
