package org.koharu.miyo.core.di.expect

import platform.UIKit.UIPasteboard

actual class ClipboardManager {
	actual suspend fun copyText(text: String, label: String) {
		UIPasteboard.generalUIPasteboard.string = text
	}

	actual suspend fun getText(): String? {
		return UIPasteboard.generalUIPasteboard.string
	}

	actual suspend fun hasText(): Boolean {
		return UIPasteboard.generalUIPasteboard.hasStrings
	}
}

actual fun createClipboardManager(): ClipboardManager {
	return ClipboardManager()
}
