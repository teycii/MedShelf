package com.example.medshelf.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val type: String,
    val owner: String,

    // If user leaves this blank in Add/Edit Document,
    // save the current date instead.
    val date: String,

    // New field for document time.
    // If user leaves this blank in Add/Edit Document,
    // save the current time instead.
    val time: String,

    val clinic: String,
    val notes: String,
    val fileUri: String,

    val createdAt: Long = System.currentTimeMillis()
)