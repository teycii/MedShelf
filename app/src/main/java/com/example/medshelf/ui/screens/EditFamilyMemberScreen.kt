package com.example.medshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.medshelf.model.FamilyMemberEntity
import com.example.medshelf.viewmodel.FamilyMemberViewModel

private val MedGreen = Color(0xFF009688)
private val DarkText = Color(0xFF111827)
private val SoftText = Color(0xFF64748B)
private val SoftBorder = Color(0xFFE2E8F0)
private val ErrorRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFamilyMemberScreen(
    navController: NavController,
    familyMemberViewModel: FamilyMemberViewModel,
    familyMemberId: Int
) {
    val familyMembers by familyMemberViewModel.familyMembers.collectAsState()
    val member = familyMembers.find { it.id == familyMemberId }

    Scaffold(
        topBar = {
            MedShelfTopBar(
                title = "Edit Family Profile",
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
            EditFamilyMemberContent(
                navController = navController,
                paddingValues = paddingValues,
                member = member,
                onSave = { updatedMember ->
                    familyMemberViewModel.updateFamilyMember(updatedMember)

                    navController.navigate("family_member_details/${updatedMember.id}") {
                        popUpTo("edit_family_member/${updatedMember.id}") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditFamilyMemberContent(
    navController: NavController,
    paddingValues: PaddingValues,
    member: FamilyMemberEntity,
    onSave: (FamilyMemberEntity) -> Unit
) {
    var firstName by remember(member.id) { mutableStateOf(member.firstName) }
    var lastName by remember(member.id) { mutableStateOf(member.lastName) }
    var relationship by remember(member.id) { mutableStateOf(member.relationship) }
    var age by remember(member.id) { mutableStateOf(member.age?.toString() ?: "") }
    var sex by remember(member.id) { mutableStateOf(member.sex) }
    var bloodType by remember(member.id) { mutableStateOf(member.bloodType) }
    var allergies by remember(member.id) { mutableStateOf(member.allergies) }
    var conditions by remember(member.id) { mutableStateOf(member.conditions) }
    var medications by remember(member.id) { mutableStateOf(member.medications) }
    var emergencyContactName by remember(member.id) { mutableStateOf(member.emergencyContactName) }
    var emergencyContactNumber by remember(member.id) { mutableStateOf(member.emergencyContactNumber) }
    var notes by remember(member.id) { mutableStateOf(member.notes) }

    var showErrors by remember { mutableStateOf(false) }
    var sexExpanded by remember { mutableStateOf(false) }
    var bloodTypeExpanded by remember { mutableStateOf(false) }

    val fullName = "$firstName $lastName".trim().ifBlank { "Family Profile" }

    val firstNameError = showErrors && firstName.trim().isBlank()
    val lastNameError = showErrors && lastName.trim().isBlank()
    val relationshipError = showErrors && relationship.trim().isBlank()
    val ageError = showErrors && age.isNotBlank() && age.toIntOrNull() == null

    val sexOptions = listOf("Male", "Female", "Prefer not to say")
    val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

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
        EditHeroCard(
            name = fullName,
            relationship = relationship,
            bloodType = bloodType
        )

        EditSection(
            title = "Basic Information",
            icon = Icons.Filled.Person
        ) {
            EditInputField(
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

            EditInputField(
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

            EditInputField(
                label = "Relationship / Label",
                value = relationship,
                icon = Icons.Outlined.Badge,
                isError = relationshipError,
                errorText = "Relationship or label is required.",
                onValueChange = {
                    relationship = it
                    showErrors = false
                }
            )

            EditInputField(
                label = "Age",
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
                onExpandedChange = {
                    sexExpanded = !sexExpanded
                }
            ) {
                OutlinedTextField(
                    value = sex,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sex") },
                    leadingIcon = {
                        FieldIcon(Icons.Outlined.Wc)
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
                    shape = RoundedCornerShape(14.dp),
                    colors = editFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = sexExpanded,
                    onDismissRequest = {
                        sexExpanded = false
                    }
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

        EditSection(
            title = "Medical Information",
            icon = Icons.Filled.HealthAndSafety
        ) {
            ExposedDropdownMenuBox(
                expanded = bloodTypeExpanded,
                onExpandedChange = {
                    bloodTypeExpanded = !bloodTypeExpanded
                }
            ) {
                OutlinedTextField(
                    value = bloodType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Blood Type") },
                    leadingIcon = {
                        FieldIcon(Icons.Filled.Bloodtype)
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
                    shape = RoundedCornerShape(14.dp),
                    colors = editFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = bloodTypeExpanded,
                    onDismissRequest = {
                        bloodTypeExpanded = false
                    }
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

            EditInputField(
                label = "Allergies",
                value = allergies,
                icon = Icons.Filled.Warning,
                onValueChange = { allergies = it }
            )

            EditInputField(
                label = "Medical Conditions",
                value = conditions,
                icon = Icons.Filled.MonitorHeart,
                onValueChange = { conditions = it }
            )

            EditInputField(
                label = "Medications",
                value = medications,
                icon = Icons.Filled.Medication,
                onValueChange = { medications = it }
            )
        }

        EditSection(
            title = "Emergency Contact",
            icon = Icons.Filled.Call
        ) {
            EditInputField(
                label = "Contact Name",
                value = emergencyContactName,
                icon = Icons.Outlined.Person,
                onValueChange = { emergencyContactName = it }
            )

            EditInputField(
                label = "Contact Number",
                value = emergencyContactNumber,
                icon = Icons.Filled.Phone,
                keyboardType = KeyboardType.Phone,
                onValueChange = {
                    emergencyContactNumber = it.filter { char -> char.isDigit() }.take(11)
                }
            )
        }

        EditSection(
            title = "Important Notes",
            icon = Icons.Filled.NoteAlt
        ) {
            EditInputField(
                label = "Important Notes",
                value = notes,
                icon = Icons.Outlined.Description,
                singleLine = false,
                minLines = 4,
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
                    onSave(
                        member.copy(
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            relationship = relationship.trim(),
                            age = age.toIntOrNull(),
                            sex = sex.ifBlank { "Not set" },
                            bloodType = bloodType.ifBlank { "Not set" },
                            allergies = allergies.ifBlank { "None" },
                            conditions = conditions.ifBlank { "None" },
                            medications = medications.ifBlank { "None" },
                            emergencyContactName = emergencyContactName.ifBlank { "Not set" },
                            emergencyContactNumber = emergencyContactNumber.ifBlank { "Not set" },
                            notes = notes.ifBlank { "None" }
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MedGreen)
        ) {
            Icon(Icons.Filled.Save, contentDescription = null)

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Save Changes",
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedButton(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkText),
            border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
        ) {
            Text(
                text = "Cancel",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EditHeroCard(
    name: String,
    relationship: String,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .background(Color(0xFFEAFBF7), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        tint = MedGreen,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Update Family Profile",
                        color = DarkText,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = name,
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

            if (relationship.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Badge,
                            contentDescription = null,
                            tint = MedGreen,
                            modifier = Modifier.size(19.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = "Relationship / Label",
                                color = SoftText,
                                style = MaterialTheme.typography.labelSmall
                            )

                            Text(
                                text = relationship,
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
        }
    }
}

@Composable
private fun EditSection(
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
private fun EditInputField(
    label: String,
    value: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorText: String = "",
    singleLine: Boolean = true,
    minLines: Int = 1,
    onValueChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            leadingIcon = {
                FieldIcon(icon)
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = isError,
            singleLine = singleLine,
            minLines = minLines,
            shape = RoundedCornerShape(14.dp),
            colors = editFieldColors()
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
private fun FieldIcon(icon: ImageVector) {
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
            text = "Please correct the highlighted fields before saving.",
            color = Color(0xFFB91C1C),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun editFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MedGreen,
    unfocusedBorderColor = SoftBorder,
    focusedLabelColor = MedGreen,
    cursorColor = MedGreen,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    errorBorderColor = ErrorRed
)