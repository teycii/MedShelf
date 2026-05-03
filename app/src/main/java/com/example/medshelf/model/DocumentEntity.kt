package com.example.medshelf.model  // Ensure this is the correct package

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")  // This tells Room to create a table called "documents"
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // ID will auto-generate
    val name: String,
    val type: String,
    val fileUri: String  // URI or file path to store the document
)