package com.example.medshelf.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.medshelf.viewmodel.DocumentViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var docType by remember { mutableStateOf("") }
    var owner by remember { mutableStateOf("Main profile") }
    var date by remember { mutableStateOf("") }
    var clinic by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    var typeExpanded by remember { mutableStateOf(false) }
    var ownerExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

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

            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
                // Some providers already grant temporary permission.
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingCameraUri != null) {
            selectedFileUri = pendingCameraUri
            errorMessage = ""
        } else {
            errorMessage = "Camera capture was cancelled."
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createCameraImageUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            errorMessage = "Camera permission is required to capture documents."
        }
    }

    fun openCamera() {
        val permissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            val uri = createCameraImageUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            MedShelfTopBar(
                title = "Add Document",
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

            Text(
                text = "Document Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Text(
                text = "Upload, scan, or capture a medical document.",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftText
            )

            UploadBox(
                context = context,
                selectedFileUri = selectedFileUri,
                documentType = docType,
                onUploadClick = {
                    filePickerLauncher.launch(
                        arrayOf(
                            "application/pdf",
                            "image/png",
                            "image/jpeg",
                            "image/jpg",
                            "image/webp"
                        )
                    )
                },
                onCameraClick = {
                    openCamera()
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
                    colors = textFieldColors()
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
                    colors = textFieldColors()
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

            OutlinedTextField(
                value = date,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                label = { Text("Document Date") },
                placeholder = { Text("Select document date") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Event,
                        contentDescription = null,
                        tint = SoftText
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = "Choose Date",
                            tint = MedGreen
                        )
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = textFieldColors()
            )

            AddDocumentInputField(
                label = "Doctor / Clinic / Hospital",
                value = clinic,
                icon = Icons.Filled.LocalHospital,
                placeholder = "e.g., City Health Laboratory",
                onValueChange = {
                    clinic = it
                }
            )

            AddDocumentInputField(
                label = "Notes / Details",
                value = notes,
                icon = Icons.Filled.NoteAlt,
                placeholder = "e.g., Routine checkup, normal result",
                singleLine = false,
                minLines = 4,
                onValueChange = {
                    notes = it
                }
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
                        selectedFileUri == null -> {
                            errorMessage = "Please upload or capture a document."
                        }

                        title.isBlank() -> {
                            errorMessage = "Please enter the document title."
                        }

                        docType.isBlank() -> {
                            errorMessage = "Please select the document type."
                        }

                        date.isBlank() -> {
                            errorMessage = "Please select the document date."
                        }

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
                                popUpTo("add_document") {
                                    inclusive = true
                                }
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

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            date = formatDocumentDate(millis)
                            errorMessage = ""
                        }

                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun UploadBox(
    context: Context,
    selectedFileUri: Uri?,
    documentType: String,
    onUploadClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    val imageFile = selectedFileUri?.let { isImageFile(context, it) } == true
    val accent = categoryColor(documentType)
    val icon = categoryIcon(documentType)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clickable { onUploadClick() },
                contentAlignment = Alignment.Center
            ) {
                when {
                    selectedFileUri != null && imageFile -> {
                        AsyncImage(
                            model = selectedFileUri,
                            contentDescription = "Selected image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(18.dp))
                        )
                    }

                    selectedFileUri != null -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .background(
                                        color = accent.copy(alpha = 0.12f),
                                        shape = PaperFoldShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = accent,
                                    modifier = Modifier.size(34.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "File selected",
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )

                            Text(
                                text = selectedFileUri.lastPathSegment ?: "Selected file",
                                style = MaterialTheme.typography.bodySmall,
                                color = SoftText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    else -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(Color(0xFFE6F7F4), RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CloudUpload,
                                    contentDescription = null,
                                    tint = MedGreen,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Upload or capture document",
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )

                            Text(
                                text = "PDF, JPG, PNG, WEBP",
                                style = MaterialTheme.typography.bodySmall,
                                color = SoftText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onUploadClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudUpload,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text("Upload")
                }

                Button(
                    onClick = onCameraClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedGreen)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text("Camera")
                }
            }
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
        colors = textFieldColors()
    )
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MedGreen,
    unfocusedBorderColor = SoftBorder,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    cursorColor = MedGreen
)

private val PaperFoldShape: Shape = GenericShape { size, _ ->
    val fold = size.width * 0.28f

    moveTo(0f, 0f)
    lineTo(size.width - fold, 0f)
    lineTo(size.width, fold)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}

private fun createCameraImageUri(context: Context): Uri {
    val imageFile = File(
        context.cacheDir,
        "medshelf_camera_${System.currentTimeMillis()}.jpg"
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

private fun isImageFile(
    context: Context,
    uri: Uri
): Boolean {
    return try {
        val mimeType = context.contentResolver.getType(uri)
        mimeType?.startsWith("image/") == true
    } catch (_: Exception) {
        false
    }
}

private fun formatDocumentDate(millis: Long): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

private fun categoryIcon(type: String): ImageVector {
    return when {
        type.contains("Lab", ignoreCase = true) -> Icons.Default.Biotech
        type.contains("Prescription", ignoreCase = true) -> Icons.Default.Medication
        type.contains("Certificate", ignoreCase = true) -> Icons.Default.Badge
        type.contains("X-Ray", ignoreCase = true) || type.contains("Imaging", ignoreCase = true) -> Icons.Default.MonitorHeart
        type.contains("Vaccination", ignoreCase = true) -> Icons.Default.Vaccines
        type.contains("Discharge", ignoreCase = true) -> Icons.Default.LocalHospital
        type.contains("Bill", ignoreCase = true) -> Icons.AutoMirrored.Filled.ReceiptLong
        type.contains("Insurance", ignoreCase = true) -> Icons.Default.Shield
        type.contains("Note", ignoreCase = true) -> Icons.Default.NoteAlt
        type.contains("Clearance", ignoreCase = true) -> Icons.Default.HealthAndSafety
        type.contains("ECG", ignoreCase = true) || type.contains("Heart", ignoreCase = true) -> Icons.Default.Favorite
        else -> Icons.Default.Description
    }
}

private fun categoryColor(type: String): Color {
    return when {
        type.contains("Lab", ignoreCase = true) -> Color(0xFF0EA5E9)
        type.contains("Prescription", ignoreCase = true) -> Color(0xFF7C3AED)
        type.contains("Certificate", ignoreCase = true) -> Color(0xFFF59E0B)
        type.contains("X-Ray", ignoreCase = true) || type.contains("Imaging", ignoreCase = true) -> Color(0xFF6366F1)
        type.contains("Vaccination", ignoreCase = true) -> Color(0xFF16A34A)
        type.contains("Discharge", ignoreCase = true) -> Color(0xFFEF4444)
        type.contains("Bill", ignoreCase = true) -> Color(0xFFEA580C)
        type.contains("Insurance", ignoreCase = true) -> Color(0xFF0891B2)
        type.contains("Note", ignoreCase = true) -> Color(0xFF8B5CF6)
        type.contains("Clearance", ignoreCase = true) -> Color(0xFF14B8A6)
        type.contains("ECG", ignoreCase = true) || type.contains("Heart", ignoreCase = true) -> Color(0xFFE11D48)
        else -> MedGreen
    }
}