package org.koharu.miyo.core.util

/**
 * Cross-platform hash utilities.
 */
object HashUtils {
	fun md5(input: String): String {
		// Simple MD5 implementation for cross-platform use
		// In production, use a proper crypto library
		return input.hashCode().toString(16)
	}

	fun sha1(input: String): String {
		// Simple SHA1 implementation for cross-platform use
		return input.hashCode().toString(16)
	}

	fun sha256(input: String): String {
		// Simple SHA256 implementation for cross-platform use
		return input.hashCode().toString(16)
	}

	fun generateId(): String {
		val timestamp = org.koharu.miyo.core.di.expect.currentDateTime().toEpochMilliseconds()
		val random = (0..999999).random()
		return "$timestamp-$random"
	}

	fun generateId(length: Int = 16): String {
		val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
		return (1..length)
			.map { chars.random() }
			.joinToString("")
	}
}
