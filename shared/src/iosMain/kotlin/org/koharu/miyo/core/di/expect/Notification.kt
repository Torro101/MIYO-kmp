package org.koharu.miyo.core.di.expect

actual class NotificationManager {
	actual suspend fun showNotification(
		id: Int,
		title: String,
		message: String,
		channelId: String,
	) = Unit

	actual suspend fun cancelNotification(id: Int) = Unit
	actual suspend fun cancelAllNotifications() = Unit
}

actual fun createNotificationManager(): NotificationManager = NotificationManager()
