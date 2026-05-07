package com.example.medshelf.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.medshelf.model.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(
        document: DocumentEntity
    )

    @Update
    suspend fun updateDocument(
        document: DocumentEntity
    )

    @Delete
    suspend fun deleteDocument(
        document: DocumentEntity
    )

    @Query("""
        SELECT * FROM documents
        ORDER BY createdAt DESC
    """)
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("""
        SELECT * FROM documents
        WHERE id = :documentId
        LIMIT 1
    """)
    suspend fun getDocumentById(
        documentId: Int
    ): DocumentEntity?

    @Query("""
        SELECT * FROM documents
        WHERE
            name LIKE '%' || :query || '%'
            OR type LIKE '%' || :query || '%'
            OR owner LIKE '%' || :query || '%'
            OR clinic LIKE '%' || :query || '%'
            OR notes LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchDocuments(
        query: String
    ): Flow<List<DocumentEntity>>
}