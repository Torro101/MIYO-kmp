package org.koharu.miyo.reader.ui

import kotlinx.serialization.Serializable

/** Cross-platform reader position. */
@Serializable
data class ReaderPosition(
	val chapterId: Long,
	val page: Int,
	val scroll: Int = 0,
)
