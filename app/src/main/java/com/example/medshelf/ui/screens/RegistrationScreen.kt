package com.example.medshelf.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sick
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
private val ErrorRed = Color(0xFFEF4444)

@Composable
fun RegistrationScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var conditions by remember { mutableStateOf("") }
    var medications by remember { mutableStateOf("") }
    var emergencyContactName by remember { mutableStateOf("") }
    var emergencyContactNumber by remember { mutableStateOf("") }

    var showErrors by remember { mutableStateOf(false) }

    val firstNameError by remember { derivedStateOf { showErrors && firstName.isBlank() } }
    val lastNameError by remember { derivedStateOf { showErrors && lastName.isBlank() } }
    val ageError by remember { derivedStateOf { showErrors && age.isBlank() } }
    val bloodTypeError by remember { derivedStateOf { showErrors && bloodType.isBlank() } }
    val emergencyNameError by remember { derivedStateOf { showErrors && emergencyContactName.isBlank() } }
    val emergencyNumberError by remember { derivedStateOf { showErrors && emergencyContactNumber.isBlank() } }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            RegistrationBottomBar(navController)
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color.White,
                            Color(0xFFEFFFF8)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 28.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                BrandHeader()

                HeroHeader()

                RegistrationSection(
                    title = "Personal Info",
                    icon = Icons.Filled.Person
                ) {
                    ModernInputField(
                        label = "First Name",
                        value = firstName,
                        icon = Icons.Outlined.Person,
                        isError = firstNameError,
                        errorText = "This field is required",
                        onChange = { firstName = it }
                    )

                    ModernInputField(
                        label = "Last Name",
                        value = lastName,
                        icon = Icons.Outlined.Person,
                        isError = lastNameError,
                        errorText = "This field is required",
                        onChange = { lastName = it }
                    )

                    ModernInputField(
                        label = "Age",
                        value = age,
                        icon = Icons.Filled.CalendarMonth,
                        isError = ageError,
                        errorText = "This field is required",
                        onChange = { age = it }
                    )

                    ModernInputField(
                        label = "Blood Type",
                        value = bloodType,
                        icon = Icons.Filled.Bloodtype,
                        isError = bloodTypeError,
                        errorText = "This field is required",
                        onChange = { bloodType = it }
                    )
                }

                RegistrationSection(
                    title = "Medical Info",
                    icon = Icons.Filled.HealthAndSafety
                ) {
                    ModernInputField(
                        label = "Allergies",
                        value = allergies,
                        icon = Icons.Filled.Sick,
                        onChange = { allergies = it }
                    )

                    ModernInputField(
                        label = "Medical Conditions",
                        value = conditions,
                        icon = Icons.Filled.LocalHospital,
                        onChange = { conditions = it }
                    )

                    ModernInputField(
                        label = "Medications",
                        value = medications,
                        icon = Icons.Filled.Medication,
                        onChange = { medications = it }
                    )
                }

                RegistrationSection(
                    title = "Emergency Contact",
                    icon = Icons.Filled.Phone
                ) {
                    ModernInputField(
                        label = "Contact Name",
                        value = emergencyContactName,
                        icon = Icons.Outlined.Person,
                        isError = emergencyNameError,
                        errorText = "This field is required",
                        onChange = { emergencyContactName = it }
                    )

                    ModernInputField(
                        label = "Contact Number",
                        value = emergencyContactNumber,
                        icon = Icons.Filled.Phone,
                        isError = emergencyNumberError,
                        errorText = "This field is required",
                        onChange = { emergencyContactNumber = it }
                    )
                }

                if (
                    firstNameError ||
                    lastNameError ||
                    ageError ||
                    bloodTypeError ||
                    emergencyNameError ||
                    emergencyNumberError
                ) {
                    ErrorBox()
                }

                Button(
                    onClick = {
                        showErrors = true

                        if (
                            firstName.isNotBlank() &&
                            lastName.isNotBlank() &&
                            age.isNotBlank() &&
                            bloodType.isNotBlank() &&
                            emergencyContactName.isNotBlank() &&
                            emergencyContactNumber.isNotBlank()
                        ) {
                            userViewModel.saveUser(
                                firstName = firstName,
                                lastName = lastName,
                                age = age.toIntOrNull() ?: 0,
                                bloodType = bloodType,
                                allergies = allergies.ifBlank { "None" },
                                conditions = conditions.ifBlank { "None" },
                                medications = medications.ifBlank { "None" },
                                emergencyContactName = emergencyContactName,
                                emergencyContactNumber = emergencyContactNumber
                            )

                            navController.navigate("dashboard") {
                                popUpTo("registration") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedGreen
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.HealthAndSafety,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Save Profile",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun BrandHeader() {
    Row(
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
    }
}

@Composable
private fun HeroHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Set Up Your\nMedical Profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = DarkText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Create a profile for yourself or a family member.",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftText
            )
        }

        Box(
            modifier = Modifier
                .size(92.dp)
                .padding(end = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .offset(x = (-12).dp, y = 8.dp)
                    .background(Color(0xFFE8F7F3), CircleShape)
            )

            Card(
                modifier = Modifier
                    .width(48.dp)
                    .height(64.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = SoftBorder
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(5.dp)
                            .background(Color(0xFF334155), RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = MedGreen,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(4.dp)
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .offset(x = 31.dp, y = 22.dp)
                    .background(MedGreen, RoundedCornerShape(11.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}

@Composable
private fun RegistrationSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = SoftBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MedGreen,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(9.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F4F4A)
                )
            }

            content()
        }
    }
}

@Composable
private fun ModernInputField(
    label: String,
    value: String,
    icon: ImageVector,
    isError: Boolean = false,
    errorText: String = "",
    onChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            placeholder = {
                Text(
                    text = label,
                    color = SoftText
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SoftText,
                    modifier = Modifier.size(22.dp)
                )
            },
            singleLine = true,
            isError = isError,
            shape = RoundedCornerShape(13.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) ErrorRed else MedGreen,
                unfocusedBorderColor = if (isError) ErrorRed else SoftBorder,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorBorderColor = ErrorRed,
                cursorColor = MedGreen
            )
        )

        if (isError) {
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = errorText,
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun ErrorBox() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFFFEEEE),
                shape = RoundedCornerShape(14.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFFFFCACA),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = ErrorRed
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "Please fill in all required fields.",
            color = Color(0xFFB91C1C),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RegistrationBottomBar(navController: NavController) {
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
                navController.navigate("library")
            }

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(MedGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(31.dp)
                )
            }

            BottomItem("Reminders", Icons.Outlined.Notifications, false) {}

            BottomItem("Profile", Icons.Outlined.AccountCircle, false) {}
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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