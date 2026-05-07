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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.medshelf.viewmodel.UserViewModel

private val MedGreen = Color(0xFF009688)
private val DarkText = Color(0xFF111827)
private val SoftText = Color(0xFF64748B)
private val SoftBorder = Color(0xFFE2E8F0)
private val Purple = Color(0xFF7C3AED)
private val Orange = Color(0xFFF59E0B)

@Composable
fun DashboardScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val user = userViewModel.user.value
    val firstName = user?.firstName ?: "User"
    val bloodType = user?.bloodType ?: "Not set"

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = { MedShelfBottomBar(navController) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFFFFF),
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
                contentPadding = PaddingValues(top = 22.dp, bottom = 110.dp)
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
                            subtitle = "View files",
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
                            subtitle = "Medication",
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
                    UpcomingReminderHighlight()
                }

                item {
                    RecentDocumentsCard(navController)
                }

                item {
                    HealthSummaryCard {
                        navController.navigate("edit_profile")
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_medshelf_logo),
            contentDescription = "MedShelf Logo",
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

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

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = { }) {
            BadgedBox(
                badge = { Badge(containerColor = MedGreen) }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = SoftText
                )
            }
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(Color(0xFFE6F7F4), CircleShape)
                .clickable { navController.navigate("edit_profile") },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = "Profile",
                tint = MedGreen,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun GreetingSection(firstName: String) {
    val greeting = when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }

    Text(
        text = "$greeting, $firstName",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        color = DarkText,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = "Here’s your health overview for today.",
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
                    Text(
                        text = "View",
                        color = MedGreen,
                        fontWeight = FontWeight.Bold
                    )

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
            SectionTitle(
                title = "Profiles",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ProfileMiniCard(
                    name = userName,
                    info = "Main profile • $bloodType",
                    selected = true,
                    modifier = Modifier.weight(1.45f),
                    isYou = true
                )

                ProfileMiniCard(
                    name = "Family",
                    info = "Add member",
                    selected = false,
                    modifier = Modifier.weight(1f)
                )
            }
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
        modifier = modifier.height(76.dp),
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
                    imageVector = if (selected) Icons.Default.Person else Icons.Default.PersonAdd,
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
private fun UpcomingReminderHighlight() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF6FFFC)),
        elevation = CardDefaults.cardElevation(2.dp),
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
                    text = "Upcoming Reminder",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MedGreen
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
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

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Take Vitamin D",
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )

                        Text(
                            text = "Today, 8:00 AM",
                            color = SoftText,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Surface(
                color = Color(0xFFF0EAFE),
                shape = RoundedCornerShape(50.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Due soon",
                        color = Purple,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Purple,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentDocumentsCard(navController: NavController) {
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

            DocumentRow(
                title = "Blood Test Report",
                subtitle = "May 18, 2024 • PDF • $CURRENT_PROFILE_LABEL",
                icon = Icons.Outlined.Description,
                iconBg = Color(0xFFE6F7F4),
                iconColor = MedGreen
            )

            HorizontalDivider(color = SoftBorder)

            DocumentRow(
                title = "Chest X-Ray",
                subtitle = "May 10, 2024 • JPG • Family",
                icon = Icons.Filled.MonitorHeart,
                iconBg = Color(0xFFF0EAFE),
                iconColor = Purple
            )

            HorizontalDivider(color = SoftBorder)

            DocumentRow(
                title = "Vaccination Record",
                subtitle = "Apr 28, 2024 • PDF • Family",
                icon = Icons.Filled.Vaccines,
                iconBg = Color(0xFFFFF4D8),
                iconColor = Orange
            )
        }
    }
}

private const val CURRENT_PROFILE_LABEL = "Main profile"

@Composable
private fun HealthSummaryCard(onClick: () -> Unit) {
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
                    text = "Information is ready for quick access.",
                    color = DarkText,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Last updated: Today",
                    color = SoftText,
                    style = MaterialTheme.typography.bodySmall
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
    iconColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
            imageVector = Icons.Default.MoreVert,
            contentDescription = null,
            tint = SoftText
        )
    }
}
