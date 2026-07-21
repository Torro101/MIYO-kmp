package org.koharu.miyo.core.di.expect

/** Minimal iOS stubs that compile for framework link. */
actual class ClipboardManager {
	actual suspend fun copyText(text: String, label: String) = Unit
	actual suspend fun getText(): String? = null
	actual suspend fun hasText(): Boolean = false
}

actual fun createClipboardManager(): ClipboardManager = ClipboardManager()
