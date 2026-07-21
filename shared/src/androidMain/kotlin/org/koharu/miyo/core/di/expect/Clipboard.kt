package org.koharu.miyo.core.di.expect

import android.content.ClipData
import android.content.Context
import android.content.ClipboardManager as AndroidClipboardManager
import org.koharu.miyo.core.os.AndroidContextHolder

actual class ClipboardManager(private val context: Context) {
	private val clipboard =
		context.getSystemService(Context.CLIPBOARD_SERVICE) as AndroidClipboardManager

	actual suspend fun copyText(text: String, label: String) {
		val clip = ClipData.newPlainText(label, text)
		clipboard.setPrimaryClip(clip)
	}

	actual suspend fun getText(): String? {
		val clip = clipboard.primaryClip ?: return null
		return clip.getItemAt(0).text?.toString()
	}

	actual suspend fun hasText(): Boolean = clipboard.hasPrimaryClip()
}

actual fun createClipboardManager(): ClipboardManager =
	ClipboardManager(AndroidContextHolder.applicationContext)
