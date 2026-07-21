package org.koharu.miyo.core.util

/**
 * Cross-platform collection utilities.
 */
object CollectionUtils {
	fun <T> List<T>.chunkedBy(predicate: (T) -> Boolean): List<List<T>> {
		val result = mutableListOf<List<T>>()
		var currentChunk = mutableListOf<T>()

		for (item in this) {
			if (predicate(item)) {
				if (currentChunk.isNotEmpty()) {
					result.add(currentChunk)
					currentChunk = mutableListOf()
				}
				result.add(listOf(item))
			} else {
				currentChunk.add(item)
			}
		}

		if (currentChunk.isNotEmpty()) {
			result.add(currentChunk)
		}

		return result
	}

	fun <T> List<T>.takeUntil(predicate: (T) -> Boolean): List<T> {
		val result = mutableListOf<T>()
		for (item in this) {
			if (predicate(item)) break
			result.add(item)
		}
		return result
	}

	fun <T> List<T>.distinctByPropertyDescriptor(property: (T) -> Any): List<T> {
		val seen = mutableSetOf<Any>()
		return filter { item ->
			val key = property(item)
			seen.add(key)
		}
	}

	fun <T> List<T>.rotateLeft(distance: Int): List<T> {
		if (isEmpty()) return this
		val normalizedDistance = distance % size
		return drop(normalizedDistance) + take(normalizedDistance)
	}

	fun <T> List<T>.rotateRight(distance: Int): List<T> {
		if (isEmpty()) return this
		val normalizedDistance = distance % size
		return takeLast(normalizedDistance) + dropLast(normalizedDistance)
	}

	fun <T> List<T>.splitIntoChunks(chunkSize: Int): List<List<T>> {
		return chunked(chunkSize)
	}

	fun <T> List<T>.secondOrNull(): T? {
		return if (size >= 2) this[1] else null
	}

	fun <T> List<T>.thirdOrNull(): T? {
		return if (size >= 3) this[2] else null
	}
}
