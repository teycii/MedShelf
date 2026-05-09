package com.example.medshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContactEmergency
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    LaunchedEffect(Unit) {
        userViewModel.loadUser()
    }

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
    var showErrors by remember { mutableStateOf(false) }

    val firstNameError = showErrors && !isValidProfileName(firstName)
    val lastNameError = showErrors && !isValidProfileName(lastName)
    val ageError = showErrors && !isValidProfileAge(age)
    val bloodTypeError = showErrors && bloodType.isBlank()
    val emergencyNameError = showErrors && !isValidProfileName(emergencyContactName)
    val emergencyNumberError = showErrors && !isValidProfilePhone(emergencyContactNumber)

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
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProfileInputField(
                        label = "First Name",
                        value = firstName,
                        icon = Icons.Outlined.Person,
                        placeholder = "First name",
                        modifier = Modifier.weight(1f),
                        isError = firstNameError,
                        errorText = "Use letters only.",
                        onValueChange = { firstName = it }
                    )

                    ProfileInputField(
                        label = "Last Name",
                        value = lastName,
                        icon = Icons.Filled.Badge,
                        placeholder = "Last name",
                        modifier = Modifier.weight(1f),
                        isError = lastNameError,
                        errorText = "Use letters only.",
                        onValueChange = { lastName = it }
                    )
                }

                ProfileInputField(
                    label = "Age",
                    value = age,
                    icon = Icons.Filled.CalendarMonth,
                    placeholder = "e.g., 21",
                    keyboardType = KeyboardType.Number,
                    isError = ageError,
                    errorText = "Enter a valid age from 1 to 120.",
                    onValueChange = {
                        age = it.filter { char -> char.isDigit() }.take(3)
                    }
                )

                ProfileBloodTypeDropdown(
                    value = bloodType,
                    isError = bloodTypeError,
                    onChange = { bloodType = it }
                )
            }

            ProfileSectionCard(
                title = "Medical Information",
                icon = Icons.Filled.HealthAndSafety
            ) {
                ProfileInputField(
                    label = "Allergies",
                    value = allergies,
                    icon = Icons.Filled.Sick,
                    placeholder = "e.g., Penicillin, seafood, none",
                    singleLine = false,
                    minLines = 3,
                    onValueChange = { allergies = it }
                )

                ProfileInputField(
                    label = "Medical Conditions",
                    value = conditions,
                    icon = Icons.Filled.LocalHospital,
                    placeholder = "e.g., Asthma, hypertension, none",
                    singleLine = false,
                    minLines = 3,
                    onValueChange = { conditions = it }
                )

                ProfileInputField(
                    label = "Current Medications",
                    value = medications,
                    icon = Icons.Filled.Medication,
                    placeholder = "e.g., Maintenance medicines, none",
                    singleLine = false,
                    minLines = 3,
                    onValueChange = { medications = it }
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
                    isError = emergencyNameError,
                    errorText = "Use letters only.",
                    onValueChange = { emergencyContactName = it }
                )

                ProfileInputField(
                    label = "Contact Number",
                    value = emergencyContactNumber,
                    icon = Icons.Filled.Call,
                    placeholder = "09 followed by 9 digits",
                    keyboardType = KeyboardType.Phone,
                    isError = emergencyNumberError,
                    errorText = "Use PH mobile format: 09 followed by 9 digits.",
                    onValueChange = {
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
                ProfileErrorBox()
            }

            Button(
                onClick = {
                    showErrors = true

                    val isFormValid =
                        isValidProfileName(firstName) &&
                                isValidProfileName(lastName) &&
                                isValidProfileAge(age) &&
                                bloodType.isNotBlank() &&
                                isValidProfileName(emergencyContactName) &&
                                isValidProfilePhone(emergencyContactNumber)

                    if (isFormValid) {
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

                        navController.popBackStack()
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

private fun isValidProfileName(value: String): Boolean {
    return value.trim().matches(Regex("^[A-Za-z ]+$"))
}

private fun isValidProfileAge(value: String): Boolean {
    return value.toIntOrNull()?.let { it in 1..120 } ?: false
}

private fun isValidProfilePhone(value: String): Boolean {
    return value.trim().matches(Regex("^09\\d{9}$"))
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
                Box(contentAlignment = Alignment.Center) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileBloodTypeDropdown(
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
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    ),
                label = { Text("Blood Type") },
                placeholder = { Text("Select blood type") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Bloodtype,
                        contentDescription = null,
                        tint = SoftText
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                singleLine = true,
                isError = isError,
                shape = RoundedCornerShape(16.dp),
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
                onDismissRequest = { expanded = false }
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
private fun ProfileInputField(
    label: String,
    value: String,
    icon: ImageVector,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorText: String = "",
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
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
            isError = isError,
            shape = RoundedCornerShape(16.dp),
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
private fun ProfileErrorBox() {
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
            text = "Please correct the highlighted fields before saving.",
            color = Color(0xFFB91C1C),
            fontWeight = FontWeight.Medium
        )
    }
}