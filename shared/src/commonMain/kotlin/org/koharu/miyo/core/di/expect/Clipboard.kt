package org.koharu.miyo.core.di.expect

/**
 * Platform-agnostic clipboard interface.
 */
expect class ClipboardManager {
	suspend fun copyText(text: String, label: String = "Copied")
	suspend fun getText(): String?
	suspend fun hasText(): Boolean
}

expect fun createClipboardManager(): ClipboardManager
