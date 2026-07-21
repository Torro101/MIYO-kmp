package org.koharu.miyo.core.di.expect

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

actual class ShareManager(private val context: Context) {
	actual suspend fun shareText(text: String, title: String) {
		val intent = Intent(Intent.ACTION_SEND).apply {
			type = "text/plain"
			putExtra(Intent.EXTRA_TEXT, text)
		}
		context.startActivity(Intent.createChooser(intent, title).apply {
			addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		})
	}

	actual suspend fun shareUrl(url: String, title: String) {
		shareText(url, title)
	}

	actual suspend fun shareImage(imagePath: String, title: String) {
		val file = File(imagePath)
		val uri = FileProvider.getUriForFile(
			context,
			"${context.packageName}.fileprovider",
			file
		)
		val intent = Intent(Intent.ACTION_SEND).apply {
			type = "image/*"
			putExtra(Intent.EXTRA_STREAM, uri)
			addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
		}
		context.startActivity(Intent.createChooser(intent, title).apply {
			addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		})
	}
}

actual fun createShareManager(): ShareManager {
	return ShareManager(org.koharu.miyo.core.os.AndroidContextHolder.applicationContext)
}
