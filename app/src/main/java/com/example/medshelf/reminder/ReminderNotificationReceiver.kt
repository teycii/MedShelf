package com.example.medshelf.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class ReminderNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "MedShelf Reminder"
        val message = intent.getStringExtra("message") ?: "You have a medical reminder."

        val serviceIntent = Intent(context, ReminderAlarmService::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}