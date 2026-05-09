package com.example.medshelf.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
private val SuccessGreen = Color(0xFF16A34A)

private const val SCHEDULE_DATE_TIME = "DATE_TIME"
private const val SCHEDULE_INTERVAL = "INTERVAL"
private const val STATUS_COMPLETED = "Completed"
private const val STATUS_SCHEDULED = "Scheduled"

@Composable
fun RemindersScreen(
    navController: NavController,
    reminderViewModel: ReminderViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val reminders by reminderViewModel.reminders.collectAsState()
    
    // Ticker to update "time left" every minute
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(60000)
            currentTime = System.currentTimeMillis()
        }
    }

    val showDialog = remember { mutableStateOf(false) }
    val editingReminder = remember { mutableStateOf<ReminderEntity?>(null) }
    val reminderToDelete = remember { mutableStateOf<ReminderEntity?>(null) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {}

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(android.app.AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        }
    }

    val activeReminders = reminders.filter { it.status != STATUS_COMPLETED }
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
                    showDialog.value = true
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
                NextReminderCard(reminder = nextReminder, currentTime = currentTime)
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
                        currentTime = currentTime,
                        onEdit = {
                            editingReminder.value = reminder
                            showDialog.value = true
                        },
                        onDelete = {
                            reminderToDelete.value = reminder
                        },
                        onComplete = {
                            reminderViewModel.markComplete(reminder)
                        },
                        onRestore = {
                            reminderViewModel.markActive(reminder)
                        }
                    )
                }
            }
        }
    }

    if (showDialog.value) {
        AddEditReminderDialog(
            existingReminder = editingReminder.value,
            onDismiss = {
                showDialog.value = false
            },
            onSave = { reminderData ->
                val currentEditingReminder = editingReminder.value

                if (currentEditingReminder == null) {
                    reminderViewModel.addReminder(reminderData)
                } else {
                    reminderViewModel.updateReminder(
                        reminderData.copy(
                            id = currentEditingReminder.id,
                            createdAt = currentEditingReminder.createdAt
                        )
                    )
                }

                showDialog.value = false
            }
        )
    }

    reminderToDelete.value?.let { reminder ->
        DeleteReminderDialog(
            reminder = reminder,
            onDismiss = {
                reminderToDelete.value = null
            },
            onConfirmDelete = {
                reminderViewModel.deleteReminder(reminder)
                reminderToDelete.value = null
            }
        )
    }
}

@Composable
private fun DeleteReminderDialog(
    reminder: ReminderEntity,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete Reminder",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("Are you sure you want to delete \"${reminder.title}\"?")
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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
private fun NextReminderCard(reminder: ReminderEntity?, currentTime: Long) {
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

                val timeStatus = reminder?.let { formatTimeStatus(it.nextTriggerAtMillis, currentTime) }
                
                Text(
                    text = reminder?.let {
                        val details = if (it.scheduleType == SCHEDULE_INTERVAL) {
                            "Every ${it.intervalHours} hour(s) • ${it.profile}"
                        } else {
                            "${it.date}, ${it.time} • ${it.profile}"
                        }
                        if (timeStatus != null) "$timeStatus\n$details" else details
                    } ?: "Add your first reminder",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (timeStatus?.startsWith("Overdue") == true) ErrorRed else SoftText,
                    lineHeight = 16.sp
                )
            }

            Surface(
                color = if ((reminder?.nextTriggerAtMillis ?: 0) < currentTime && reminder?.status != STATUS_COMPLETED) 
                    ErrorRed.copy(alpha = 0.1f) else Color.White,
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = if ((reminder?.nextTriggerAtMillis ?: 0) < currentTime && reminder?.status != STATUS_COMPLETED) 
                        "Overdue" else reminder?.status ?: "Empty",
                    color = if ((reminder?.nextTriggerAtMillis ?: 0) < currentTime && reminder?.status != STATUS_COMPLETED)
                        ErrorRed else MedGreen,
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
    currentTime: Long,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onComplete: () -> Unit,
    onRestore: () -> Unit
) {
    val menuExpanded = remember { mutableStateOf(false) }
    val isOverdue = reminder.nextTriggerAtMillis < currentTime && reminder.status != STATUS_COMPLETED
    val timeStatus = formatTimeStatus(reminder.nextTriggerAtMillis, currentTime)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isOverdue) ErrorRed.copy(alpha = 0.5f) else SoftBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReminderIcon(status = if (isOverdue) "Overdue" else reminder.status)

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isOverdue) ErrorRed else DarkText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (timeStatus.isNotBlank()) {
                    Text(
                        text = timeStatus,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isOverdue) ErrorRed else MedGreen,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (reminder.scheduleType == SCHEDULE_INTERVAL) {
                    InfoLine(Icons.Filled.Schedule, "Every ${reminder.intervalHours} hour(s)")
                    InfoLine(Icons.Filled.AccessTime, "Starts: ${reminder.date}, ${reminder.time}")
                } else {
                    InfoLine(Icons.Filled.CalendarMonth, reminder.date)
                    InfoLine(Icons.Filled.AccessTime, reminder.time)
                }

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
                StatusChip(status = reminder.status)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (reminder.scheduleType == SCHEDULE_INTERVAL) {
                        "Every ${reminder.intervalHours}h"
                    } else {
                        reminder.repeat
                    },
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
                            text = {
                                Text("Edit Reminder")
                            },
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

                        HorizontalDivider()

                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (reminder.status == STATUS_COMPLETED) {
                                        "Mark as Active"
                                    } else {
                                        "Mark as Completed"
                                    }
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (reminder.status == STATUS_COMPLETED) {
                                        Icons.Filled.Notifications
                                    } else {
                                        Icons.Filled.CheckCircle
                                    },
                                    contentDescription = null,
                                    tint = if (reminder.status == STATUS_COMPLETED) {
                                        MedGreen
                                    } else {
                                        SuccessGreen
                                    }
                                )
                            },
                            onClick = {
                                menuExpanded.value = false

                                if (reminder.status == STATUS_COMPLETED) {
                                    onRestore()
                                } else {
                                    onComplete()
                                }
                            }
                        )

                        HorizontalDivider()

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Delete Reminder",
                                    color = ErrorRed
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = null,
                                    tint = ErrorRed
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
        STATUS_COMPLETED -> MedGreen
        else -> MedGreen
    }

    val icon = if (status == STATUS_COMPLETED) {
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
        STATUS_COMPLETED -> MedGreen
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
    onSave: (ReminderEntity) -> Unit
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

    val scheduleType = remember(existingReminder?.id) {
        mutableStateOf(existingReminder?.scheduleType ?: SCHEDULE_DATE_TIME)
    }

    val intervalHours = remember(existingReminder?.id) {
        mutableStateOf(
            if ((existingReminder?.intervalHours ?: 0) > 0) {
                existingReminder?.intervalHours.toString()
            } else {
                "6"
            }
        )
    }

    val error = remember { mutableStateOf("") }

    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }
    val profileExpanded = remember { mutableStateOf(false) }
    val repeatExpanded = remember { mutableStateOf(false) }
    val scheduleExpanded = remember { mutableStateOf(false) }

    val profiles = listOf("Main profile", "Family member")
    val repeatOptions = listOf("Once", "Daily", "Weekly", "Monthly")
    val scheduleOptions = listOf(SCHEDULE_DATE_TIME, SCHEDULE_INTERVAL)

    val datePickerState = rememberDatePickerState()
    
    // Parse existing time for the picker
    val initialTime = remember(existingReminder?.id) {
        if (existingReminder != null) {
            try {
                val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
                val date = sdf.parse(existingReminder.time)
                val calendar = java.util.Calendar.getInstance()
                if (date != null) calendar.time = date
                Pair(calendar.get(java.util.Calendar.HOUR_OF_DAY), calendar.get(java.util.Calendar.MINUTE))
            } catch (e: Exception) {
                Pair(8, 0)
            }
        } else {
            Pair(8, 0)
        }
    }

    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.first,
        initialMinute = initialTime.second,
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
                    onValueChange = {
                        title.value = it
                        error.value = ""
                    },
                    label = { Text("Reminder Title") },
                    placeholder = { Text("e.g., Take medicine") },
                    leadingIcon = {
                        Icon(Icons.Filled.Medication, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = scheduleExpanded.value,
                    onExpandedChange = {
                        scheduleExpanded.value = !scheduleExpanded.value
                    }
                ) {
                    OutlinedTextField(
                        value = if (scheduleType.value == SCHEDULE_DATE_TIME) {
                            "Specific Date & Time"
                        } else {
                            "Every _ Hours"
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Schedule Type") },
                        leadingIcon = {
                            Icon(Icons.Filled.Schedule, contentDescription = null)
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = scheduleExpanded.value
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
                        expanded = scheduleExpanded.value,
                        onDismissRequest = {
                            scheduleExpanded.value = false
                        }
                    ) {
                        scheduleOptions.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (item == SCHEDULE_DATE_TIME) {
                                            "Specific Date & Time"
                                        } else {
                                            "Every X Hours"
                                        }
                                    )
                                },
                                onClick = {
                                    scheduleType.value = item
                                    scheduleExpanded.value = false
                                    error.value = ""
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = selectedDate.value,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            if (scheduleType.value == SCHEDULE_INTERVAL) {
                                "Start Date"
                            } else {
                                "Date"
                            }
                        )
                    },
                    placeholder = { Text("Select date") },
                    leadingIcon = {
                        Icon(Icons.Filled.Event, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                showDatePicker.value = true
                            }
                        ) {
                            Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selectedTime.value,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            if (scheduleType.value == SCHEDULE_INTERVAL) {
                                "Start Time"
                            } else {
                                "Time"
                            }
                        )
                    },
                    placeholder = { Text("Select time") },
                    leadingIcon = {
                        Icon(Icons.Filled.Schedule, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                showTimePicker.value = true
                            }
                        ) {
                            Icon(Icons.Filled.AccessTime, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (scheduleType.value == SCHEDULE_INTERVAL) {
                    OutlinedTextField(
                        value = intervalHours.value,
                        onValueChange = {
                            intervalHours.value = it.filter { char -> char.isDigit() }
                            error.value = ""
                        },
                        label = { Text("Interval Hours") },
                        placeholder = { Text("e.g., 6, 8, 12") },
                        leadingIcon = {
                            Icon(Icons.Filled.AccessTime, contentDescription = null)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
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

                OutlinedTextField(
                    value = note.value,
                    onValueChange = {
                        note.value = it
                    },
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
                    val interval = intervalHours.value.toIntOrNull() ?: 0
                    val nextTrigger = calculateNextTriggerMillis(
                        selectedDate.value,
                        selectedTime.value,
                        scheduleType.value,
                        interval
                    )

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

                        scheduleType.value == SCHEDULE_INTERVAL && interval <= 0 -> {
                            error.value = "Please enter a valid interval hour."
                        }

                        nextTrigger <= 0L -> {
                            error.value = "Invalid reminder schedule."
                        }

                        else -> {
                            onSave(
                                ReminderEntity(
                                    id = existingReminder?.id ?: 0,
                                    title = title.value.trim(),
                                    date = selectedDate.value,
                                    time = selectedTime.value,
                                    profile = profile.value,
                                    note = note.value.trim(),
                                    repeat = if (scheduleType.value == SCHEDULE_INTERVAL) {
                                        "Every ${interval}h"
                                    } else {
                                        repeat.value
                                    },
                                    status = existingReminder?.status ?: STATUS_SCHEDULED,
                                    scheduleType = scheduleType.value,
                                    intervalHours = if (scheduleType.value == SCHEDULE_INTERVAL) {
                                        interval
                                    } else {
                                        0
                                    },
                                    nextTriggerAtMillis = nextTrigger,
                                    createdAt = existingReminder?.createdAt
                                        ?: System.currentTimeMillis()
                                )
                            )
                        }
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

    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
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

private fun calculateNextTriggerMillis(
    date: String,
    time: String,
    scheduleType: String,
    intervalHours: Int
): Long {
    val dateTimeMillis = parseDateTimeMillis(date, time)
    val now = System.currentTimeMillis()

    return if (scheduleType == SCHEDULE_INTERVAL) {
        if (dateTimeMillis > now) {
            dateTimeMillis
        } else {
            // If start time is in the past, find the next interval slot
            val diff = now - dateTimeMillis
            val intervalsPassed = (diff / (intervalHours * 3600000L)) + 1
            dateTimeMillis + (intervalsPassed * intervalHours * 3600000L)
        }
    } else {
        // For specific date/time, if it's in the past, we should ideally not allow it or return -1
        if (dateTimeMillis < now) -1L else dateTimeMillis
    }
}

private fun parseDateTimeMillis(
    date: String,
    time: String
): Long {
    return try {
        val formatter = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault())
        formatter.parse("$date $time")?.time ?: 0L
    } catch (_: Exception) {
        0L
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

private fun formatTimeStatus(triggerMillis: Long, currentMillis: Long): String {
    if (triggerMillis <= 0) return ""
    
    val diff = triggerMillis - currentMillis
    val absDiff = kotlin.math.abs(diff)
    val minutes = (absDiff / 60000) % 60
    val hours = (absDiff / 3600000) % 24
    val days = (absDiff / 86400000)

    val timeStr = when {
        days > 0 -> "$days d $hours h left"
        hours > 0 -> "$hours h $minutes m left"
        minutes > 0 -> "$minutes m left"
        else -> "Due now"
    }

    return if (diff < 0) {
        val overdueStr = when {
            days > 0 -> "$days d $hours h"
            hours > 0 -> "$hours h $minutes m"
            else -> "$minutes m"
        }
        "Overdue by $overdueStr"
    } else {
        timeStr
    }
}