package com.example.medshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContactEmergency
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medshelf.viewmodel.UserViewModel

private val MedGreen = Color(0xFF009688)
private val DarkText = Color(0xFF111827)
private val SoftText = Color(0xFF64748B)
private val SoftBorder = Color(0xFFE2E8F0)
private val ErrorRed = Color(0xFFEF4444)

@Composable
fun EditProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val user = userViewModel.user.value

    var firstName by remember(user) { mutableStateOf(user?.firstName ?: "") }
    var lastName by remember(user) { mutableStateOf(user?.lastName ?: "") }
    var age by remember(user) { mutableStateOf(user?.age?.toString() ?: "") }
    var bloodType by remember(user) { mutableStateOf(user?.bloodType ?: "") }
    var allergies by remember(user) { mutableStateOf(user?.allergies ?: "") }
    var conditions by remember(user) { mutableStateOf(user?.conditions ?: "") }
    var medications by remember(user) { mutableStateOf(user?.medications ?: "") }
    var emergencyContactName by remember(user) { mutableStateOf(user?.emergencyContactName ?: "") }
    var emergencyContactNumber by remember(user) { mutableStateOf(user?.emergencyContactNumber ?: "") }

    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            MedShelfTopBar(
                title = "Edit Profile",
                navController = navController,
                showBackButton = true
            )
        },
        bottomBar = {
            MedShelfBottomBar(navController)
        },
        containerColor = Color.Transparent
    ) { paddingValues ->

        Column(
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
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            ProfileHeaderCard(
                name = "${firstName.ifBlank { "Your" }} ${lastName.ifBlank { "Profile" }}",
                subtitle = "Keep your emergency medical details updated."
            )

            ProfileSectionCard(
                title = "Basic Information",
                icon = Icons.Filled.Person
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileInputField(
                        label = "First Name",
                        value = firstName,
                        icon = Icons.Filled.Person,
                        placeholder = "First name",
                        modifier = Modifier.weight(1f),
                        onValueChange = {
                            firstName = it
                            errorMessage = ""
                        }
                    )

                    ProfileInputField(
                        label = "Last Name",
                        value = lastName,
                        icon = Icons.Filled.Badge,
                        placeholder = "Last name",
                        modifier = Modifier.weight(1f),
                        onValueChange = {
                            lastName = it
                            errorMessage = ""
                        }
                    )
                }

                ProfileInputField(
                    label = "Age",
                    value = age,
                    icon = Icons.Filled.Badge,
                    placeholder = "e.g., 21",
                    keyboardType = KeyboardType.Number,
                    onValueChange = {
                        age = it.filter { char -> char.isDigit() }
                        errorMessage = ""
                    }
                )

                ProfileInputField(
                    label = "Blood Type",
                    value = bloodType,
                    icon = Icons.Filled.Bloodtype,
                    placeholder = "e.g., O+, A-, AB+",
                    onValueChange = {
                        bloodType = it.uppercase()
                    }
                )
            }

            ProfileSectionCard(
                title = "Medical Information",
                icon = Icons.Filled.HealthAndSafety
            ) {
                ProfileInputField(
                    label = "Allergies",
                    value = allergies,
                    icon = Icons.Filled.Warning,
                    placeholder = "e.g., Penicillin, seafood, none",
                    singleLine = false,
                    minLines = 3,
                    onValueChange = {
                        allergies = it
                    }
                )

                ProfileInputField(
                    label = "Medical Conditions",
                    value = conditions,
                    icon = Icons.Filled.HealthAndSafety,
                    placeholder = "e.g., Asthma, hypertension, none",
                    singleLine = false,
                    minLines = 3,
                    onValueChange = {
                        conditions = it
                    }
                )

                ProfileInputField(
                    label = "Current Medications",
                    value = medications,
                    icon = Icons.Filled.Medication,
                    placeholder = "e.g., Maintenance medicines, none",
                    singleLine = false,
                    minLines = 3,
                    onValueChange = {
                        medications = it
                    }
                )
            }

            ProfileSectionCard(
                title = "Emergency Contact",
                icon = Icons.Filled.ContactEmergency
            ) {
                ProfileInputField(
                    label = "Contact Name",
                    value = emergencyContactName,
                    icon = Icons.Filled.ContactEmergency,
                    placeholder = "e.g., Parent / Guardian",
                    onValueChange = {
                        emergencyContactName = it
                    }
                )

                ProfileInputField(
                    label = "Contact Number",
                    value = emergencyContactNumber,
                    icon = Icons.Filled.Call,
                    placeholder = "e.g., 09XXXXXXXXX",
                    keyboardType = KeyboardType.Phone,
                    onValueChange = {
                        emergencyContactNumber = it
                    }
                )
            }

            if (errorMessage.isNotBlank()) {
                Text(
                    text = errorMessage,
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = {
                    when {
                        firstName.isBlank() -> {
                            errorMessage = "Please enter your first name."
                        }

                        lastName.isBlank() -> {
                            errorMessage = "Please enter your last name."
                        }

                        age.isBlank() || age.toIntOrNull() == null -> {
                            errorMessage = "Please enter a valid age."
                        }

                        else -> {
                            userViewModel.saveUser(
                                firstName = firstName.trim(),
                                lastName = lastName.trim(),
                                age = age.toIntOrNull() ?: 0,
                                bloodType = bloodType.trim(),
                                allergies = allergies.ifBlank { "None" },
                                conditions = conditions.ifBlank { "None" },
                                medications = medications.ifBlank { "None" },
                                emergencyContactName = emergencyContactName.ifBlank { "Not specified" },
                                emergencyContactNumber = emergencyContactNumber.ifBlank { "Not specified" }
                            )

                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MedGreen,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Save Changes",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    name: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = Color(0xFFE6F7F4)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = MedGreen,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = name.trim(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkText
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftText
                )
            }
        }
    }
}

@Composable
private fun ProfileSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MedGreen,
                    modifier = Modifier.size(23.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

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
private fun ProfileInputField(
    label: String,
    value: String,
    icon: ImageVector,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SoftText
            )
        },
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MedGreen,
            unfocusedBorderColor = SoftBorder,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = MedGreen
        )
    )
}