package com.example.medshelf.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medshelf.R
import com.example.medshelf.model.DocumentEntity
import com.example.medshelf.model.ReminderEntity
import com.example.medshelf.viewmodel.DocumentViewModel
import com.example.medshelf.viewmodel.ReminderViewModel
import com.example.medshelf.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val MedGreen = Color(0xFF009688)
private val DarkText = Color(0xFF111827)
private val SoftText = Color(0xFF64748B)
private val SoftBorder = Color(0xFFE2E8F0)
private val Purple = Color(0xFF7C3AED)
private val Orange = Color(0xFFF59E0B)
private val ErrorRed = Color(0xFFEF4444)

private const val STATUS_COMPLETED = "Completed"

@Composable
fun DashboardScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    documentViewModel: DocumentViewModel,
    reminderViewModel: ReminderViewModel
) {
    val user = userViewModel.user.value
    val documents by documentViewModel.documents.collectAsState()
    val reminders by reminderViewModel.reminders.collectAsState()

    val firstName = user?.firstName ?: "User"
    val bloodType = user?.bloodType ?: "Not set"

    val recentDocuments = documents.take(3)

    val upcomingReminders = reminders
        .filter { reminder ->
            reminder.status != STATUS_COMPLETED &&
                    reminder.nextTriggerAtMillis > System.currentTimeMillis()
        }
        .sortedBy { it.nextTriggerAtMillis }
        .take(3)

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            MedShelfBottomBar(navController)
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White,
                            Color(0xFFF8FFFC),
                            Color(0xFFEFFFF8)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 18.dp, bottom = 110.dp)
            ) {
                item {
                    DashboardHeader(navController)
                }

                item {
                    GreetingSection(firstName)
                }


                item {
                    EmergencySnapshotBanner {
                        navController.navigate("emergency_snapshot")
                    }
                }

                item {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        QuickActionCard(
                            title = "Documents",
                            subtitle = "${documents.size} saved",
                            icon = Icons.Outlined.Folder,
                            bgColor = Color(0xFFE6F7F4),
                            iconColor = MedGreen,
                            modifier = Modifier.weight(1f)
                        ) {
                            navController.navigate("document_library")
                        }

                        QuickActionCard(
                            title = "Upload",
                            subtitle = "Add new",
                            icon = Icons.Filled.Add,
                            bgColor = Color(0xFFE9FBEF),
                            iconColor = Color(0xFF16A34A),
                            modifier = Modifier.weight(1f)
                        ) {
                            navController.navigate("add_document")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        QuickActionCard(
                            title = "Reminders",
                            subtitle = "${upcomingReminders.size} upcoming",
                            icon = Icons.Outlined.Notifications,
                            bgColor = Color(0xFFF0EAFE),
                            iconColor = Purple,
                            modifier = Modifier.weight(1f)
                        ) {
                            navController.navigate("reminders")
                        }

                        QuickActionCard(
                            title = "Health Notes",
                            subtitle = "Medical notes",
                            icon = Icons.Outlined.EditNote,
                            bgColor = Color(0xFFFFF4D8),
                            iconColor = Orange,
                            modifier = Modifier.weight(1f)
                        ) {
                            navController.navigate("notes")
                        }
                    }
                }

                item {
                    ProfilesCard(
                        userName = firstName,
                        bloodType = bloodType
                    )
                }

                item {
                    UpcomingRemindersCard(
                        reminders = upcomingReminders,
                        onClick = {
                            navController.navigate("reminders")
                        }
                    )
                }

                item {
                    RecentDocumentsCard(
                        navController = navController,
                        recentDocuments = recentDocuments
                    )
                }

                item {
                    HealthSummaryCard(
                        allergies = user?.allergies ?: "Not set",
                        conditions = user?.conditions ?: "Not set",
                        medications = user?.medications ?: "Not set",
                        onClick = {
                            navController.navigate("edit_profile")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.White.copy(alpha = 0.92f),
        tonalElevation = 0.dp,
        shadowElevation = 3.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE6F2EF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFE6F7F4), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_medshelf_logo),
                    contentDescription = "MedShelf Logo",
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Med",
                        color = MedGreen,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "Shelf",
                        color = DarkText,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Text(
                    text = "Personal medical library",
                    color = SoftText,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            HeaderIconButton(
                icon = Icons.Outlined.Notifications,
                iconColor = SoftText,
                backgroundColor = Color(0xFFF8FAFC),
                borderColor = SoftBorder
            ) {
                navController.navigate("reminders")
            }

            Spacer(modifier = Modifier.width(8.dp))

            HeaderIconButton(
                icon = Icons.Outlined.AccountCircle,
                iconColor = MedGreen,
                backgroundColor = Color(0xFFE6F7F4),
                borderColor = Color(0xFFCFF1EA)
            ) {
                navController.navigate("edit_profile")
            }
        }
    }
}

@Composable
private fun HeaderIconButton(
    icon: ImageVector,
    iconColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(backgroundColor, CircleShape)
            .border(1.dp, borderColor, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun GreetingSection(firstName: String) {
    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }

    Text(
        text = "$greeting, $firstName",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.ExtraBold,
        color = DarkText,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )

    Spacer(modifier = Modifier.height(3.dp))

    Text(
        text = "Here’s your latest health overview from saved records.",
        style = MaterialTheme.typography.bodyMedium,
        color = SoftText
    )
}
@Composable
private fun EmergencySnapshotBanner(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFE6F7F4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.HealthAndSafety,
                    contentDescription = null,
                    tint = MedGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Emergency Snapshot",
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Quick access to vital info",
                    color = SoftText,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Surface(
                color = Color(0xFFEAFBF7),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("View", color = MedGreen, fontWeight = FontWeight.Bold)

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MedGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    bgColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(118.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(bgColor, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = SoftText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProfilesCard(
    userName: String,
    bloodType: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            SectionTitle(title = "Profiles", onClick = {})

            Spacer(modifier = Modifier.height(12.dp))

            ProfileMiniCard(
                name = userName,
                info = "Main profile • $bloodType",
                selected = true,
                isYou = true
            )
        }
    }
}

@Composable
private fun ProfileMiniCard(
    name: String,
    info: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    isYou: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) MedGreen else SoftBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(Color(0xFFE6F7F4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MedGreen,
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(modifier = Modifier.width(9.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (isYou) {
                        Spacer(modifier = Modifier.width(5.dp))

                        Box(
                            modifier = Modifier
                                .background(
                                    color = MedGreen.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "You",
                                color = MedGreen,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = info,
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
private fun UpcomingRemindersCard(
    reminders: List<ReminderEntity>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6FFFC)),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6F5EF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Upcoming Reminders",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MedGreen
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Latest",
                    color = Purple,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Purple,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (reminders.isEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ReminderIconBox()

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "No upcoming reminders",
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )

                        Text(
                            text = "Tap to add a medication reminder",
                            color = SoftText,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                reminders.forEachIndexed { index, reminder ->
                    UpcomingReminderRow(reminder = reminder)

                    if (index != reminders.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = Color(0xFFD6F5EF)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpcomingReminderRow(reminder: ReminderEntity) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ReminderIconBox()

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = reminder.title.ifBlank { "Untitled Reminder" },
                fontWeight = FontWeight.Bold,
                color = DarkText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = formatReminderSchedule(reminder),
                color = SoftText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Surface(
            color = Purple.copy(alpha = 0.10f),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = formatReminderDateShort(reminder.nextTriggerAtMillis),
                color = Purple,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
            )
        }
    }
}

@Composable
private fun ReminderIconBox() {
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(Color(0xFFF0EAFE), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Medication,
            contentDescription = null,
            tint = Purple
        )
    }
}

private fun formatReminderSchedule(reminder: ReminderEntity): String {
    return if (reminder.scheduleType == "INTERVAL") {
        "Every ${reminder.intervalHours} hour(s) • ${reminder.profile}"
    } else {
        "${reminder.date}, ${reminder.time} • ${reminder.profile}"
    }
}

private fun formatReminderDateShort(millis: Long): String {
    if (millis <= 0L) return "Unset"

    val now = Calendar.getInstance()
    val reminderDate = Calendar.getInstance().apply {
        timeInMillis = millis
    }

    return when {
        now.get(Calendar.YEAR) == reminderDate.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == reminderDate.get(Calendar.DAY_OF_YEAR) -> {
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))
        }

        else -> {
            SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(millis))
        }
    }
}

@Composable
private fun RecentDocumentsCard(
    navController: NavController,
    recentDocuments: List<DocumentEntity>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle(
                title = "Recent Documents",
                onClick = { navController.navigate("document_library") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (recentDocuments.isEmpty()) {
                Text(
                    text = "No documents yet. Upload your first medical document.",
                    color = SoftText,
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                recentDocuments.forEachIndexed { index, document ->
                    DocumentRow(
                        title = document.name.ifBlank { "Untitled Document" },
                        subtitle = "${document.date.ifBlank { "No date" }} • ${document.type.ifBlank { "Uncategorized" }} • ${document.owner.ifBlank { "Main profile" }}",
                        icon = categoryIcon(document.type),
                        iconBg = categoryColor(document.type).copy(alpha = 0.12f),
                        iconColor = categoryColor(document.type),
                        onClick = {
                            navController.navigate("document_details/${document.id}")
                        }
                    )

                    if (index != recentDocuments.lastIndex) {
                        HorizontalDivider(color = SoftBorder)
                    }
                }
            }
        }
    }
}

@Composable
private fun HealthSummaryCard(
    allergies: String,
    conditions: String,
    medications: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAFBF7)),
        elevation = CardDefaults.cardElevation(1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6F5EF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Health Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MedGreen
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Allergies: ${allergies.ifBlank { "Not set" }}",
                    color = DarkText,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Conditions: ${conditions.ifBlank { "Not set" }}",
                    color = SoftText,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Meds: ${medications.ifBlank { "Not set" }}",
                    color = SoftText,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            DashboardGraphic()

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = SoftText
            )
        }
    }
}

@Composable
private fun DashboardGraphic() {
    Box(
        modifier = Modifier.size(82.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .background(Color(0xFFD6F5EF), CircleShape)
        )

        Icon(
            imageVector = Icons.Filled.HealthAndSafety,
            contentDescription = null,
            tint = MedGreen,
            modifier = Modifier.size(48.dp)
        )

        Box(
            modifier = Modifier
                .size(26.dp)
                .offset(x = 26.dp, y = 25.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(1.dp, SoftBorder, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.MedicalServices,
                contentDescription = null,
                tint = MedGreen,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    onClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "View all",
            color = SoftText,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onClick() }
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = SoftText,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun DocumentRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(iconBg, RoundedCornerShape(15.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = SoftText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = SoftText
        )
    }
}

private fun categoryIcon(type: String): ImageVector {
    return when {
        type.contains("Lab", ignoreCase = true) -> Icons.Default.Biotech
        type.contains("Prescription", ignoreCase = true) -> Icons.Default.Medication
        type.contains("Certificate", ignoreCase = true) -> Icons.Default.Badge
        type.contains("X-Ray", ignoreCase = true) || type.contains("Imaging", ignoreCase = true) -> Icons.Default.MonitorHeart
        type.contains("Vaccination", ignoreCase = true) -> Icons.Default.Vaccines
        type.contains("Discharge", ignoreCase = true) -> Icons.Default.LocalHospital
        type.contains("Bill", ignoreCase = true) -> Icons.AutoMirrored.Filled.ReceiptLong
        type.contains("Insurance", ignoreCase = true) -> Icons.Default.Shield
        type.contains("Note", ignoreCase = true) -> Icons.Default.NoteAlt
        type.contains("Clearance", ignoreCase = true) -> Icons.Default.HealthAndSafety
        type.contains("ECG", ignoreCase = true) || type.contains("Heart", ignoreCase = true) -> Icons.Default.Favorite
        else -> Icons.Default.Description
    }
}

private fun categoryColor(type: String): Color {
    return when {
        type.contains("Lab", ignoreCase = true) -> Color(0xFF0EA5E9)
        type.contains("Prescription", ignoreCase = true) -> Color(0xFF7C3AED)
        type.contains("Certificate", ignoreCase = true) -> Color(0xFFF59E0B)
        type.contains("X-Ray", ignoreCase = true) || type.contains("Imaging", ignoreCase = true) -> Color(0xFF6366F1)
        type.contains("Vaccination", ignoreCase = true) -> Color(0xFF16A34A)
        type.contains("Discharge", ignoreCase = true) -> Color(0xFFEF4444)
        type.contains("Bill", ignoreCase = true) -> Color(0xFFEA580C)
        type.contains("Insurance", ignoreCase = true) -> Color(0xFF0891B2)
        type.contains("Note", ignoreCase = true) -> Color(0xFF8B5CF6)
        type.contains("Clearance", ignoreCase = true) -> Color(0xFF14B8A6)
        type.contains("ECG", ignoreCase = true) || type.contains("Heart", ignoreCase = true) -> Color(0xFFE11D48)
        else -> MedGreen
    }
}