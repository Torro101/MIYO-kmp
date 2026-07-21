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
		MiyoShared.startSampleRuntime()
	}

	fun statusLine(): String = MiyoShared.platformSummary()

	fun libraryCount(): Int = MiyoShared.library().size

	fun favoriteCount(): Int = MiyoShared.favorites().size
}
