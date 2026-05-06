package com.example.medshelf.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medshelf.data.AppDatabase
import com.example.medshelf.model.DocumentEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DocumentViewModel(application: Application) : AndroidViewModel(application) {

    private val documentDao =
        AppDatabase.getDatabase(application).documentDao()

    val documents: StateFlow<List<DocumentEntity>> =
        documentDao.getAllDocuments()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val selectedDocument = mutableStateOf<DocumentEntity?>(null)

    fun addDocument(
        name: String,
        type: String,
        owner: String,
        date: String,
        clinic: String,
        notes: String,
        fileUri: String
    ) {
        viewModelScope.launch {
            val document = DocumentEntity(
                id = 0,
                name = name,
                type = type,
                owner = owner,
                date = date,
                clinic = clinic,
                notes = notes,
                fileUri = fileUri,
                createdAt = System.currentTimeMillis()
            )

            documentDao.insertDocument(document)
        }
    }

    fun loadDocumentById(documentId: Int) {
        viewModelScope.launch {
            selectedDocument.value = documentDao.getDocumentById(documentId)
        }
    }

    fun loadDocuments() {
        // Room StateFlow auto-updates the documents list.
    }

    @Suppress("unused")
    fun deleteDocument(document: DocumentEntity) {
        viewModelScope.launch {
            documentDao.deleteDocument(document)
        }
    }
}