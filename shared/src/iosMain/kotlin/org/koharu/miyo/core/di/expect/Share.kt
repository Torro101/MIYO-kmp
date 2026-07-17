package org.koharu.miyo.core.di.expect

import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import kotlinx.cinterop.ExperimentalForeignApi

actual class ShareManager {
	@OptIn(ExperimentalForeignApi::class)
	actual suspend fun shareText(text: String, title: String) {
		val activityVC = UIActivityViewController(
			activityItems = listOf(text),
			applicationActivities = null
		)
		UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
			activityVC,
			animated = true,
			completion = null
		)
	}

	@OptIn(ExperimentalForeignApi::class)
	actual suspend fun shareUrl(url: String, title: String) {
		val nsUrl = NSURL(string = url)
		val activityVC = UIActivityViewController(
			activityItems = listOf(nsUrl),
			applicationActivities = null
		)
		UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
			activityVC,
			animated = true,
			completion = null
		)
	}

	@OptIn(ExperimentalForeignApi::class)
	actual suspend fun shareImage(imagePath: String, title: String) {
		val nsUrl = NSURL(fileURLWithPath = imagePath)
		val activityVC = UIActivityViewController(
			activityItems = listOf(nsUrl),
			applicationActivities = null
		)
		UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
			activityVC,
			animated = true,
			completion = null
		)
	}
}

actual fun createShareManager(): ShareManager {
	return ShareManager()
}
