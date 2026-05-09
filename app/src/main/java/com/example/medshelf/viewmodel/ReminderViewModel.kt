package com.example.medshelf.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medshelf.data.AppDatabase
import com.example.medshelf.model.ReminderEntity
import com.example.medshelf.reminder.ReminderAlarmScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val appContext = getApplication<Application>()

    private val reminderDao =
        AppDatabase.getDatabase(appContext).reminderDao()

    val reminders: StateFlow<List<ReminderEntity>> =
        reminderDao.getAllReminders()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            val newId = reminderDao.insertReminder(reminder).toInt()
            val savedReminder = reminder.copy(id = newId)

            ReminderAlarmScheduler.scheduleReminder(
                context = appContext,
                reminder = savedReminder
            )
        }
    }

    fun updateReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            reminderDao.updateReminder(reminder)

            if (reminder.status == "Completed") {
                ReminderAlarmScheduler.cancelReminder(
                    context = appContext,
                    reminder = reminder
                )
            } else {
                ReminderAlarmScheduler.cancelReminder(
                    context = appContext,
                    reminder = reminder
                )

                ReminderAlarmScheduler.scheduleReminder(
                    context = appContext,
                    reminder = reminder
                )
            }
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            ReminderAlarmScheduler.cancelReminder(
                context = appContext,
                reminder = reminder
            )

            reminderDao.deleteReminder(reminder)
        }
    }

    fun markComplete(reminder: ReminderEntity) {
        viewModelScope.launch {
            val completedReminder = reminder.copy(status = "Completed")

            ReminderAlarmScheduler.cancelReminder(
                context = appContext,
                reminder = reminder
            )

            reminderDao.updateReminder(completedReminder)
        }
    }

    fun markActive(reminder: ReminderEntity) {
        viewModelScope.launch {
            val activeReminder = reminder.copy(status = "Scheduled")

            reminderDao.updateReminder(activeReminder)

            ReminderAlarmScheduler.scheduleReminder(
                context = appContext,
                reminder = activeReminder
            )
        }
    }
}