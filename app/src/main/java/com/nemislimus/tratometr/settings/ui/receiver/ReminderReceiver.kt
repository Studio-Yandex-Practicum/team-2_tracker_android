package com.nemislimus.tratometr.settings.ui.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.nemislimus.tratometr.R
import com.nemislimus.tratometr.common.util.AppNotificationManager

class ReminderReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { contextInstance ->
            showNotification(contextInstance)

            intent?.let { intentInstance ->
                if (intentInstance.getBooleanExtra(AppNotificationManager.CANT_USE_REPEAT_ALARM_KEY, false)) {
                    val hour = intentInstance.getIntExtra(AppNotificationManager.HOURS_KEY, -1)
                    val minute = intentInstance.getIntExtra(AppNotificationManager.MINUTES_KEY, -1)
                    AppNotificationManager.setNotification(contextInstance, hour, minute)
                }
            }
        }
    }

    private fun showNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаём канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_label)
            .setContentText(context.getString(R.string.notification_message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(AppNotificationManager.createNotificationPendingIntent(context))
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {

        const val NOTIFICATION_CHANNEL_ID = "daily_reminder"
        const val NOTIFICATION_CHANNEL_NAME = "reminder"
        const val NOTIFICATION_ID = 13
    }

}