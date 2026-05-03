package com.example.medshelf.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medshelf.data.AppDatabase
import com.example.medshelf.model.DocumentEntity
import kotlinx.coroutines.launch

class DocumentViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).documentDao()

    var documents = mutableStateOf<List<DocumentEntity>>(emptyList())
        private set

    fun loadDocuments() {
        viewModelScope.launch {
            documents.value = dao.getAllDocuments()
        }
    }

    fun addDocument(name: String, type: String, fileUri: String) {
        viewModelScope.launch {
            dao.insert(
                DocumentEntity(
                    name = name,
                    type = type,
                    fileUri = fileUri
                )
            )
            loadDocuments()
        }
    }
}