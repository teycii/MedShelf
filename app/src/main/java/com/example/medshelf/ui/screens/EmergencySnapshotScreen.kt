package com.example.medshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medshelf.ui.theme.Navy
import com.example.medshelf.viewmodel.UserViewModel

private val EmergencyRed = Color(0xFFEF4444)
private val EmergencyGreen = Color(0xFF009688)
private val EmergencyYellow = Color(0xFFF59E0B)
private val EmergencyPurple = Color(0xFF7C3AED)
private val CardWhite = Color.White.copy(alpha = 0.10f)
private val BorderWhite = Color.White.copy(alpha = 0.14f)

@Composable
fun EmergencySnapshotScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val user by userViewModel.user

    EmergencySnapshotContent(
        name = user?.let { "${it.firstName} ${it.lastName}" } ?: "Not set",
        age = user?.age?.toString() ?: "Not set",
        bloodType = user?.bloodType ?: "Not set",
        allergies = user?.allergies ?: "Not set",
        conditions = user?.conditions ?: "Not set",
        medications = user?.medications ?: "Not set",
        emergencyContact = user?.let {
            "${it.emergencyContactName} (${it.emergencyContactNumber})"
        } ?: "Not set",
        onClose = { navController.popBackStack() }
    )
}

@Composable
fun EmergencySnapshotContent(
    name: String,
    age: String,
    bloodType: String,
    allergies: String,
    conditions: String,
    medications: String,
    emergencyContact: String,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF071827),
                        Navy,
                        Color(0xFF0F2D3A)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(22.dp))

            EmergencyTopSection(onClose = onClose)

            Spacer(modifier = Modifier.height(24.dp))

            EmergencyIdentityCard(
                name = name,
                age = age,
                bloodType = bloodType
            )

            Spacer(modifier = Modifier.height(16.dp))

            EmergencyPriorityCard(
                allergies = allergies,
                conditions = conditions,
                medications = medications,
                emergencyContact = emergencyContact
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Read-only emergency information. Do not edit from this screen.",
                color = Color.White.copy(alpha = 0.58f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 28.dp)
            )
        }
    }
}

@Composable
private fun EmergencyTopSection(
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(18.dp),
            color = Color.White.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderWhite)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Emergency Snapshot",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Critical medical information",
                color = Color.White.copy(alpha = 0.68f),
                style = MaterialTheme.typography.bodySmall
            )
        }

        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun EmergencyIdentityCard(
    name: String,
    age: String,
    bloodType: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.12f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderWhite)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(58.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.14f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name.ifBlank { "Not set" },
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Age: ${age.ifBlank { "Not set" }}",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = EmergencyRed.copy(alpha = 0.18f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    EmergencyRed.copy(alpha = 0.35f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bloodtype,
                        contentDescription = null,
                        tint = Color(0xFFFFB4B4),
                        modifier = Modifier.size(30.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Blood Type",
                            color = Color.White.copy(alpha = 0.65f),
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = bloodType.ifBlank { "Not set" },
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmergencyPriorityCard(
    allergies: String,
    conditions: String,
    medications: String,
    emergencyContact: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Critical Details",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(14.dp))

            EmergencyInfoRow(
                icon = Icons.Default.Warning,
                label = "Allergies",
                value = allergies,
                color = EmergencyYellow
            )

            EmergencyDivider()

            EmergencyInfoRow(
                icon = Icons.Default.LocalHospital,
                label = "Medical Conditions",
                value = conditions,
                color = EmergencyGreen
            )

            EmergencyDivider()

            EmergencyInfoRow(
                icon = Icons.Default.Medication,
                label = "Current Medications",
                value = medications,
                color = EmergencyPurple
            )

            EmergencyDivider()

            EmergencyInfoRow(
                icon = Icons.Default.Call,
                label = "Emergency Contact",
                value = emergencyContact,
                color = EmergencyRed
            )
        }
    }
}

@Composable
private fun EmergencyInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(15.dp),
            color = color.copy(alpha = 0.16f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.58f),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value.ifBlank { "Not set" },
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EmergencyDivider() {
    HorizontalDivider(
        color = Color.White.copy(alpha = 0.10f),
        thickness = 1.dp
    )
}