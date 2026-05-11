package com.example.medshelf.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Notifications
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
import com.example.medshelf.model.FamilyMemberEntity
import com.example.medshelf.viewmodel.FamilyMemberViewModel

private val MedGreen = Color(0xFF009688)
private val DarkText = Color(0xFF111827)
private val SoftText = Color(0xFF64748B)
private val SoftBorder = Color(0xFFE2E8F0)
private val ErrorRed = Color(0xFFEF4444)
private val Purple = Color(0xFF7C3AED)
private val Orange = Color(0xFFF59E0B)

@Composable
fun FamilyMemberDetailsScreen(
    navController: NavController,
    familyMemberViewModel: FamilyMemberViewModel,
    familyMemberId: Int
) {
    val familyMembers by familyMemberViewModel.familyMembers.collectAsState()
    val member = familyMembers.find { it.id == familyMemberId }

    Scaffold(
        topBar = {
            MedShelfTopBar(
                title = "Family Profile",
                navController = navController,
                showBackButton = true
            )
        },
        bottomBar = {
            MedShelfBottomBar(navController)
        },
        containerColor = Color.Transparent
    ) { paddingValues ->

        if (member == null) {
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
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Family member not found.",
                    color = SoftText
                )
            }
        } else {
            FamilyMemberDetailsContent(
                navController = navController,
                paddingValues = paddingValues,
                member = member,
                onDelete = {
                    familyMemberViewModel.deleteFamilyMember(member)

                    navController.navigate("dashboard") {
                        popUpTo("dashboard") {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
private fun FamilyMemberDetailsContent(
    navController: NavController,
    paddingValues: PaddingValues,
    member: FamilyMemberEntity,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val fullName = "${member.firstName} ${member.lastName}".trim()
        .ifBlank { "Unnamed Profile" }

    val ownerRoute = Uri.encode(fullName)

    Column(
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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ProfileHeroCard(
            name = fullName,
            relationship = member.relationship,
            age = member.age,
            sex = member.sex,
            bloodType = member.bloodType
        )

        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionCard(
                title = "Documents",
                subtitle = "View files",
                icon = Icons.Outlined.Folder,
                bgColor = Color(0xFFF2FFFC),
                iconColor = MedGreen,
                borderColor = MedGreen.copy(alpha = 0.22f),
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("document_library/$ownerRoute")
                }
            )

            ActionCard(
                title = "Edit",
                subtitle = "Update info",
                icon = Icons.Filled.Edit,
                bgColor = Color(0xFFEAFBF7),
                iconColor = MedGreen,
                borderColor = MedGreen.copy(alpha = 0.18f),
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("edit_family_member/${member.id}")
                }
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionCard(
                title = "Add File",
                subtitle = "Upload record",
                icon = Icons.Filled.Add,
                bgColor = Color(0xFFE9FBEF),
                iconColor = Color(0xFF16A34A),
                borderColor = Color(0xFF16A34A).copy(alpha = 0.18f),
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("add_document")
                }
            )

            ActionCard(
                title = "Reminders",
                subtitle = "Med schedule",
                icon = Icons.Outlined.Notifications,
                bgColor = Color(0xFFFBF8FF),
                iconColor = Purple,
                borderColor = Purple.copy(alpha = 0.22f),
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("reminders")
                }
            )
        }

        DetailsSection(
            title = "Basic Information",
            icon = Icons.Filled.Person
        ) {
            DetailRow(Icons.Filled.Badge, "Relationship / Label", member.relationship.ifBlank { "Not set" })
            DetailRow(Icons.Filled.CalendarMonth, "Age", member.age?.toString() ?: "Not set")
            DetailRow(Icons.Filled.Wc, "Sex", member.sex.ifBlank { "Not set" })
            DetailRow(Icons.Filled.Bloodtype, "Blood Type", member.bloodType.ifBlank { "Not set" })
        }

        DetailsSection(
            title = "Medical Details",
            icon = Icons.Filled.HealthAndSafety
        ) {
            DetailRow(Icons.Filled.Warning, "Allergies", member.allergies.ifBlank { "None" })
            DetailRow(Icons.Filled.MonitorHeart, "Medical Conditions", member.conditions.ifBlank { "None" })
            DetailRow(Icons.Filled.Medication, "Medications", member.medications.ifBlank { "None" })
        }

        DetailsSection(
            title = "Emergency Contact",
            icon = Icons.Filled.Call
        ) {
            DetailRow(Icons.Filled.Person, "Contact Name", member.emergencyContactName.ifBlank { "Not set" })
            DetailRow(Icons.Filled.Phone, "Contact Number", member.emergencyContactNumber.ifBlank { "Not set" })
        }

        DetailsSection(
            title = "Important Notes",
            icon = Icons.Filled.NoteAlt
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    tint = Orange,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = member.notes.ifBlank { "No important notes added." },
                    color = SoftText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Button(
            onClick = {
                navController.navigate("edit_family_member/${member.id}")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MedGreen)
        ) {
            Icon(Icons.Filled.Edit, contentDescription = null)

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Edit Family Profile",
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedButton(
            onClick = {
                showDeleteDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = ErrorRed
            )
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null)

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Delete Family Profile",
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text(
                    text = "Delete Profile",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Are you sure you want to delete $fullName?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProfileHeroCard(
    name: String,
    relationship: String,
    age: Int?,
    sex: String,
    bloodType: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7F2EF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White,
                            Color(0xFFF8FFFC)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .background(Color(0xFFEAFBF7), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = MedGreen,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        color = DarkText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = relationship.ifBlank { "Family member" },
                        color = SoftText,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = Color(0xFFEAFBF7),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = "Blood Type: ${bloodType.ifBlank { "Not set" }}",
                            color = MedGreen,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HeroInfoChip(
                    label = "Age",
                    value = age?.toString() ?: "Not set",
                    icon = Icons.Filled.CalendarMonth,
                    modifier = Modifier.weight(1f)
                )

                HeroInfoChip(
                    label = "Sex",
                    value = sex.ifBlank { "Not set" },
                    icon = Icons.Filled.Wc,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HeroInfoChip(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MedGreen,
                modifier = Modifier.size(19.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = label,
                    color = SoftText,
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = value,
                    color = DarkText,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    bgColor: Color,
    iconColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(118.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
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
                    .size(42.dp)
                    .background(Color.White.copy(alpha = 0.75f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
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
private fun DetailsSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFFEAFBF7), RoundedCornerShape(13.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MedGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    color = DarkText,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            content()
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(Color(0xFFF2FFFC), RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MedGreen,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = SoftText,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = value.ifBlank { "Not set" },
                color = DarkText,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}