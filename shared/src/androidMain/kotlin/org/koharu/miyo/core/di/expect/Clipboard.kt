package org.koharu.miyo.core.di.expect

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

actual class ClipboardManager(private val context: Context) {
	private val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

	actual suspend fun copyText(text: String, label: String) {
		val clip = ClipData.newPlainText(label, text)
		clipboard.setPrimaryClip(clip)
	}

	actual suspend fun getText(): String? {
		val clip = clipboard.primaryClip ?: return null
		return clip.getItemAt(0).text?.toString()
	}

	actual suspend fun hasText(): Boolean {
		return clipboard.hasPrimaryClip()
	}
}

actual fun createClipboardManager(): ClipboardManager {
	return ClipboardManager(org.koharu.miyo.core.os.AndroidContextHolder.applicationContext)
}
