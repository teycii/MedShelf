package com.example.medshelf.data

import androidx.room.*
import com.example.medshelf.model.NoteEntity

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY isPinned DESC, id DESC")
    suspend fun getAllNotes(): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)
}