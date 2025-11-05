package com.clara.provider.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Firebase Cloud Messaging service for push notifications
 * Mirrors iOS ProviderPushNotificationManager
 */
class ClaraMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle notification payload
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "Clara Provider"
            val body = notification.body ?: "New review request"
            sendNotification(title, body)
        }

        // Handle data payload
        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            // Update badge count, refresh UI, etc.
            handleDataMessage(data)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Send token to backend for registration
        // Similar to iOS registerDeviceToken
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel (required for Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Clara provider review requests"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Build notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_notification_clear_all)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500))
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun handleDataMessage(data: Map<String, String>) {
        // TODO: Parse data payload and update app state
        // Examples:
        // - Update badge count
        // - Refresh review requests list
        // - Show specific conversation
    }

    companion object {
        private const val CHANNEL_ID = "clara_review_requests"
        private const val CHANNEL_NAME = "Clara Review Requests"
        private const val NOTIFICATION_ID = 1
    }
}
