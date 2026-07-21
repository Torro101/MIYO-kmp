package org.koharu.miyo.list.ui

/**
 * Shared DiffUtil-style payload tokens.
 * Android [ListModelDiffCallback] companion should reference these same instances.
 */
object ListModelDiffPayloads {
	val PAYLOAD_CHECKED_CHANGED: Any = Any()
	val PAYLOAD_NESTED_LIST_CHANGED: Any = Any()
	val PAYLOAD_PROGRESS_CHANGED: Any = Any()
	val PAYLOAD_ANYTHING_CHANGED: Any = Any()
}
