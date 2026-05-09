package com.example.medshelf.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.medshelf.model.ReminderEntity

object ReminderAlarmScheduler {

    fun scheduleReminder(
        context: Context,
        reminder: ReminderEntity
    ) {
        if (reminder.nextTriggerAtMillis <= System.currentTimeMillis()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderNotificationReceiver::class.java).apply {
            putExtra("title", reminder.title)
            putExtra("message", buildReminderMessage(reminder))
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminder.nextTriggerAtMillis,
            pendingIntent
        )
    }

    fun cancelReminder(
        context: Context,
        reminder: ReminderEntity
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderNotificationReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun buildReminderMessage(reminder: ReminderEntity): String {
        val scheduleText = if (reminder.scheduleType == "INTERVAL") {
            "Every ${reminder.intervalHours} hour(s)"
        } else {
            "${reminder.date}, ${reminder.time}"
        }

        return "$scheduleText • ${reminder.profile}" +
                if (reminder.note.isNotBlank()) " • ${reminder.note}" else ""
    }
}