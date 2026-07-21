package org.koharu.miyo.core.network

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Cross-platform in-memory cache with expiration.
 */
class CacheManager<K, V>(
	private val maxSize: Int = 1000,
	private val expirationTimeMillis: Long = 30 * 60 * 1000 // 30 minutes
) {
	private val cache = mutableMapOf<K, CacheEntry<V>>()
	private val mutex = Mutex()

	data class CacheEntry<V>(
		val value: V,
		val timestamp: Long
	) {
		fun isExpired(): Boolean {
			return kotlinx.datetime.Clock.System.now().toEpochMilliseconds() - timestamp > expirationTimeMillis
		}
	}

	suspend fun get(key: K): V? {
		return mutex.withLock {
			val entry = cache[key]
			if (entry != null && !entry.isExpired()) {
				entry.value
			} else {
				cache.remove(key)
				null
			}
		}
	}

	suspend fun put(key: K, value: V) {
		mutex.withLock {
			if (cache.size >= maxSize) {
				// Remove oldest entries
				val entriesToRemove = cache.entries
					.sortedBy { it.value.timestamp }
					.take(cache.size - maxSize + 1)
					.map { it.key }
				entriesToRemove.forEach { cache.remove(it) }
			}

			cache[key] = CacheEntry(
				value = value,
				timestamp = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
			)
		}
	}

	suspend fun remove(key: K) {
		mutex.withLock {
			cache.remove(key)
		}
	}

	suspend fun clear() {
		mutex.withLock {
			cache.clear()
		}
	}

	suspend fun size(): Int {
		return mutex.withLock {
			cache.size
		}
	}

	suspend fun cleanup() {
		mutex.withLock {
			val expiredKeys = cache.entries
				.filter { it.value.isExpired() }
				.map { it.key }
			expiredKeys.forEach { cache.remove(it) }
		}
	}
}
