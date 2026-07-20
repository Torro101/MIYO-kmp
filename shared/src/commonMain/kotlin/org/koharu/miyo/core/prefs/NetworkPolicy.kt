package org.koharu.miyo.core.prefs

/**
 * Download / network access policy (persisted by [key]).
 * Android connectivity checks live in androidMain extensions.
 */
enum class NetworkPolicy(val key: Int) {
	NEVER(0),
	ALWAYS(1),
	NON_METERED(2),
	;

	companion object {
		fun from(key: String?, default: NetworkPolicy): NetworkPolicy {
			val intKey = key?.toIntOrNull() ?: return default
			return entries.find { it.key == intKey } ?: default
		}
	}
}
