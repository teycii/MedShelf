package com.example.medshelf.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medshelf.data.AppDatabase
import com.example.medshelf.model.DocumentEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DocumentViewModel(application: Application) : AndroidViewModel(application) {

    private val documentDao =
        AppDatabase.getDatabase(application).documentDao()

    private val searchQuery = MutableStateFlow("")

    val documents: StateFlow<List<DocumentEntity>> =
        combine(
            documentDao.getAllDocuments(),
            searchQuery
        ) { documentList, query ->

            if (query.isBlank()) {
                documentList
            } else {
                documentList.filter { document ->
                    document.name.contains(query, ignoreCase = true) ||
                            document.type.contains(query, ignoreCase = true) ||
                            document.owner.contains(query, ignoreCase = true) ||
                            document.clinic.contains(query, ignoreCase = true) ||
                            document.notes.contains(query, ignoreCase = true)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val selectedDocument = mutableStateOf<DocumentEntity?>(null)

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

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

    fun updateDocument(document: DocumentEntity) {
        viewModelScope.launch {
            documentDao.updateDocument(document)
        }
    }

    fun deleteDocument(document: DocumentEntity) {
        viewModelScope.launch {
            documentDao.deleteDocument(document)
        }
    }

    fun loadDocumentById(documentId: Int) {
        viewModelScope.launch {
            selectedDocument.value = documentDao.getDocumentById(documentId)
        }
    }

    fun clearSelectedDocument() {
        selectedDocument.value = null
    }
}