package org.koharu.miyo

/**
 * iOS entry called from Swift on app launch.
 *
 * ```swift
 * import shared
 * MiyoIosBootstrap.shared.start()
 * let items = MiyoShared.shared.library()
 * ```
 */
object MiyoIosBootstrap {
	fun start() {
		MiyoRuntime.start(useSampleData = true)
	}

	fun statusLine(): String = MiyoRuntime.platformSummary()

	fun libraryCount(): Int = MiyoRuntime.librarySnapshot().size

	fun favoriteCount(): Int = MiyoRuntime.favoritesSnapshot().size
}
