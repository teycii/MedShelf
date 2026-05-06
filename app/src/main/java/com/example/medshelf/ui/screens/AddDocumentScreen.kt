package com.example.medshelf.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
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
import com.example.medshelf.viewmodel.DocumentViewModel

private val MedGreen = Color(0xFF009688)
private val DarkText = Color(0xFF111827)
private val SoftText = Color(0xFF64748B)
private val SoftBorder = Color(0xFFE2E8F0)
private val ErrorRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDocumentScreen(
    navController: NavController,
    documentViewModel: DocumentViewModel
) {
    var title by remember { mutableStateOf("") }
    var docType by remember { mutableStateOf("") }
    var owner by remember { mutableStateOf("Main profile") }
    var date by remember { mutableStateOf("") }
    var clinic by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    var typeExpanded by remember { mutableStateOf(false) }
    var ownerExpanded by remember { mutableStateOf(false) }

    val documentTypes = listOf(
        "Laboratory Result",
        "Prescription",
        "Medical Certificate",
        "X-Ray / Imaging",
        "Vaccination Record",
        "Discharge Summary",
        "Hospital Bill",
        "Insurance Document",
        "Doctor's Note",
        "Medical Clearance",
        "Ultrasound Result",
        "ECG / Heart Test",
        "Dental Record",
        "Eye Checkup Record",
        "Surgery Record",
        "Maintenance Medication",
        "Allergy Record",
        "Emergency Document",
        "Family Member Record",
        "Other"
    )

    val owners = listOf("Main profile", "Family member")

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            selectedFileUri = uri
            errorMessage = ""
        }
    }

    Scaffold(
        topBar = {
            MedShelfTopBar(
                title = "Add Document",
                navController = navController,
                showBackButton = true
            )
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Document Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Text(
                    text = "Upload and organize a medical document.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftText
                )

                UploadBox(
                    selectedFileUri = selectedFileUri,
                    onClick = {
                        filePickerLauncher.launch(arrayOf("application/pdf", "image/*"))
                    }
                )

                AddDocumentInputField(
                    label = "Document Title",
                    value = title,
                    icon = Icons.Filled.Description,
                    placeholder = "e.g., CBC Lab Result",
                    onValueChange = {
                        title = it
                        errorMessage = ""
                    }
                )

                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = docType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Document Type") },
                        placeholder = { Text("Select document type") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Category,
                                contentDescription = null,
                                tint = SoftText
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(
                                type = MenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedGreen,
                            unfocusedBorderColor = SoftBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = MedGreen
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        documentTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    docType = type
                                    typeExpanded = false
                                    errorMessage = ""
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = ownerExpanded,
                    onExpandedChange = { ownerExpanded = !ownerExpanded }
                ) {
                    OutlinedTextField(
                        value = owner,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Profile / Owner") },
                        placeholder = { Text("Select profile") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = SoftText
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = ownerExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(
                                type = MenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedGreen,
                            unfocusedBorderColor = SoftBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = MedGreen
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = ownerExpanded,
                        onDismissRequest = { ownerExpanded = false }
                    ) {
                        owners.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    owner = item
                                    ownerExpanded = false
                                }
                            )
                        }
                    }
                }

                AddDocumentInputField(
                    label = "Document Date",
                    value = date,
                    icon = Icons.Filled.CalendarMonth,
                    placeholder = "e.g., May 18, 2024",
                    onValueChange = {
                        date = it
                        errorMessage = ""
                    }
                )

                AddDocumentInputField(
                    label = "Doctor / Clinic",
                    value = clinic,
                    icon = Icons.Filled.LocalHospital,
                    placeholder = "e.g., City Health Laboratory",
                    onValueChange = { clinic = it }
                )

                AddDocumentInputField(
                    label = "Notes",
                    value = notes,
                    icon = Icons.Filled.NoteAlt,
                    placeholder = "e.g., Routine checkup, normal result",
                    singleLine = false,
                    minLines = 3,
                    onValueChange = { notes = it }
                )

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
                            selectedFileUri == null -> errorMessage = "Please select a file."
                            title.isBlank() -> errorMessage = "Please enter the document title."
                            docType.isBlank() -> errorMessage = "Please select the document type."
                            date.isBlank() -> errorMessage = "Please enter the document date."

                            else -> {
                                documentViewModel.addDocument(
                                    name = title.trim(),
                                    type = docType.trim(),
                                    owner = owner.trim(),
                                    date = date.trim(),
                                    clinic = clinic.ifBlank { "Not specified" },
                                    notes = notes.ifBlank { "No notes" },
                                    fileUri = selectedFileUri.toString()
                                )

                                navController.navigate("document_library") {
                                    popUpTo("add_document") { inclusive = true }
                                }
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
                        text = "Save Document",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun UploadBox(
    selectedFileUri: Uri?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Color(0xFFE6F7F4), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (selectedFileUri == null) Icons.Filled.CloudUpload else Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MedGreen,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (selectedFileUri == null) "Tap to upload document" else "File selected",
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Text(
                text = selectedFileUri?.lastPathSegment ?: "PDF, JPG, PNG",
                style = MaterialTheme.typography.bodySmall,
                color = SoftText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AddDocumentInputField(
    label: String,
    value: String,
    icon: ImageVector,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
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