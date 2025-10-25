package com.otlante.news.data.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.otlante.news.R
import com.otlante.news.presentation.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationsHelper @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private val notificationManager = context.getSystemService<NotificationManager>()

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.new_articles),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun showNewArticlesNotifications(topics: List<String>) {
        val intent = Intent(context, MainActivity::class.java)
            .apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

        val pendingIntent = PendingIntent.getActivity(
            context,
            PENDING_INTENT_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_breaking_news)
            .setContentTitle(context.getString(R.string.new_articles_notification_header))
            .setAutoCancel(true)
            .setContentText(
                context.getString(
                    R.string.updated_subscriptions,
                    topics.size,
                    topics.joinToString(", ")
                ))
            .setContentIntent(pendingIntent)
            .build()
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "new_articles"
        const val NOTIFICATION_ID = 1
        const val PENDING_INTENT_REQUEST_CODE = 1
    }
}
