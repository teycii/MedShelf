package com.example.medshelf.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medshelf.data.AppDatabase
import com.example.medshelf.model.NoteEntity
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase
        .getDatabase(application)
        .noteDao()

    var notes = mutableStateOf<List<NoteEntity>>(emptyList())
        private set

    fun loadNotes() {
        viewModelScope.launch {
            notes.value = dao.getAllNotes()
        }
    }

    fun addNote(
        title: String,
        content: String,
        category: String,
        noteDate: String,
        noteTime: String
    ) {
        viewModelScope.launch {

            dao.insertNote(
                NoteEntity(
                    title = title,
                    content = content,
                    category = category,
                    noteDate = noteDate,
                    noteTime = noteTime
                )
            )

            loadNotes()
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            dao.updateNote(note)
            loadNotes()
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            dao.deleteNote(note)
            loadNotes()
        }
    }
}