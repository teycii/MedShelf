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
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
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

    val nameRegex = Regex("^[A-Za-z ]+$")
    val phoneRegex = Regex("^09\\d{9}$")

    val validFirstName by remember {
        derivedStateOf { firstName.trim().matches(nameRegex) }
    }

    val validLastName by remember {
        derivedStateOf { lastName.trim().matches(nameRegex) }
    }

    val validAge by remember {
        derivedStateOf {
            age.toIntOrNull()?.let { it in 1..120 } ?: false
        }
    }

    val validEmergencyName by remember {
        derivedStateOf { emergencyContactName.trim().matches(nameRegex) }
    }

    val validEmergencyNumber by remember {
        derivedStateOf { emergencyContactNumber.trim().matches(phoneRegex) }
    }

    val firstNameError = showErrors && !validFirstName
    val lastNameError = showErrors && !validLastName
    val ageError = showErrors && !validAge
    val bloodTypeError = showErrors && bloodType.isBlank()
    val emergencyNameError = showErrors && !validEmergencyName
    val emergencyNumberError = showErrors && !validEmergencyNumber

    val formIsValid =
        validFirstName &&
                validLastName &&
                validAge &&
                bloodType.isNotBlank() &&
                validEmergencyName &&
                validEmergencyNumber

    Scaffold(
        containerColor = Color.Transparent
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
                    .padding(top = 28.dp, bottom = 40.dp),
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
                        errorText = "Use letters only.",
                        onChange = { firstName = it }
                    )

                    ModernInputField(
                        label = "Last Name",
                        value = lastName,
                        icon = Icons.Outlined.Person,
                        isError = lastNameError,
                        errorText = "Use letters only.",
                        onChange = { lastName = it }
                    )

                    ModernInputField(
                        label = "Age",
                        value = age,
                        icon = Icons.Filled.CalendarMonth,
                        keyboardType = KeyboardType.Number,
                        isError = ageError,
                        errorText = "Enter a valid age from 1 to 120.",
                        onChange = { age = it.filter { char -> char.isDigit() } }
                    )

                    BloodTypeDropdown(
                        value = bloodType,
                        isError = bloodTypeError,
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
                        errorText = "Use letters only.",
                        onChange = { emergencyContactName = it }
                    )

                    ModernInputField(
                        label = "Contact Number",
                        value = emergencyContactNumber,
                        icon = Icons.Filled.Phone,
                        keyboardType = KeyboardType.Phone,
                        isError = emergencyNumberError,
                        errorText = "Use PH mobile format: 09 followed by 9 digits.",
                        onChange = {
                            emergencyContactNumber = it
                                .filter { char -> char.isDigit() }
                                .take(11)
                        }
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

                        if (formIsValid) {
                            userViewModel.saveUser(
                                firstName = firstName.trim(),
                                lastName = lastName.trim(),
                                age = age.toInt(),
                                bloodType = bloodType.trim(),
                                allergies = allergies.ifBlank { "None" },
                                conditions = conditions.ifBlank { "None" },
                                medications = medications.ifBlank { "None" },
                                emergencyContactName = emergencyContactName.trim(),
                                emergencyContactNumber = emergencyContactNumber.trim()
                            )

                            navController.navigate("dashboard") {
                                popUpTo("registration") {
                                    inclusive = true
                                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BloodTypeDropdown(
    value: String,
    isError: Boolean,
    onChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val bloodTypes = listOf(
        "A+",
        "A-",
        "B+",
        "B-",
        "AB+",
        "AB-",
        "O+",
        "O-"
    )

    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    ),
                placeholder = {
                    Text(
                        text = "Blood Type",
                        color = SoftText
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Bloodtype,
                        contentDescription = null,
                        tint = SoftText,
                        modifier = Modifier.size(22.dp)
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
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

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                bloodTypes.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            onChange(item)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (isError) {
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Please select a blood type.",
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun ModernInputField(
    label: String,
    value: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
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
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
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
            text = "Please correct the highlighted fields before continuing.",
            color = Color(0xFFB91C1C),
            fontWeight = FontWeight.Medium
        )
    }
}