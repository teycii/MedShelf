package com.example.medshelf.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medshelf.R
import com.example.medshelf.viewmodel.UserViewModel

private val MedGreen = Color(0xFF009688)
private val DarkText = Color(0xFF111827)
private val SoftText = Color(0xFF64748B)
private val SoftBorder = Color(0xFFE2E8F0)

@Composable
fun DashboardScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    LaunchedEffect(Unit) {
        userViewModel.loadUser()
    }

    val user = userViewModel.user.value
    val firstName = user?.firstName ?: "User"

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            DashboardBottomBar(navController)
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White,
                            Color.White,
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
                    DashboardHeader(
                        navController = navController
                    )
                }

                item {
                    Text(
                        text = "Good morning, $firstName",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkText
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Here's your health overview for today.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftText
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            CompactQuickCard(
                                title = "Documents",
                                subtitle = "View files",
                                icon = Icons.Outlined.Folder,
                                bgColor = Color(0xFFE6F7F4),
                                iconColor = MedGreen,
                                modifier = Modifier.weight(1f)
                            ) {
                                navController.navigate("document_library")
                            }

                            CompactQuickCard(
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

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            CompactQuickCard(
                                title = "Reminders",
                                subtitle = "Medication",
                                icon = Icons.Outlined.Notifications,
                                bgColor = Color(0xFFF0EAFE),
                                iconColor = Color(0xFF7C3AED),
                                modifier = Modifier.weight(1f)
                            ) {
                                navController.navigate("reminders")
                            }

                            CompactQuickCard(
                                title = "Health Notes",
                                subtitle = "Medical notes",
                                icon = Icons.Outlined.EditNote,
                                bgColor = Color(0xFFFFF4D8),
                                iconColor = Color(0xFFF59E0B),
                                modifier = Modifier.weight(1f)
                            ) {
                                navController.navigate("notes")
                            }
                        }
                    }
                }

                item {
                    ProfilesCard(
                        userName = firstName,
                        bloodType = user?.bloodType ?: "Not set"
                    )
                }

                item {
                    HealthSummaryCard {
                        navController.navigate("edit_profile")
                    }
                }

                item {
                    RecentDocumentsCard(navController)
                }

                item {
                    UpcomingReminderCard()
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
            modifier = Modifier.size(42.dp)
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
                badge = {
                    Badge(containerColor = MedGreen)
                }
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
private fun CompactQuickCard(
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
            .height(78.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(bgColor, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftText
                )
            }
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
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Profiles",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "View all",
                    color = SoftText,
                    fontWeight = FontWeight.Medium
                )

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = SoftText
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileMiniCard(
                    name = "$userName  You",
                    info = "Main profile • $bloodType",
                    selected = true,
                    modifier = Modifier.weight(1f)
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(82.dp),
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFFE6F7F4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (selected) Icons.Default.Person else Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = MedGreen
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Text(
                    text = info,
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftText
                )
            }
        }
    }
}

@Composable
private fun HealthSummaryCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAFBF7)),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Health Summary",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MedGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Keep medical information updated and accessible.",
                    color = DarkText,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.clickable { onClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Update Health Info",
                        color = MedGreen,
                        fontWeight = FontWeight.Bold
                    )

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MedGreen
                    )
                }
            }

            DashboardGraphic()
        }
    }
}

@Composable
private fun DashboardGraphic() {
    Box(
        modifier = Modifier.size(116.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(82.dp)
                .background(Color(0xFFD6F5EF), CircleShape)
        )

        Icon(
            imageVector = Icons.Filled.HealthAndSafety,
            contentDescription = null,
            tint = MedGreen,
            modifier = Modifier.size(72.dp)
        )

        Box(
            modifier = Modifier
                .size(34.dp)
                .offset(x = 34.dp, y = 32.dp)
                .background(Color.White, RoundedCornerShape(10.dp))
                .border(1.dp, SoftBorder, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.MedicalServices,
                contentDescription = null,
                tint = MedGreen,
                modifier = Modifier.size(22.dp)
            )
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
        Column(modifier = Modifier.padding(18.dp)) {
            SectionTitle(
                title = "Recent Documents",
                onClick = { navController.navigate("document_library") }
            )

            Spacer(modifier = Modifier.height(14.dp))

            DocumentRow(
                title = "Blood Test Report",
                subtitle = "Main profile • May 18, 2024 • PDF",
                icon = Icons.Outlined.Description,
                iconBg = Color(0xFFE6F7F4),
                iconColor = MedGreen
            )

            HorizontalDivider(color = SoftBorder)

            DocumentRow(
                title = "Chest X-Ray",
                subtitle = "Family member • May 10, 2024 • JPG",
                icon = Icons.Filled.MonitorHeart,
                iconBg = Color(0xFFF0EAFE),
                iconColor = Color(0xFF7C3AED)
            )

            HorizontalDivider(color = SoftBorder)

            DocumentRow(
                title = "Vaccination Record",
                subtitle = "Family member • Apr 28, 2024 • PDF",
                icon = Icons.Filled.Vaccines,
                iconBg = Color(0xFFFFF4D8),
                iconColor = Color(0xFFF59E0B)
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
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "View all",
            color = SoftText,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onClick() }
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = SoftText
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
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(iconBg, RoundedCornerShape(15.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = SoftText
            )
        }

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = null,
            tint = SoftText
        )
    }
}

@Composable
private fun UpcomingReminderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            SectionTitle(
                title = "Upcoming Reminders",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, SoftBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFF0EAFE), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Medication,
                        contentDescription = null,
                        tint = Color(0xFF7C3AED)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Take Vitamin D",
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )

                    Text(
                        text = "Today, 8:00 AM",
                        color = SoftText
                    )
                }

                Surface(
                    color = Color(0xFFF0EAFE),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "Due soon",
                        color = Color(0xFF7C3AED),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardBottomBar(navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BottomItem("Home", Icons.Outlined.Home, true) {
                navController.navigate("dashboard")
            }

            BottomItem("Documents", Icons.Outlined.Folder, false) {
                navController.navigate("document_library")
            }

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(MedGreen, CircleShape)
                    .clickable { navController.navigate("add_document") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(31.dp)
                )
            }

            BottomItem("Reminders", Icons.Outlined.Notifications, false) {
                navController.navigate("reminders")
            }

            BottomItem("Profile", Icons.Outlined.AccountCircle, false) {
                navController.navigate("edit_profile")
            }
        }
    }
}

@Composable
private fun BottomItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) MedGreen else SoftText,
                modifier = Modifier.size(23.dp)
            )

            Text(
                text = label,
                color = if (selected) MedGreen else SoftText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}