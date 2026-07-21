package org.koharu.miyo.core.di.expect

actual class ShareManager {
	actual suspend fun shareText(text: String, title: String) = Unit
	actual suspend fun shareUrl(url: String, title: String) = Unit
	actual suspend fun shareImage(imagePath: String, title: String) = Unit
}

actual fun createShareManager(): ShareManager = ShareManager()
