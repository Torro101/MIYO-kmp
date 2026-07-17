package org.koharu.miyo.core.di.expect

import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

actual class NotificationManager {
	private val center = UNUserNotificationCenter.currentNotificationCenter()

	actual suspend fun showNotification(id: Int, title: String, message: String, channelId: String) {
		val content = UNMutableNotificationContent().apply {
			this.title = title
			this.body = message
			this.sound = UNNotificationSound.defaultSound
		}

		val request = UNNotificationRequest.requestWithIdentifier(
			identifier = id.toString(),
			content = content,
			trigger = null
		)

		center.addNotificationRequest(request)
	}

	actual suspend fun cancelNotification(id: Int) {
		center.removePendingNotificationRequestsWithIdentifiers(listOf(id.toString()))
	}

	actual suspend fun cancelAllNotifications() {
		center.removeAllPendingNotificationRequests()
	}
}

actual fun createNotificationManager(): NotificationManager {
	return NotificationManager()
}
