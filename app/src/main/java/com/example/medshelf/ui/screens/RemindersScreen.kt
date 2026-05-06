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
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
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

private val MedGreen = Color(0xFF009688)
private val DarkText = Color(0xFF111827)
private val SoftText = Color(0xFF64748B)
private val SoftBorder = Color(0xFFE2E8F0)
private val Purple = Color(0xFF7C3AED)
private val Orange = Color(0xFFF59E0B)

data class ReminderItem(
    val title: String,
    val time: String,
    val profile: String,
    val note: String,
    val status: String
)

@Composable
fun RemindersScreen(navController: NavController) {
    val showAddDialog = remember { mutableStateOf(false) }

    val reminders = remember {
        mutableStateListOf(
            ReminderItem("Take Vitamin D", "Today, 8:00 AM", "Main profile", "After breakfast", "Due soon"),
            ReminderItem("Blood pressure check", "Today, 6:00 PM", "Family member", "Record reading in Health Notes", "Upcoming"),
            ReminderItem("Doctor follow-up", "Tomorrow, 10:30 AM", "Main profile", "Bring latest lab result", "Scheduled")
        )
    }

    Scaffold(
        topBar = {
            MedShelfTopBar("Reminders", navController, true)
        },
        bottomBar = {
            MedShelfBottomBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog.value = true },
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
                ReminderHeader(count = reminders.size)
            }

            item {
                TodayReminderCard()
            }

            item {
                Text(
                    text = "All Reminders",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
            }

            items(reminders) { reminder ->
                ReminderCard(reminder = reminder)
            }
        }
    }

    if (showAddDialog.value) {
        AddReminderDialog(
            onDismiss = {
                showAddDialog.value = false
            },
            onSave = { title, time, profile, note ->
                reminders.add(
                    ReminderItem(
                        title = title,
                        time = time,
                        profile = profile,
                        note = note,
                        status = "Scheduled"
                    )
                )
                showAddDialog.value = false
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
private fun TodayReminderCard() {
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
                    text = "Take Vitamin D",
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkText,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Today, 8:00 AM • Main profile",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftText
                )
            }

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = "Due soon",
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
private fun ReminderCard(reminder: ReminderItem) {
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

                InfoLine(Icons.Filled.AccessTime, reminder.time)

                Spacer(modifier = Modifier.height(2.dp))

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

                Spacer(modifier = Modifier.height(12.dp))

                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More",
                    tint = SoftText
                )
            }
        }
    }
}

@Composable
private fun ReminderIcon(status: String) {
    val color = when (status) {
        "Due soon" -> Purple
        "Upcoming" -> Orange
        else -> MedGreen
    }

    Box(
        modifier = Modifier
            .size(50.dp)
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Medication,
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

@Composable
private fun AddReminderDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var profile by remember { mutableStateOf("Main profile") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Reminder",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reminder Title") },
                    placeholder = { Text("e.g., Take medicine") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time") },
                    placeholder = { Text("e.g., Today, 8:00 PM") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = profile,
                    onValueChange = { profile = it },
                    label = { Text("Profile") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Notes") },
                    placeholder = { Text("Optional") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && time.isNotBlank()) {
                        onSave(
                            title.trim(),
                            time.trim(),
                            profile.trim().ifBlank { "Main profile" },
                            note.trim()
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
}