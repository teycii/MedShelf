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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medshelf.model.NoteEntity
import com.example.medshelf.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val MedGreen = Color(0xFF009688)
private val DarkText = Color(0xFF111827)
private val SoftText = Color(0xFF64748B)
private val SoftBorder = Color(0xFFE2E8F0)
private val ScreenBg = Color(0xFFF8FAFC)

private sealed class NoteDialogState {
    data object Closed : NoteDialogState()
    data object Add : NoteDialogState()
    data class Edit(val note: NoteEntity) : NoteDialogState()
}

@Composable
fun NotesScreen(
    navController: NavController,
    noteViewModel: NoteViewModel
) {
    val notes = noteViewModel.notes.value

    var searchText by remember { mutableStateOf("") }
    var dialogState by remember { mutableStateOf<NoteDialogState>(NoteDialogState.Closed) }

    LaunchedEffect(Unit) {
        noteViewModel.loadNotes()
    }

    val filteredNotes = notes
        .filter { note ->
            note.title.contains(searchText, true) ||
                    note.content.contains(searchText, true) ||
                    note.category.contains(searchText, true) ||
                    note.noteDate.contains(searchText, true) ||
                    note.noteTime.contains(searchText, true)
        }
        .sortedWith(compareByDescending<NoteEntity> { it.isPinned }.thenByDescending { it.id })

    Scaffold(
        bottomBar = { MedShelfBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MedGreen,
                contentColor = Color.White,
                onClick = { dialogState = NoteDialogState.Add }
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

            SearchField(
                value = searchText,
                onValueChange = { searchText = it }
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
                                dialogState = NoteDialogState.Edit(note)
                            },
                            onPinClick = {
                                noteViewModel.updateNote(
                                    note.copy(isPinned = !note.isPinned)
                                )
                            },
                            onDeleteClick = {
                                noteViewModel.deleteNote(note)
                            }
                        )
                    }
                }
            }
        }
    }

    when (val state = dialogState) {
        NoteDialogState.Closed -> Unit

        NoteDialogState.Add -> {
            AddEditNoteDialog(
                existingNote = null,
                onDismiss = { dialogState = NoteDialogState.Closed },
                onSave = { title, content, category, noteDate, noteTime ->
                    noteViewModel.addNote(
                        title = title,
                        content = content,
                        category = category,
                        noteDate = noteDate,
                        noteTime = noteTime
                    )

                    dialogState = NoteDialogState.Closed
                }
            )
        }

        is NoteDialogState.Edit -> {
            AddEditNoteDialog(
                existingNote = state.note,
                onDismiss = { dialogState = NoteDialogState.Closed },
                onSave = { title, content, category, noteDate, noteTime ->
                    noteViewModel.updateNote(
                        state.note.copy(
                            title = title,
                            content = content,
                            category = category,
                            noteDate = noteDate,
                            noteTime = noteTime
                        )
                    )

                    dialogState = NoteDialogState.Closed
                }
            )
        }
    }
}

@Composable
private fun NotesHeader(navController: NavController) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = DarkText
            )
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
                color = SoftText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
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
}

@Composable
private fun NoteCard(
    note: NoteEntity,
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
                        Icon(
                            imageVector = Icons.Filled.PushPin,
                            contentDescription = "Pin Note",
                            tint = if (note.isPinned) MedGreen else SoftText
                        )
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
                    color = SoftText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditNoteDialog(
    existingNote: NoteEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var title by remember(existingNote) { mutableStateOf(existingNote?.title ?: "") }
    var content by remember(existingNote) { mutableStateOf(existingNote?.content ?: "") }
    var category by remember(existingNote) { mutableStateOf(existingNote?.category ?: "General") }
    var noteDate by remember(existingNote) { mutableStateOf(existingNote?.noteDate ?: "") }
    var noteTime by remember(existingNote) { mutableStateOf(existingNote?.noteTime ?: "") }
    var error by remember(existingNote) { mutableStateOf("") }

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    shape = RoundedCornerShape(13.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = noteDate,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Date") },
                            trailingIcon = {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(13.dp)
                        )

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showDatePicker = true }
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = noteTime,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Time") },
                            trailingIcon = {
                                Icon(Icons.Default.AccessTime, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(13.dp)
                        )

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showTimePicker = true }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { showDatePicker = true }) {
                        Text("Choose Date")
                    }

                    TextButton(onClick = { showTimePicker = true }) {
                        Text("Choose Time")
                    }
                }

                Text("Category", fontWeight = FontWeight.SemiBold, color = DarkText)

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

                if (error.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank() || content.isBlank() || noteDate.isBlank() || noteTime.isBlank()) {
                        error = "Please complete all note fields."
                    } else {
                        onSave(
                            title.trim(),
                            content.trim(),
                            category,
                            noteDate,
                            noteTime
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MedGreen)
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

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            noteDate = formatDate(millis)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = {
                Text("Select Time", fontWeight = FontWeight.Bold)
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
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
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
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