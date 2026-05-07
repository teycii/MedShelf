package com.example.medshelf.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val MedGreen = Color(0xFF009688)
private val DarkText = Color(0xFF111827)
private val SoftText = Color(0xFF64748B)
private val SoftBorder = Color(0xFFE2E8F0)
private val ScreenBg = Color(0xFFF8FAFC)

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
fun NotesScreen(navController: NavController) {
    var notes by remember { mutableStateOf<List<MedicalNote>>(emptyList()) }
    var searchText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedNote by remember { mutableStateOf<MedicalNote?>(null) }

    val filteredNotes = notes
        .filter { note ->
            note.title.contains(searchText, true) ||
                    note.content.contains(searchText, true) ||
                    note.category.contains(searchText, true) ||
                    note.noteDate.contains(searchText, true) ||
                    note.noteTime.contains(searchText, true)
        }
        .sortedWith(compareByDescending<MedicalNote> { it.isPinned }.thenByDescending { it.id })

    Scaffold(
        bottomBar = { MedShelfBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MedGreen,
                contentColor = Color.White,
                onClick = {
                    selectedNote = null
                    showDialog = true
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Note")
            }
        },
        containerColor = ScreenBg
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .padding(padding)
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))
            NotesHeader(navController)

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search notes...") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = SoftText)
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
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 110.dp),
                    modifier = Modifier.fillMaxSize()
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
                                    if (it.id == note.id) it.copy(isPinned = !it.isPinned) else it
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
            onDismiss = { showDialog = false },
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
                        } else it
                    }
                }

                showDialog = false
            }
        )
    }
}

@Composable
private fun NotesHeader(navController: NavController) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkText)
        }

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
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, SoftBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (note.isPinned) {
                            Icon(Icons.Filled.PushPin, contentDescription = null, tint = MedGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftText,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit Note", tint = MedGreen)
                    }
                    IconButton(onClick = onPinClick) {
                        Icon(Icons.Filled.PushPin, contentDescription = "Pin Note", tint = if (note.isPinned) MedGreen else SoftText)
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete Note", tint = Color(0xFFEF4444))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
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
    Surface(shape = CircleShape, color = Color(0xFFE6F7F3)) {
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
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.NoteAlt, contentDescription = null, modifier = Modifier.size(70.dp), tint = MedGreen)
            Spacer(modifier = Modifier.height(12.dp))
            Text("No medical notes yet", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = DarkText)
            Text("Tap + to add reminders, dates, and instructions.", color = SoftText, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditNoteDialog(
    existingNote: MedicalNote?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var title by remember(existingNote) { mutableStateOf(existingNote?.title ?: "") }
    var content by remember(existingNote) { mutableStateOf(existingNote?.content ?: "") }
    var category by remember(existingNote) { mutableStateOf(existingNote?.category ?: "General") }
    var noteDate by remember(existingNote) { mutableStateOf(existingNote?.noteDate ?: "") }
    var noteTime by remember(existingNote) { mutableStateOf(existingNote?.noteTime ?: "") }
    var error by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(is24Hour = false)
    val categories = listOf("General", "Allergy", "Medication", "Doctor", "Emergency", "Lab Result")

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
                    onValueChange = {
                        title = it
                        error = ""
                    },
                    label = { Text("Note Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(13.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = {
                        content = it
                        error = ""
                    },
                    label = { Text("Note Details") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5,
                    shape = RoundedCornerShape(13.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = noteDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Date") },
                        trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showDatePicker = true },
                        shape = RoundedCornerShape(13.dp)
                    )

                    OutlinedTextField(
                        value = noteTime,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Time") },
                        trailingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showTimePicker = true },
                        shape = RoundedCornerShape(13.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { showDatePicker = true }) { Text("Choose Date") }
                    TextButton(onClick = { showTimePicker = true }) { Text("Choose Time") }
                }

                Text("Category", fontWeight = FontWeight.SemiBold, color = DarkText)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach {
                        FilterChip(
                            selected = category == it,
                            onClick = { category = it },
                            label = { Text(it) }
                        )
                    }
                }

                if (error.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank() || content.isBlank() || noteDate.isBlank() || noteTime.isBlank()) {
                        error = "Please complete all note fields."
                    } else {
                        onSave(title.trim(), content.trim(), category, noteDate, noteTime)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MedGreen)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            noteDate = formatDate(it)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time", fontWeight = FontWeight.Bold) },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        noteTime = formatTime(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            }
        )
    }
}

private fun getCurrentDate(): String {
    return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
}

private fun getCurrentTime(): String {
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
}

private fun formatDate(millis: Long): String {
    return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(millis))
}

private fun formatTime(hour: Int, minute: Int): String {
    val amPm = if (hour >= 12) "PM" else "AM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "$displayHour:${minute.toString().padStart(2, '0')} $amPm"
}