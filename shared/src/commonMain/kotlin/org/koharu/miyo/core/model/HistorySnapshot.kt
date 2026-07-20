package org.koharu.miyo.core.model

import kotlinx.serialization.Serializable

/** Cross-platform reading history snapshot (epoch millis). */
@Serializable
data class HistorySnapshot(
	val createdAtEpochMs: Long,
	val updatedAtEpochMs: Long,
	val chapterId: Long,
	val page: Int,
	val scroll: Int,
	val percent: Float,
	val chaptersCount: Int,
)
