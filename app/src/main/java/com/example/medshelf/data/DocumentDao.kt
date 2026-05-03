package com.example.medshelf.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.medshelf.model.DocumentEntity // Add this import to fix the unresolved reference

@Dao  // This annotation makes it a Room DAO
interface DocumentDao {

    @Insert  // Insert method for saving documents
    suspend fun insert(document: DocumentEntity)

    @Query("SELECT * FROM documents")  // Get all documents from the database
    suspend fun getAllDocuments(): List<DocumentEntity>

    @Query("SELECT * FROM documents WHERE id = :documentId")  // Get document by its ID
    suspend fun getDocumentById(documentId: Int): DocumentEntity
}