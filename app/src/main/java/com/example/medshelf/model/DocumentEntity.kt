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
    val date: String,
    val clinic: String,
    val notes: String,
    val fileUri: String,
    val createdAt: Long = System.currentTimeMillis()
)