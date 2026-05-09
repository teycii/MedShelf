package com.example.medshelf.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.medshelf.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d("MedShelf_Receiver", "Received action: $action")

        if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            rescheduleAllReminders(context)
            return
        }

        val title = intent.getStringExtra("title") ?: "MedShelf Reminder"
        val message = intent.getStringExtra("message") ?: "You have a medical reminder."

        val serviceIntent = Intent(context, ReminderAlarmService::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } catch (e: Exception) {
            Log.e("MedShelf_Receiver", "Error starting service: ${e.message}")
        }
    }

    private fun rescheduleAllReminders(context: Context) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                val database = AppDatabase.getDatabase(context)
                val activeReminders = database.reminderDao().getAllRemindersSync()
                    .filter { it.status == "Scheduled" }

                activeReminders.forEach { reminder ->
                    ReminderAlarmScheduler.scheduleReminder(context, reminder)
                }
                Log.d("MedShelf_Receiver", "Rescheduled ${activeReminders.size} reminders after boot.")
            } catch (e: Exception) {
                Log.e("MedShelf_Receiver", "Failed to reschedule reminders: ${e.message}")
            }
        }
    }
}