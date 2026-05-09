package com.example.medshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medshelf.model.ReminderEntity
import com.example.medshelf.viewmodel.ReminderViewModel
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

@Composable
fun RemindersScreen(
    navController: NavController,
    reminderViewModel: ReminderViewModel
) {
    val reminders by reminderViewModel.reminders.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingReminder by remember { mutableStateOf<ReminderEntity?>(null) }
    var reminderToDelete by remember { mutableStateOf<ReminderEntity?>(null) }

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
                    editingReminder = null
                    showDialog = true
                },
                containerColor = MedGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Reminder")
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White, Color(0xFFF9FFFC), Color(0xFFEFFFF8))
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

            if (reminders.isEmpty()) {
                item {
                    EmptyReminderState()
                }
            } else {
                items(
                    items = reminders,
                    key = { it.id }
                ) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onEdit = {
                            editingReminder = reminder
                            showDialog = true
                        },
                        onDelete = {
                            reminderToDelete = reminder
                        },
                        onComplete = {
                            reminderViewModel.markComplete(reminder)
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddEditReminderDialog(
            existingReminder = editingReminder,
            onDismiss = {
                showDialog = false
            },
            onSave = { title, date, time, profile, note, repeat ->
                val editing = editingReminder

                if (editing == null) {
                    reminderViewModel.addReminder(
                        title = title,
                        date = date,
                        time = time,
                        profile = profile,
                        note = note,
                        repeat = repeat
                    )
                } else {
                    reminderViewModel.updateReminder(
                        editing.copy(
                            title = title,
                            date = date,
                            time = time,
                            profile = profile,
                            note = note,
                            repeat = repeat
                        )
                    )
                }

                showDialog = false
            }
        )
    }

    reminderToDelete?.let { reminder ->
        AlertDialog(
            onDismissRequest = {
                reminderToDelete = null
            },
            title = {
                Text("Delete Reminder", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Are you sure you want to delete \"${reminder.title}\"?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        reminderViewModel.deleteReminder(reminder)
                        reminderToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        reminderToDelete = null
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

        Text(
            text = "$count active reminder${if (count == 1) "" else "s"} for you and your family",
            style = MaterialTheme.typography.bodyMedium,
            color = SoftText
        )
    }
}

@Composable
private fun NextReminderCard(reminder: ReminderEntity?) {
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
    reminder: ReminderEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onComplete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

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

                InfoLine(Icons.Filled.CalendarMonth, reminder.date)
                InfoLine(Icons.Filled.AccessTime, reminder.time)
                InfoLine(Icons.Filled.Person, reminder.profile)

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

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = reminder.repeat,
                    color = SoftText,
                    style = MaterialTheme.typography.labelSmall
                )

                Box {
                    IconButton(
                        onClick = {
                            menuExpanded = true
                        }
                    ) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = SoftText)
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = {
                            menuExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )

                        if (reminder.status != "Completed") {
                            DropdownMenuItem(
                                text = { Text("Mark Complete") },
                                leadingIcon = {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null)
                                },
                                onClick = {
                                    menuExpanded = false
                                    onComplete()
                                }
                            )
                        }

                        DropdownMenuItem(
                            text = { Text("Delete") },
                            leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
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
private fun EmptyReminderState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null,
                tint = MedGreen,
                modifier = Modifier.size(54.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "No reminders yet",
                color = DarkText,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Tap + to add your first reminder.",
                color = SoftText,
                style = MaterialTheme.typography.bodySmall
            )
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
    existingReminder: ReminderEntity?,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        date: String,
        time: String,
        profile: String,
        note: String,
        repeat: String
    ) -> Unit
) {
    var title by remember(existingReminder?.id) { mutableStateOf(existingReminder?.title ?: "") }
    var selectedDate by remember(existingReminder?.id) { mutableStateOf(existingReminder?.date ?: "") }
    var selectedTime by remember(existingReminder?.id) { mutableStateOf(existingReminder?.time ?: "") }
    var profile by remember(existingReminder?.id) { mutableStateOf(existingReminder?.profile ?: "Main profile") }
    var note by remember(existingReminder?.id) { mutableStateOf(existingReminder?.note ?: "") }
    var repeat by remember(existingReminder?.id) { mutableStateOf(existingReminder?.repeat ?: "Once") }
    var error by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var profileExpanded by remember { mutableStateOf(false) }
    var repeatExpanded by remember { mutableStateOf(false) }

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
                    value = title,
                    onValueChange = {
                        title = it
                        error = ""
                    },
                    label = { Text("Reminder Title") },
                    placeholder = { Text("e.g., Take medicine") },
                    leadingIcon = { Icon(Icons.Filled.Medication, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") },
                    placeholder = { Text("Select date") },
                    leadingIcon = { Icon(Icons.Filled.Event, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selectedTime,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Time") },
                    placeholder = { Text("Select time") },
                    leadingIcon = { Icon(Icons.Filled.Schedule, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(Icons.Filled.AccessTime, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = profileExpanded,
                    onExpandedChange = { profileExpanded = !profileExpanded }
                ) {
                    OutlinedTextField(
                        value = profile,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Profile") },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = profileExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(
                                type = MenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = profileExpanded,
                        onDismissRequest = {
                            profileExpanded = false
                        }
                    ) {
                        profiles.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    profile = item
                                    profileExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = repeatExpanded,
                    onExpandedChange = { repeatExpanded = !repeatExpanded }
                ) {
                    OutlinedTextField(
                        value = repeat,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Repeat") },
                        leadingIcon = { Icon(Icons.Filled.Notifications, contentDescription = null) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = repeatExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(
                                type = MenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = repeatExpanded,
                        onDismissRequest = {
                            repeatExpanded = false
                        }
                    ) {
                        repeatOptions.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    repeat = item
                                    repeatExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Notes") },
                    placeholder = { Text("Optional instructions") },
                    leadingIcon = { Icon(Icons.Filled.NoteAlt, contentDescription = null) },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                if (error.isNotBlank()) {
                    Text(
                        text = error,
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
                        title.isBlank() -> error = "Please enter a reminder title."
                        selectedDate.isBlank() -> error = "Please select a date."
                        selectedTime.isBlank() -> error = "Please select a time."
                        else -> onSave(
                            title.trim(),
                            selectedDate,
                            selectedTime,
                            profile,
                            note.trim(),
                            repeat
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
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = formatDate(millis)
                        }

                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = {
                showTimePicker = false
            },
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
                        selectedTime = formatTime(
                            hour = timePickerState.hour,
                            minute = timePickerState.minute
                        )

                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
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