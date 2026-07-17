package org.koharu.miyo.core.di.expect

/**
 * Platform-agnostic notification interface.
 */
expect class NotificationManager {
	suspend fun showNotification(
		id: Int,
		title: String,
		message: String,
	 channelId: String = "default"
	)
	suspend fun cancelNotification(id: Int)
	suspend fun cancelAllNotifications()
}

expect fun createNotificationManager(): NotificationManager
