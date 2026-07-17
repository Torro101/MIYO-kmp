package org.koharu.miyo.core.util

/**
 * Cross-platform validation utilities.
 */
object ValidationUtils {
	fun isValidEmail(email: String): Boolean {
		val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
		return emailRegex.matches(email)
	}

	fun isValidUrl(url: String): Boolean {
		return try {
			// Simple URL validation
			url.startsWith("http://") || url.startsWith("https://")
		} catch (e: Exception) {
			false
		}
	}

	fun isValidUsername(username: String): Boolean {
		// 3-20 characters, alphanumeric and underscores
		val usernameRegex = "^[a-zA-Z0-9_]{3,20}$".toRegex()
		return usernameRegex.matches(username)
	}

	fun isValidPassword(password: String): Boolean {
		// At least 8 characters
		return password.length >= 8
	}

	fun isValidNumber(input: String): Boolean {
		return input.all { it.isDigit() }
	}

	fun isValidFloat(input: String): Boolean {
		return input.toFloatOrNull() != null
	}

	fun isValidInteger(input: String): Boolean {
		return input.toIntOrNull() != null
	}

	fun isNullOrEmpty(input: String?): Boolean {
		return input.isNullOrBlank()
	}

	fun isNotNullOrEmpty(input: String?): Boolean {
		return !input.isNullOrBlank()
	}
}
