package com.example.medshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
private val ErrorRed = Color(0xFFEF4444)

data class FamilyMemberProfile(
    val name: String,
    val relationship: String,
    val bloodType: String,
    val allergies: String,
    val conditions: String,
    val emergencyContact: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFamilyMemberProfilesScreen(
    navController: NavController
) {
    var fullName by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf("Not set") }
    var allergies by remember { mutableStateOf("") }
    var conditions by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }

    var bloodTypeExpanded by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    val bloodTypes = listOf(
        "Not set", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
    )

    val familyMembers = remember {
        mutableStateListOf(
            FamilyMemberProfile(
                name = "Maria Santos",
                relationship = "Mother",
                bloodType = "O+",
                allergies = "Penicillin",
                conditions = "Hypertension",
                emergencyContact = "0912 345 6789"
            ),
            FamilyMemberProfile(
                name = "Juan Santos",
                relationship = "Father",
                bloodType = "A+",
                allergies = "None",
                conditions = "Diabetes",
                emergencyContact = "0998 765 4321"
            )
        )
    }

    Scaffold(
        containerColor = Color.Transparent
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
                contentPadding = PaddingValues(top = 18.dp, bottom = 30.dp)
            ) {
                item {
                    TopBar(
                        title = "Family Profiles",
                        subtitle = "Manage medical info for family members",
                        onBack = { navController.popBackStack() }
                    )
                }

                item {
                    InfoBanner()
                }

                item {
                    AddProfileForm(
                        fullName = fullName,
                        onFullNameChange = {
                            fullName = it
                            showError = false
                        },
                        relationship = relationship,
                        onRelationshipChange = {
                            relationship = it
                            showError = false
                        },
                        bloodType = bloodType,
                        bloodTypeExpanded = bloodTypeExpanded,
                        onBloodTypeExpandedChange = { bloodTypeExpanded = it },
                        bloodTypes = bloodTypes,
                        onBloodTypeSelected = {
                            bloodType = it
                            bloodTypeExpanded = false
                        },
                        allergies = allergies,
                        onAllergiesChange = { allergies = it },
                        conditions = conditions,
                        onConditionsChange = { conditions = it },
                        emergencyContact = emergencyContact,
                        onEmergencyContactChange = { emergencyContact = it },
                        showError = showError,
                        onAddClick = {
                            if (fullName.isBlank() || relationship.isBlank()) {
                                showError = true
                            } else {
                                familyMembers.add(
                                    FamilyMemberProfile(
                                        name = fullName.trim(),
                                        relationship = relationship.trim(),
                                        bloodType = bloodType,
                                        allergies = allergies.ifBlank { "Not set" },
                                        conditions = conditions.ifBlank { "Not set" },
                                        emergencyContact = emergencyContact.ifBlank { "Not set" }
                                    )
                                )

                                fullName = ""
                                relationship = ""
                                bloodType = "Not set"
                                allergies = ""
                                conditions = ""
                                emergencyContact = ""
                                showError = false
                            }
                        }
                    )
                }

                item {
                    Text(
                        text = "Saved Family Members",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                }

                if (familyMembers.isEmpty()) {
                    item {
                        EmptyProfilesCard()
                    }
                } else {
                    items(familyMembers) { member ->
                        FamilyMemberCard(
                            member = member,
                            onDelete = {
                                familyMembers.remove(member)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    title: String,
    subtitle: String,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.95f),
        shadowElevation = 3.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE6F2EF))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFE6F7F4), CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MedGreen
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = DarkText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = subtitle,
                    color = SoftText,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFEAFBF7), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Groups,
                    contentDescription = null,
                    tint = MedGreen
                )
            }
        }
    }
}

@Composable
private fun InfoBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAFBF7)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6F5EF))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.HealthAndSafety,
                    contentDescription = null,
                    tint = MedGreen
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "For caregivers and dependents",
                    color = MedGreen,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Add profiles for parents, children, or family members whose medical records you manage.",
                    color = SoftText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddProfileForm(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    relationship: String,
    onRelationshipChange: (String) -> Unit,
    bloodType: String,
    bloodTypeExpanded: Boolean,
    onBloodTypeExpandedChange: (Boolean) -> Unit,
    bloodTypes: List<String>,
    onBloodTypeSelected: (String) -> Unit,
    allergies: String,
    onAllergiesChange: (String) -> Unit,
    conditions: String,
    onConditionsChange: (String) -> Unit,
    emergencyContact: String,
    onEmergencyContactChange: (String) -> Unit,
    showError: Boolean,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Add Family Member",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            MedTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = "Full Name",
                icon = Icons.Outlined.Person
            )

            MedTextField(
                value = relationship,
                onValueChange = onRelationshipChange,
                label = "Relationship",
                icon = Icons.Outlined.Groups
            )

            ExposedDropdownMenuBox(
                expanded = bloodTypeExpanded,
                onExpandedChange = onBloodTypeExpandedChange
            ) {
                OutlinedTextField(
                    value = bloodType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Blood Type") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Bloodtype,
                            contentDescription = null,
                            tint = MedGreen
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodTypeExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedGreen,
                        unfocusedBorderColor = SoftBorder,
                        focusedLabelColor = MedGreen,
                        cursorColor = MedGreen
                    )
                )

                ExposedDropdownMenu(
                    expanded = bloodTypeExpanded,
                    onDismissRequest = { onBloodTypeExpandedChange(false) }
                ) {
                    bloodTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = { onBloodTypeSelected(type) }
                        )
                    }
                }
            }

            MedTextField(
                value = allergies,
                onValueChange = onAllergiesChange,
                label = "Allergies",
                icon = Icons.Outlined.Warning
            )

            MedTextField(
                value = conditions,
                onValueChange = onConditionsChange,
                label = "Medical Conditions",
                icon = Icons.Outlined.MonitorHeart
            )

            MedTextField(
                value = emergencyContact,
                onValueChange = onEmergencyContactChange,
                label = "Emergency Contact",
                icon = Icons.Outlined.Call
            )

            if (showError) {
                Text(
                    text = "Full name and relationship are required.",
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = onAddClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MedGreen)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Add Profile",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MedGreen
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MedGreen,
            unfocusedBorderColor = SoftBorder,
            focusedLabelColor = MedGreen,
            cursorColor = MedGreen
        )
    )
}

@Composable
private fun FamilyMemberCard(
    member: FamilyMemberProfile,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFE6F7F4), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MedGreen
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = member.name,
                        color = DarkText,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${member.relationship} • ${member.bloodType}",
                        color = SoftText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = ErrorRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoRow(
                icon = Icons.Outlined.Warning,
                label = "Allergies",
                value = member.allergies,
                iconColor = Orange
            )

            ProfileInfoRow(
                icon = Icons.Outlined.MonitorHeart,
                label = "Conditions",
                value = member.conditions,
                iconColor = Purple
            )

            ProfileInfoRow(
                icon = Icons.Outlined.Call,
                label = "Emergency Contact",
                value = member.emergencyContact,
                iconColor = MedGreen
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(iconColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = label,
                color = SoftText,
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                text = value,
                color = DarkText,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EmptyProfilesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.Groups,
                contentDescription = null,
                tint = SoftText,
                modifier = Modifier.size(44.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "No family profiles yet",
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Text(
                text = "Add a profile to manage medical records for a family member.",
                color = SoftText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}