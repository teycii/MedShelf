package com.example.medshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
private val Purple = Color(0xFF7C3AED)
private val Orange = Color(0xFFF59E0B)
private val ErrorRed = Color(0xFFEF4444)

data class ReminderItem(
    val id: Long,
    val title: String,
    val date: String,
    val time: String,
    val profile: String,
    val note: String,
    val repeat: String,
    val status: String
)

@Composable
fun RemindersScreen(navController: NavController) {
    val showReminderDialog = remember { mutableStateOf(false) }
    val editingReminder = remember { mutableStateOf<ReminderItem?>(null) }
    val reminderToDelete = remember { mutableStateOf<ReminderItem?>(null) }

    val reminders = remember {
        mutableStateListOf(
            ReminderItem(
                id = 1L,
                title = "Take Vitamin D",
                date = "Today",
                time = "8:00 AM",
                profile = "Main profile",
                note = "After breakfast",
                repeat = "Daily",
                status = "Due soon"
            ),
            ReminderItem(
                id = 2L,
                title = "Blood pressure check",
                date = "Today",
                time = "6:00 PM",
                profile = "Family member",
                note = "Record reading in Health Notes",
                repeat = "Once",
                status = "Upcoming"
            ),
            ReminderItem(
                id = 3L,
                title = "Doctor follow-up",
                date = "Tomorrow",
                time = "10:30 AM",
                profile = "Main profile",
                note = "Bring latest lab result",
                repeat = "Once",
                status = "Scheduled"
            )
        )
    }

    val activeReminders = reminders.filter { it.status != "Completed" }
    val nextReminder = activeReminders.firstOrNull()

    Scaffold(
        topBar = {
            MedShelfTopBar(
                title = "Reminders",
                navController = navController,
                showBackButton = true
            )
        },
        bottomBar = {
            MedShelfBottomBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingReminder.value = null
                    showReminderDialog.value = true
                },
                containerColor = MedGreen,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Reminder"
                )
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White,
                            Color(0xFFF9FFFC),
                            Color(0xFFEFFFF8)
                        )
                    )
                )
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 18.dp,
                bottom = 115.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ReminderHeader(count = activeReminders.size)
            }

            item {
                NextReminderCard(reminder = nextReminder)
            }

            item {
                Text(
                    text = "All Reminders",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
            }

            items(
                items = reminders,
                key = { it.id }
            ) { reminder ->
                ReminderCard(
                    reminder = reminder,
                    onEdit = {
                        editingReminder.value = reminder
                        showReminderDialog.value = true
                    },
                    onDelete = {
                        reminderToDelete.value = reminder
                    },
                    onComplete = {
                        val index = reminders.indexOfFirst { it.id == reminder.id }
                        if (index != -1) {
                            reminders[index] = reminder.copy(status = "Completed")
                        }
                    }
                )
            }
        }
    }

    if (showReminderDialog.value) {
        AddEditReminderDialog(
            existingReminder = editingReminder.value,
            onDismiss = {
                showReminderDialog.value = false
                editingReminder.value = null
            },
            onSave = { savedReminder ->
                val editing = editingReminder.value

                if (editing == null) {
                    reminders.add(
                        savedReminder.copy(
                            id = System.currentTimeMillis()
                        )
                    )
                } else {
                    val index = reminders.indexOfFirst { it.id == editing.id }
                    if (index != -1) {
                        reminders[index] = savedReminder.copy(id = editing.id)
                    }
                }

                showReminderDialog.value = false
                editingReminder.value = null
            }
        )
    }

    reminderToDelete.value?.let { reminder ->
        AlertDialog(
            onDismissRequest = {
                reminderToDelete.value = null
            },
            title = {
                Text(
                    text = "Delete Reminder",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Are you sure you want to delete ${reminder.title}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        reminders.remove(reminder)
                        reminderToDelete.value = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        reminderToDelete.value = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ReminderHeader(count: Int) {
    Column {
        Text(
            text = "Medication & Health Reminders",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = DarkText
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "$count active reminder${if (count == 1) "" else "s"} for you and your family",
            style = MaterialTheme.typography.bodyMedium,
            color = SoftText
        )
    }
}

@Composable
private fun NextReminderCard(reminder: ReminderItem?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAFBF7)),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6F5EF))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(MedGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Next Reminder",
                    style = MaterialTheme.typography.bodySmall,
                    color = MedGreen,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = reminder?.title ?: "No reminder set",
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkText,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = reminder?.let { "${it.date}, ${it.time} • ${it.profile}" }
                        ?: "Add your first reminder",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = reminder?.status ?: "Empty",
                    color = MedGreen,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun ReminderCard(
    reminder: ReminderItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onComplete: () -> Unit
) {
    val menuExpanded = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReminderIcon(status = reminder.status)

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                InfoLine(
                    icon = Icons.Filled.CalendarMonth,
                    text = reminder.date
                )

                Spacer(modifier = Modifier.height(2.dp))

                InfoLine(
                    icon = Icons.Filled.AccessTime,
                    text = reminder.time
                )

                Spacer(modifier = Modifier.height(2.dp))

                InfoLine(
                    icon = Icons.Filled.Person,
                    text = reminder.profile
                )

                if (reminder.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = reminder.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                StatusChip(reminder.status)

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = reminder.repeat,
                    color = SoftText,
                    style = MaterialTheme.typography.labelSmall
                )

                Box {
                    IconButton(
                        onClick = {
                            menuExpanded.value = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More",
                            tint = SoftText
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded.value,
                        onDismissRequest = {
                            menuExpanded.value = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                menuExpanded.value = false
                                onEdit()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Mark Complete") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                menuExpanded.value = false
                                onComplete()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Delete") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                menuExpanded.value = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderIcon(status: String) {
    val color = when (status) {
        "Due soon" -> Purple
        "Upcoming" -> Orange
        "Completed" -> MedGreen
        else -> MedGreen
    }

    val icon = if (status == "Completed") {
        Icons.Filled.CheckCircle
    } else {
        Icons.Filled.Medication
    }

    Box(
        modifier = Modifier
            .size(50.dp)
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun InfoLine(
    icon: ImageVector,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SoftText,
            modifier = Modifier.size(15.dp)
        )

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = SoftText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun StatusChip(status: String) {
    val color = when (status) {
        "Due soon" -> Purple
        "Upcoming" -> Orange
        "Completed" -> MedGreen
        else -> MedGreen
    }

    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(50.dp)
    ) {
        Text(
            text = status,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditReminderDialog(
    existingReminder: ReminderItem?,
    onDismiss: () -> Unit,
    onSave: (ReminderItem) -> Unit
) {
    val title = remember(existingReminder?.id) {
        mutableStateOf(existingReminder?.title ?: "")
    }

    val selectedDate = remember(existingReminder?.id) {
        mutableStateOf(existingReminder?.date ?: "")
    }

    val selectedTime = remember(existingReminder?.id) {
        mutableStateOf(existingReminder?.time ?: "")
    }

    val profile = remember(existingReminder?.id) {
        mutableStateOf(existingReminder?.profile ?: "Main profile")
    }

    val note = remember(existingReminder?.id) {
        mutableStateOf(existingReminder?.note ?: "")
    }

    val repeat = remember(existingReminder?.id) {
        mutableStateOf(existingReminder?.repeat ?: "Once")
    }

    val error = remember { mutableStateOf("") }

    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }
    val profileExpanded = remember { mutableStateOf(false) }
    val repeatExpanded = remember { mutableStateOf(false) }

    val profiles = listOf("Main profile", "Family member")
    val repeatOptions = listOf("Once", "Daily", "Weekly", "Monthly")

    val datePickerState = rememberDatePickerState()

    val timePickerState = rememberTimePickerState(
        initialHour = 8,
        initialMinute = 0,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existingReminder == null) "Add Reminder" else "Edit Reminder",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title.value,
                    onValueChange = { title.value = it },
                    label = { Text("Reminder Title") },
                    placeholder = { Text("e.g., Take medicine") },
                    leadingIcon = {
                        Icon(Icons.Filled.Medication, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selectedDate.value,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") },
                    placeholder = { Text("Select date") },
                    leadingIcon = {
                        Icon(Icons.Filled.Event, contentDescription = null)
                    },
                    trailingIcon = {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp)
                )

                TextButton(
                    onClick = {
                        showDatePicker.value = true
                    }
                ) {
                    Text("Choose Date")
                }

                OutlinedTextField(
                    value = selectedTime.value,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Time") },
                    placeholder = { Text("Select time") },
                    leadingIcon = {
                        Icon(Icons.Filled.Schedule, contentDescription = null)
                    },
                    trailingIcon = {
                        Icon(Icons.Filled.AccessTime, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                TextButton(
                    onClick = {
                        showTimePicker.value = true
                    }
                ) {
                    Text("Choose Time")
                }

                ExposedDropdownMenuBox(
                    expanded = profileExpanded.value,
                    onExpandedChange = {
                        profileExpanded.value = !profileExpanded.value
                    }
                ) {
                    OutlinedTextField(
                        value = profile.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Profile") },
                        leadingIcon = {
                            Icon(Icons.Filled.Person, contentDescription = null)
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = profileExpanded.value
                            )
                        },
                        modifier = Modifier
                            .menuAnchor(
                                type = MenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = profileExpanded.value,
                        onDismissRequest = {
                            profileExpanded.value = false
                        }
                    ) {
                        profiles.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    profile.value = item
                                    profileExpanded.value = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = repeatExpanded.value,
                    onExpandedChange = {
                        repeatExpanded.value = !repeatExpanded.value
                    }
                ) {
                    OutlinedTextField(
                        value = repeat.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Repeat") },
                        leadingIcon = {
                            Icon(Icons.Filled.Notifications, contentDescription = null)
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = repeatExpanded.value
                            )
                        },
                        modifier = Modifier
                            .menuAnchor(
                                type = MenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = repeatExpanded.value,
                        onDismissRequest = {
                            repeatExpanded.value = false
                        }
                    ) {
                        repeatOptions.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    repeat.value = item
                                    repeatExpanded.value = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = note.value,
                    onValueChange = { note.value = it },
                    label = { Text("Notes") },
                    placeholder = { Text("Optional instructions") },
                    leadingIcon = {
                        Icon(Icons.Filled.NoteAlt, contentDescription = null)
                    },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                if (error.value.isNotBlank()) {
                    Text(
                        text = error.value,
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        title.value.isBlank() -> {
                            error.value = "Please enter a reminder title."
                        }

                        selectedDate.value.isBlank() -> {
                            error.value = "Please select a date."
                        }

                        selectedTime.value.isBlank() -> {
                            error.value = "Please select a time."
                        }

                        else -> {
                            onSave(
                                ReminderItem(
                                    id = existingReminder?.id ?: 0L,
                                    title = title.value.trim(),
                                    date = selectedDate.value,
                                    time = selectedTime.value,
                                    profile = profile.value,
                                    note = note.value.trim(),
                                    repeat = repeat.value,
                                    status = existingReminder?.status ?: "Scheduled"
                                )
                            )
                        }
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
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )

    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis

                        if (millis != null) {
                            selectedDate.value = formatDate(millis)
                        }

                        showDatePicker.value = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker.value = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker.value) {
        AlertDialog(
            onDismissRequest = {
                showTimePicker.value = false
            },
            title = {
                Text(
                    text = "Select Time",
                    fontWeight = FontWeight.Bold
                )
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
                        selectedTime.value = formatTime(
                            hour = timePickerState.hour,
                            minute = timePickerState.minute
                        )

                        showTimePicker.value = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTimePicker.value = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
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