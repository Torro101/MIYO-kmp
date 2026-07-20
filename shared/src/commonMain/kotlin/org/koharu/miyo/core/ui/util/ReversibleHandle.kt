package org.koharu.miyo.core.ui.util

/** Undo handle for destructive actions (delete, disable sources, etc.). */
fun interface ReversibleHandle {
	suspend fun reverse()
}
