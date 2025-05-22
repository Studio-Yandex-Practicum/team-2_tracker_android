package com.nemislimus.tratometr.common.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.nemislimus.tratometr.common.MainActivity
import com.nemislimus.tratometr.settings.ui.receiver.ReminderReceiver
import java.util.Calendar

object AppNotificationManager {

    const val CANT_USE_REPEAT_ALARM_KEY = "CANT_USE_REPEAT_ALARM_KEY"
    const val HOURS_KEY = "HOURS_KEY"
    const val MINUTES_KEY = "MINUTES_KEY"

    private const val REMINDER_INTENT_CODE = 13
    private const val NOTIFICATION_INTENT_CODE = 31
    private val cantUseRepeatAlarm = Build.VERSION.SDK_INT > Build.VERSION_CODES.R

    @SuppressLint("ScheduleExactAlarm")
    fun setNotification(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent: PendingIntent

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        if (cantUseRepeatAlarm) {
            pendingIntent = createReminderPendingIntent(context, hour, minute)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            pendingIntent = createReminderPendingIntent(context)
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }

    fun cancelNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = createReminderPendingIntent(context)
        alarmManager.cancel(pendingIntent)
    }

    private fun createReminderPendingIntent(context: Context, hour: Int = -1, minute: Int = -1): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(CANT_USE_REPEAT_ALARM_KEY, cantUseRepeatAlarm)
            putExtra(HOURS_KEY, hour)
            putExtra(MINUTES_KEY, minute)
        }
        return PendingIntent.getBroadcast(
            context,
            REMINDER_INTENT_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun createNotificationPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return PendingIntent.getActivity(
            context,
            NOTIFICATION_INTENT_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}