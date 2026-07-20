package org.koharu.miyo.core.model

import org.koharu.miyo.reader.ui.ReaderPosition
import org.koharu.miyo.reader.ui.ReaderState
import java.time.Instant

fun MangaHistory.toSnapshot(): HistorySnapshot = HistorySnapshot(
	createdAtEpochMs = createdAt.toEpochMilli(),
	updatedAtEpochMs = updatedAt.toEpochMilli(),
	chapterId = chapterId,
	page = page,
	scroll = scroll,
	percent = percent,
	chaptersCount = chaptersCount,
)

fun HistorySnapshot.toMangaHistory(): MangaHistory = MangaHistory(
	createdAt = Instant.ofEpochMilli(createdAtEpochMs),
	updatedAt = Instant.ofEpochMilli(updatedAtEpochMs),
	chapterId = chapterId,
	page = page,
	scroll = scroll,
	percent = percent,
	chaptersCount = chaptersCount,
)

fun ReaderState.toPosition(): ReaderPosition =
	ReaderPosition(chapterId = chapterId, page = page, scroll = scroll)

fun ReaderPosition.toReaderState(): ReaderState =
	ReaderState(chapterId = chapterId, page = page, scroll = scroll)

fun MangaHistory.toReaderPosition(): ReaderPosition =
	ReaderPosition(chapterId = chapterId, page = page, scroll = scroll)
