package org.koharu.miyo

/**
 * iOS entry called from Swift on app launch.
 *
 * ```swift
 * import shared
 * MiyoIosBootstrap.shared.start()
 * let items = MiyoShared.shared.library()
 * print(MiyoIosBootstrap.shared.statusLine())
 * ```
 */
object MiyoIosBootstrap {
	private var started = false

	fun start() {
		if (started) return
		MiyoShared.startSampleRuntime()
		started = true
	}

	/** Idempotent: starts sample runtime if needed, returns platform summary. */
	fun ensureStarted(): String {
		start()
		return statusLine()
	}

	fun statusLine(): String = MiyoShared.platformSummary()

	fun hello(): String = MiyoShared.hello()

	fun libraryCount(): Int {
		start()
		return MiyoShared.library().size
	}

	fun favoriteCount(): Int {
		start()
		return MiyoShared.favorites().size
	}

	fun historyCount(): Int {
		start()
		return MiyoShared.history().size
	}

	fun isRuntimeReady(): Boolean = MiyoRuntime.isStarted()
}
