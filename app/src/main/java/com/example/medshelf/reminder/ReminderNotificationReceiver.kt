package com.example.medshelf.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import com.example.medshelf.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "MedShelf:ReminderWakeLock"
        )

        wakeLock.acquire(10_000L)

        try {
            val action = intent.action

            if (
                action == Intent.ACTION_BOOT_COMPLETED ||
                action == Intent.ACTION_LOCKED_BOOT_COMPLETED
            ) {
                rescheduleAllReminders(context)
                return
            }

            val reminderId = intent.getIntExtra("reminderId", 0)
            val title = intent.getStringExtra("title") ?: "MedShelf Reminder"
            val message = intent.getStringExtra("message") ?: "You have a medical reminder."

            val serviceIntent = Intent(context, ReminderAlarmService::class.java).apply {
                putExtra("reminderId", reminderId)
                putExtra("title", title)
                putExtra("message", message)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }

        } catch (exception: Exception) {
            Log.e(
                "MedShelf_Receiver",
                "Failed to start reminder service: ${exception.message}"
            )
        } finally {
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
        }
    }

    private fun rescheduleAllReminders(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(context)

                val activeReminders = database.reminderDao()
                    .getAllRemindersSync()
                    .filter { reminder ->
                        reminder.status == ReminderUtils.STATUS_SCHEDULED &&
                                reminder.nextTriggerAtMillis > System.currentTimeMillis()
                    }

                activeReminders.forEach { reminder ->
                    ReminderAlarmScheduler.scheduleReminder(
                        context = context,
                        reminder = reminder
                    )
                }

                Log.d(
                    "MedShelf_Receiver",
                    "Rescheduled ${activeReminders.size} reminders after reboot."
                )

            } catch (exception: Exception) {
                Log.e(
                    "MedShelf_Receiver",
                    "Failed to reschedule reminders: ${exception.message}"
                )
            }
        }
    }
}