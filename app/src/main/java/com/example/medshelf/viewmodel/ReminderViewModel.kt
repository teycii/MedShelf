package com.example.medshelf.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medshelf.data.AppDatabase
import com.example.medshelf.model.ReminderEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val reminderDao =
        AppDatabase.getDatabase(application).reminderDao()

    val reminders: StateFlow<List<ReminderEntity>> =
        reminderDao.getAllReminders()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addReminder(
        title: String,
        date: String,
        time: String,
        profile: String,
        note: String,
        repeat: String
    ) {
        viewModelScope.launch {
            reminderDao.insertReminder(
                ReminderEntity(
                    title = title,
                    date = date,
                    time = time,
                    profile = profile,
                    note = note,
                    repeat = repeat,
                    status = "Scheduled"
                )
            )
        }
    }

    fun updateReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            reminderDao.updateReminder(reminder)
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            reminderDao.deleteReminder(reminder)
        }
    }

    fun markComplete(reminder: ReminderEntity) {
        viewModelScope.launch {
            reminderDao.updateReminder(
                reminder.copy(status = "Completed")
            )
        }
    }
}