package com.example.medshelf.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medshelf.data.AppDatabase
import com.example.medshelf.model.ReminderEntity
import com.example.medshelf.reminder.ReminderAlarmScheduler
import com.example.medshelf.reminder.ReminderUtils
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
            val nextTrigger = ReminderUtils.calculateNextTriggerMillis(reminder)

            val reminderToSave = reminder.copy(
                status = ReminderUtils.STATUS_SCHEDULED,
                nextTriggerAtMillis = nextTrigger,
                lastCompletedAtMillis = 0L
            )

            val newId = reminderDao.insertReminder(reminderToSave).toInt()
            val savedReminder = reminderToSave.copy(id = newId)

            if (savedReminder.nextTriggerAtMillis > System.currentTimeMillis()) {
                ReminderAlarmScheduler.scheduleReminder(
                    context = appContext,
                    reminder = savedReminder
                )
            }
        }
    }

    fun updateReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            ReminderAlarmScheduler.cancelReminder(
                context = appContext,
                reminder = reminder
            )

            val nextTrigger = ReminderUtils.calculateNextTriggerMillis(reminder)

            val reminderToUpdate = reminder.copy(
                status = ReminderUtils.STATUS_SCHEDULED,
                nextTriggerAtMillis = nextTrigger,
                lastCompletedAtMillis = 0L
            )

            reminderDao.updateReminder(reminderToUpdate)

            if (reminderToUpdate.nextTriggerAtMillis > System.currentTimeMillis()) {
                ReminderAlarmScheduler.scheduleReminder(
                    context = appContext,
                    reminder = reminderToUpdate
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
            ReminderAlarmScheduler.cancelReminder(
                context = appContext,
                reminder = reminder
            )

            val now = System.currentTimeMillis()

            val isRepeating =
                reminder.scheduleType == ReminderUtils.SCHEDULE_INTERVAL ||
                        reminder.repeat != "Once"

            if (isRepeating) {
                val nextTrigger = ReminderUtils.calculateNextTriggerMillis(reminder)

                val updatedReminder = reminder.copy(
                    status = ReminderUtils.STATUS_SCHEDULED,
                    lastCompletedAtMillis = now,
                    nextTriggerAtMillis = nextTrigger
                )

                reminderDao.updateReminder(updatedReminder)

                if (nextTrigger > now) {
                    ReminderAlarmScheduler.scheduleReminder(
                        context = appContext,
                        reminder = updatedReminder
                    )
                }
            } else {
                val completedReminder = reminder.copy(
                    status = ReminderUtils.STATUS_COMPLETED,
                    lastCompletedAtMillis = now,
                    nextTriggerAtMillis = 0L
                )

                reminderDao.updateReminder(completedReminder)
            }
        }
    }

    fun markActive(reminder: ReminderEntity) {
        viewModelScope.launch {
            ReminderAlarmScheduler.cancelReminder(
                context = appContext,
                reminder = reminder
            )

            val nextTrigger = ReminderUtils.calculateNextTriggerMillis(reminder)

            val activeReminder = reminder.copy(
                status = ReminderUtils.STATUS_SCHEDULED,
                lastCompletedAtMillis = 0L,
                nextTriggerAtMillis = nextTrigger
            )

            reminderDao.updateReminder(activeReminder)

            if (nextTrigger > System.currentTimeMillis()) {
                ReminderAlarmScheduler.scheduleReminder(
                    context = appContext,
                    reminder = activeReminder
                )
            }
        }
    }
}