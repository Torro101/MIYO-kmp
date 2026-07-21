package org.koharu.miyo.core.di.expect

import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

actual class NotificationManager(private val context: Context) {
	private val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager

	init {
		createNotificationChannels()
	}

	private fun createNotificationChannels() {
		val channel = NotificationChannel(
			CHANNEL_ID,
			"Miyo Notifications",
			AndroidNotificationManager.IMPORTANCE_DEFAULT
		).apply {
			description = "General notifications from Miyo"
		}
		manager.createNotificationChannel(channel)
	}

	actual suspend fun showNotification(id: Int, title: String, message: String, channelId: String) {
		val notification = NotificationCompat.Builder(context, channelId)
			.setSmallIcon(android.R.drawable.ic_dialog_info)
			.setContentTitle(title)
			.setContentText(message)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setAutoCancel(true)
			.build()

		manager.notify(id, notification)
	}

	actual suspend fun cancelNotification(id: Int) {
		manager.cancel(id)
	}

	actual suspend fun cancelAllNotifications() {
		manager.cancelAll()
	}

	companion object {
		private const val CHANNEL_ID = "miyo_notifications"
	}
}

actual fun createNotificationManager(): NotificationManager {
	return NotificationManager(org.koharu.miyo.core.os.AndroidContextHolder.applicationContext)
}
