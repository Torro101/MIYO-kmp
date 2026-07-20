package org.koharu.miyo

import org.koharu.miyo.MiyoRuntime

/**
 * Optional Android debug bootstrap for the shared in-memory stack.
 * Production Android continues to use Room/Hilt repositories.
 */
object MiyoAndroidDebugBootstrap {
	fun startSampleStack() {
		MiyoRuntime.start(useSampleData = true)
	}
}
