package org.koharu.miyo.core.network

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock

/**
 * Cross-platform rate limiter for API requests.
 */
class RateLimiter(
	private val maxRequests: Int = 10,
	private val timeWindowMillis: Long = 60_000
) {
	private val mutex = Mutex()
	private val semaphore = Semaphore(maxRequests)
	private val requestTimestamps = mutableListOf<Long>()

	suspend fun <T> execute(block: suspend () -> T): T {
		semaphore.acquire()
		try {
			val now = getCurrentTimeMillis()
			mutex.withLock {
				// Remove old timestamps outside the time window
				requestTimestamps.removeAll { now - it > timeWindowMillis }

				// If we've hit the rate limit, wait
				if (requestTimestamps.size >= maxRequests) {
					val oldestTimestamp = requestTimestamps.first()
					val waitTime = timeWindowMillis - (now - oldestTimestamp)
					if (waitTime > 0) {
						delay(waitTime)
					}
				}

				requestTimestamps.add(getCurrentTimeMillis())
			}

			return block()
		} finally {
			semaphore.release()
		}
	}

	private fun getCurrentTimeMillis(): Long {
		return org.koharu.miyo.core.di.expect.currentDateTime().toEpochMilliseconds()
	}
}
