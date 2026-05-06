package com.example.medshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val MedGreen = Color(0xFF009688)
private val DarkText = Color(0xFF111827)
private val SoftText = Color(0xFF64748B)
private val SoftBorder = Color(0xFFE2E8F0)

data class MedicalNote(
    val id: Int,
    val title: String,
    val content: String,
    val category: String,
    val noteDate: String,
    val noteTime: String,
    val isPinned: Boolean = false
)

@Composable
fun NotesScreen(
    navController: NavController
) {
    var notes by remember {
        mutableStateOf(
            listOf(
                MedicalNote(
                    id = 1,
                    title = "Allergy Reminder",
                    content = "Allergic to penicillin.",
                    category = "Allergy",
                    noteDate = getCurrentDate(),
                    noteTime = getCurrentTime(),
                    isPinned = true
                ),
                MedicalNote(
                    id = 2,
                    title = "Doctor Instruction",
                    content = "Take medicine after meals only.",
                    category = "Medication",
                    noteDate = getCurrentDate(),
                    noteTime = getCurrentTime()
                )
            )
        )
    }

    var searchText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedNote by remember { mutableStateOf<MedicalNote?>(null) }

    val filteredNotes = notes
        .filter {
            it.title.contains(searchText, ignoreCase = true) ||
                    it.content.contains(searchText, ignoreCase = true) ||
                    it.category.contains(searchText, ignoreCase = true) ||
                    it.noteDate.contains(searchText, ignoreCase = true) ||
                    it.noteTime.contains(searchText, ignoreCase = true)
        }
        .sortedWith(compareByDescending<MedicalNote> { it.isPinned }.thenByDescending { it.id })

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MedGreen,
                onClick = {
                    selectedNote = null
                    showDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Note",
                    tint = Color.White
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(18.dp)
        ) {
            NotesHeader(navController)

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search notes...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = SoftText
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MedGreen,
                    unfocusedBorderColor = SoftBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = MedGreen
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (filteredNotes.isEmpty()) {
                EmptyNotesView()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onEditClick = {
                                selectedNote = note
                                showDialog = true
                            },
                            onPinClick = {
                                notes = notes.map {
                                    if (it.id == note.id) {
                                        it.copy(isPinned = !it.isPinned)
                                    } else {
                                        it
                                    }
                                }
                            },
                            onDeleteClick = {
                                notes = notes.filter { it.id != note.id }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddEditNoteDialog(
            existingNote = selectedNote,
            onDismiss = {
                showDialog = false
            },
            onSave = { title, content, category, noteDate, noteTime ->
                notes = if (selectedNote == null) {
                    notes + MedicalNote(
                        id = (notes.maxOfOrNull { it.id } ?: 0) + 1,
                        title = title,
                        content = content,
                        category = category,
                        noteDate = noteDate,
                        noteTime = noteTime
                    )
                } else {
                    notes.map {
                        if (it.id == selectedNote?.id) {
                            it.copy(
                                title = title,
                                content = content,
                                category = category,
                                noteDate = noteDate,
                                noteTime = noteTime
                            )
                        } else {
                            it
                        }
                    }
                }

                showDialog = false
            }
        )
    }
}

@Composable
private fun NotesHeader(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { navController.popBackStack() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = DarkText
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        Column {
            Text(
                text = "Medical Notes",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = DarkText
            )

            Text(
                text = "Save reminders, instructions, and health details",
                style = MaterialTheme.typography.bodySmall,
                color = SoftText
            )
        }
    }
}

@Composable
private fun NoteCard(
    note: MedicalNote,
    onEditClick: () -> Unit,
    onPinClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = SoftBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (note.isPinned) {
                            Icon(
                                imageVector = Icons.Filled.PushPin,
                                contentDescription = null,
                                tint = MedGreen,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftText,
                        maxLines = 3
                    )
                }

                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Note",
                        tint = MedGreen
                    )
                }

                IconButton(onClick = onPinClick) {
                    Icon(
                        imageVector = Icons.Filled.PushPin,
                        contentDescription = "Pin Note",
                        tint = if (note.isPinned) MedGreen else SoftText
                    )
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Note",
                        tint = Color(0xFFEF4444)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryChip(note.category)

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${note.noteDate} • ${note.noteTime}",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftText
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(category: String) {
    Surface(
        shape = CircleShape,
        color = Color(0xFFE6F7F3)
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MedGreen,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptyNotesView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.NoteAlt,
                contentDescription = null,
                modifier = Modifier.size(70.dp),
                tint = MedGreen
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "No medical notes yet",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = DarkText
            )

            Text(
                text = "Tap + to add reminders, dates, and instructions.",
                color = SoftText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun AddEditNoteDialog(
    existingNote: MedicalNote?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var title by remember(existingNote) { mutableStateOf(existingNote?.title ?: "") }
    var content by remember(existingNote) { mutableStateOf(existingNote?.content ?: "") }
    var category by remember(existingNote) { mutableStateOf(existingNote?.category ?: "General") }
    var noteDate by remember(existingNote) { mutableStateOf(existingNote?.noteDate ?: getCurrentDate()) }
    var noteTime by remember(existingNote) { mutableStateOf(existingNote?.noteTime ?: getCurrentTime()) }

    val categories = listOf(
        "General",
        "Allergy",
        "Medication",
        "Doctor",
        "Emergency",
        "Lab Result"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existingNote == null) "Add Medical Note" else "Edit Medical Note",
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(13.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Note Details") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    shape = RoundedCornerShape(13.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = noteDate,
                        onValueChange = { noteDate = it },
                        label = { Text("Date") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(13.dp)
                    )

                    OutlinedTextField(
                        value = noteTime,
                        onValueChange = { noteTime = it },
                        label = { Text("Time") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(13.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Category",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { item ->
                        FilterChip(
                            selected = category == item,
                            onClick = { category = item },
                            label = { Text(item) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (
                        title.isNotBlank() &&
                        content.isNotBlank() &&
                        noteDate.isNotBlank() &&
                        noteTime.isNotBlank()
                    ) {
                        onSave(
                            title.trim(),
                            content.trim(),
                            category,
                            noteDate.trim(),
                            noteTime.trim()
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MedGreen
                )
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun getCurrentDate(): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(Date())
}

private fun getCurrentTime(): String {
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return formatter.format(Date())
}