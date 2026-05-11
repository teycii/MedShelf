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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DocumentViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val documentDao =
        AppDatabase.getDatabase(application)
            .documentDao()

    val documents: StateFlow<List<DocumentEntity>> =
        documentDao.getAllDocuments()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val selectedDocument =
        mutableStateOf<DocumentEntity?>(null)

    fun addDocument(
        name: String,
        type: String,
        owner: String,
        date: String,
        time: String,
        clinic: String,
        notes: String,
        fileUri: String
    ) {
        viewModelScope.launch {

            val currentMillis =
                System.currentTimeMillis()

            val finalDate =
                if (date.isBlank()) {
                    formatDate(currentMillis)
                } else {
                    date
                }

            val finalTime =
                if (time.isBlank()) {
                    formatTime(currentMillis)
                } else {
                    time
                }

            val document = DocumentEntity(
                id = 0,
                name = name.trim(),
                type = type.trim(),
                owner = owner.trim(),
                date = finalDate,
                time = finalTime,
                clinic = clinic.ifBlank {
                    "Not specified"
                },
                notes = notes.ifBlank {
                    "No notes"
                },
                fileUri = fileUri,
                createdAt = currentMillis
            )

            documentDao.insertDocument(document)
        }
    }

    fun updateDocument(
        document: DocumentEntity
    ) {
        viewModelScope.launch {
            documentDao.updateDocument(document)
        }
    }

    fun loadDocumentById(
        documentId: Int
    ) {
        viewModelScope.launch {
            selectedDocument.value =
                documentDao.getDocumentById(documentId)
        }
    }

    fun deleteDocument(
        document: DocumentEntity
    ) {
        viewModelScope.launch {
            documentDao.deleteDocument(document)
        }
    }

    private fun formatDate(
        millis: Long
    ): String {
        return SimpleDateFormat(
            "MMM dd, yyyy",
            Locale.getDefault()
        ).format(Date(millis))
    }

    private fun formatTime(
        millis: Long
    ): String {
        return SimpleDateFormat(
            "hh:mm a",
            Locale.getDefault()
        ).format(Date(millis))
    }
}