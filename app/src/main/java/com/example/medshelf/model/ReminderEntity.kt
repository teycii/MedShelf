package com.example.medshelf.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val date: String,
    val time: String,
    val profile: String,
    val note: String,
    val repeat: String,
    val status: String,
    val createdAt: Long = System.currentTimeMillis()
)