package com.example.medshelf.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val content: String,

    val category: String,

    val noteDate: String,

    val noteTime: String,

    val isPinned: Boolean = false
)