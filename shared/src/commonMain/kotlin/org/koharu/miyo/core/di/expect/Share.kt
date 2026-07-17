package org.koharu.miyo.core.di.expect

/**
 * Platform-agnostic sharing interface.
 */
expect class ShareManager {
	suspend fun shareText(text: String, title: String = "Share")
	suspend fun shareUrl(url: String, title: String = "Share")
	suspend fun shareImage(imagePath: String, title: String = "Share Image")
}

expect fun createShareManager(): ShareManager
