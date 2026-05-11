package com.example.medshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Wc
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medshelf.viewmodel.FamilyMemberViewModel

private val MedGreen = Color(0xFF009688)
private val DarkText = Color(0xFF111827)
private val SoftText = Color(0xFF64748B)
private val SoftBorder = Color(0xFFE2E8F0)
private val ErrorRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFamilyMemberProfilesScreen(
    navController: NavController,
    familyMemberViewModel: FamilyMemberViewModel
) {
    val familyMembers by familyMemberViewModel.familyMembers.collectAsState()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var conditions by remember { mutableStateOf("") }
    var medications by remember { mutableStateOf("") }
    var emergencyContactName by remember { mutableStateOf("") }
    var emergencyContactNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var showErrors by remember { mutableStateOf(false) }
    var bloodTypeExpanded by remember { mutableStateOf(false) }
    var sexExpanded by remember { mutableStateOf(false) }

    val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    val sexOptions = listOf("Male", "Female", "Prefer not to say")

    val firstNameError = showErrors && firstName.trim().isBlank()
    val lastNameError = showErrors && lastName.trim().isBlank()
    val relationshipError = showErrors && relationship.trim().isBlank()
    val ageError = showErrors && age.isNotBlank() && age.toIntOrNull() == null

    Scaffold(containerColor = Color.Transparent) { paddingValues ->
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 18.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TopBar(
                    title = "Family Profiles",
                    subtitle = "Add profiles to organize medical files",
                    onBack = {
                        navController.navigate("dashboard") {
                            popUpTo("dashboard") {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )

                InfoCard()

                ExistingProfilesCard(
                    count = familyMembers.size
                )

                FormSection(
                    title = "Basic Profile",
                    icon = Icons.Filled.Person
                ) {
                    MedInputField(
                        label = "First Name",
                        value = firstName,
                        icon = Icons.Outlined.Person,
                        isError = firstNameError,
                        errorText = "First name is required.",
                        onValueChange = {
                            firstName = it
                            showErrors = false
                        }
                    )

                    MedInputField(
                        label = "Last Name",
                        value = lastName,
                        icon = Icons.Outlined.Person,
                        isError = lastNameError,
                        errorText = "Last name is required.",
                        onValueChange = {
                            lastName = it
                            showErrors = false
                        }
                    )

                    MedInputField(
                        label = "Relationship / Label",
                        value = relationship,
                        icon = Icons.Outlined.Badge,
                        isError = relationshipError,
                        errorText = "Enter your own label, example: Mother, Younger Brother, Patient.",
                        onValueChange = {
                            relationship = it
                            showErrors = false
                        }
                    )

                    MedInputField(
                        label = "Age (Optional)",
                        value = age,
                        icon = Icons.Filled.CalendarMonth,
                        keyboardType = KeyboardType.Number,
                        isError = ageError,
                        errorText = "Enter numbers only.",
                        onValueChange = {
                            age = it.filter { char -> char.isDigit() }.take(3)
                            showErrors = false
                        }
                    )

                    ExposedDropdownMenuBox(
                        expanded = sexExpanded,
                        onExpandedChange = { sexExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = sex,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sex (Optional)") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Wc,
                                    contentDescription = null,
                                    tint = SoftText
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = sexExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(
                                    type = MenuAnchorType.PrimaryNotEditable,
                                    enabled = true
                                ),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = fieldColors()
                        )

                        ExposedDropdownMenu(
                            expanded = sexExpanded,
                            onDismissRequest = { sexExpanded = false }
                        ) {
                            sexOptions.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        sex = item
                                        sexExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                FormSection(
                    title = "Medical Details",
                    icon = Icons.Filled.HealthAndSafety
                ) {
                    ExposedDropdownMenuBox(
                        expanded = bloodTypeExpanded,
                        onExpandedChange = { bloodTypeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = bloodType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Blood Type (Optional)") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Bloodtype,
                                    contentDescription = null,
                                    tint = SoftText
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodTypeExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(
                                    type = MenuAnchorType.PrimaryNotEditable,
                                    enabled = true
                                ),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = fieldColors()
                        )

                        ExposedDropdownMenu(
                            expanded = bloodTypeExpanded,
                            onDismissRequest = { bloodTypeExpanded = false }
                        ) {
                            bloodTypes.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        bloodType = item
                                        bloodTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    MedInputField(
                        label = "Allergies (Optional)",
                        value = allergies,
                        icon = Icons.Filled.Warning,
                        onValueChange = { allergies = it }
                    )

                    MedInputField(
                        label = "Medical Conditions (Optional)",
                        value = conditions,
                        icon = Icons.Filled.MonitorHeart,
                        onValueChange = { conditions = it }
                    )

                    MedInputField(
                        label = "Medications (Optional)",
                        value = medications,
                        icon = Icons.Filled.Medication,
                        onValueChange = { medications = it }
                    )
                }

                FormSection(
                    title = "Emergency Contact",
                    icon = Icons.Filled.Call
                ) {
                    MedInputField(
                        label = "Contact Name (Optional)",
                        value = emergencyContactName,
                        icon = Icons.Outlined.Person,
                        onValueChange = { emergencyContactName = it }
                    )

                    MedInputField(
                        label = "Contact Number (Optional)",
                        value = emergencyContactNumber,
                        icon = Icons.Filled.Call,
                        keyboardType = KeyboardType.Phone,
                        onValueChange = {
                            emergencyContactNumber = it.filter { char -> char.isDigit() }.take(11)
                        }
                    )

                    MedInputField(
                        label = "Notes (Optional)",
                        value = notes,
                        icon = Icons.Outlined.Description,
                        singleLine = false,
                        onValueChange = { notes = it }
                    )
                }

                if (showErrors && (firstNameError || lastNameError || relationshipError || ageError)) {
                    ErrorBox()
                }

                Button(
                    onClick = {
                        showErrors = true

                        val valid =
                            firstName.trim().isNotBlank() &&
                                    lastName.trim().isNotBlank() &&
                                    relationship.trim().isNotBlank() &&
                                    (age.isBlank() || age.toIntOrNull() != null)

                        if (valid) {
                            familyMemberViewModel.addFamilyMember(
                                firstName = firstName,
                                lastName = lastName,
                                relationship = relationship,
                                age = age.toIntOrNull(),
                                sex = sex,
                                bloodType = bloodType,
                                allergies = allergies,
                                conditions = conditions,
                                medications = medications,
                                emergencyContactName = emergencyContactName,
                                emergencyContactNumber = emergencyContactNumber,
                                notes = notes
                            )

                            navController.navigate("dashboard") {
                                popUpTo("dashboard") {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedGreen)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Save Family Member",
                        fontWeight = FontWeight.Bold
                    )
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
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7F2EF))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFEAFBF7), CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
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
private fun InfoCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFEAFBF7),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6F5EF))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
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
                    text = "Organize records by profile",
                    color = MedGreen,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Use this to separate medical files for each family member.",
                    color = SoftText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun ExistingProfilesCard(count: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(Color(0xFFF8FAFC), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Groups,
                    contentDescription = null,
                    tint = MedGreen,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "$count family profile(s) saved",
                    color = DarkText,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Primary user can access all family member files.",
                    color = SoftText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun FormSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MedGreen,
                    modifier = Modifier.size(21.dp)
                )

                Spacer(modifier = Modifier.width(9.dp))

                Text(
                    text = title,
                    color = DarkText,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            content()
        }
    }
}

@Composable
private fun MedInputField(
    label: String,
    value: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorText: String = "",
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SoftText
                )
            },
            singleLine = singleLine,
            minLines = if (singleLine) 1 else 3,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = isError,
            shape = RoundedCornerShape(14.dp),
            colors = fieldColors()
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
            .background(Color(0xFFFFEEEE), RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFFFFCACA), RoundedCornerShape(14.dp))
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
            text = "Please complete the required fields before saving.",
            color = Color(0xFFB91C1C),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MedGreen,
    unfocusedBorderColor = SoftBorder,
    focusedLabelColor = MedGreen,
    cursorColor = MedGreen,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    errorBorderColor = ErrorRed
)