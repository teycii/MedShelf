package com.example.medshelf.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.medshelf.viewmodel.DocumentViewModel
import com.example.medshelf.viewmodel.FamilyMemberViewModel
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
fun EditDocumentScreen(
    navController: NavController,
    documentViewModel: DocumentViewModel,
    familyMemberViewModel: FamilyMemberViewModel,
    documentId: Int
) {
    val context = LocalContext.current
    val document = documentViewModel.selectedDocument.value
    val familyMembers by familyMemberViewModel.familyMembers.collectAsState()

    LaunchedEffect(documentId) {
        documentViewModel.loadDocumentById(documentId)
    }

    if (document == null) {
        Scaffold(
            topBar = {
                MedShelfTopBar(
                    title = "Edit Document",
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
                                Color(0xFFF9FFFC),
                                Color(0xFFEFFFF8)
                            )
                        )
                    )
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MedGreen)
            }
        }
        return
    }

    val ownerChoices = remember(familyMembers, document.owner) {
        val familyNames = familyMembers.map { member ->
            "${member.firstName} ${member.lastName}".trim().ifBlank { "Unnamed Profile" }
        }

        val baseChoices = listOf("Main Profile") + familyNames

        if (document.owner.isNotBlank() && document.owner !in baseChoices) {
            baseChoices + document.owner
        } else {
            baseChoices
        }
    }

    var title by remember(document.id) { mutableStateOf(document.name) }
    var docType by remember(document.id) { mutableStateOf(document.type) }
    var owner by remember(document.id) {
        mutableStateOf(
            when (document.owner.trim()) {
                "Main profile" -> "Main Profile"
                "Main Profile" -> "Main Profile"
                else -> document.owner.ifBlank { "Main Profile" }
            }
        )
    }
    var date by remember(document.id) { mutableStateOf(document.date) }
    var time by remember(document.id) { mutableStateOf(document.time) }
    var clinic by remember(document.id) { mutableStateOf(document.clinic) }
    var notes by remember(document.id) { mutableStateOf(document.notes) }
    var fileUri by remember(document.id) { mutableStateOf(document.fileUri) }
    var errorMessage by remember { mutableStateOf("") }

    var typeExpanded by remember { mutableStateOf(false) }
    var ownerExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(
        initialHour = parseHourFromTime(time),
        initialMinute = parseMinuteFromTime(time),
        is24Hour = false
    )

    LaunchedEffect(ownerChoices) {
        if (owner !in ownerChoices) {
            owner = "Main Profile"
        }
    }

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

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            fileUri = uri.toString()
            errorMessage = ""

            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
                // Prevent crash if permission is already granted or unsupported.
            }
        }
    }

    Scaffold(
        topBar = {
            MedShelfTopBar(
                title = "Edit Document",
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
                .padding(horizontal = 20.dp)
                .padding(top = 18.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Update Document Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Text(
                text = "Edit details, assign to a profile, or replace the uploaded file.",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftText
            )

            EditFilePreviewBox(
                context = context,
                fileUri = fileUri,
                documentType = docType,
                onChangeFile = {
                    filePickerLauncher.launch(
                        arrayOf(
                            "application/pdf",
                            "image/png",
                            "image/jpeg",
                            "image/jpg",
                            "image/webp"
                        )
                    )
                }
            )

            EditDocumentInputField(
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
                onExpandedChange = {
                    typeExpanded = !typeExpanded
                }
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
                    colors = editTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = {
                        typeExpanded = false
                    }
                ) {
                    documentTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                typeExpanded = false
                                docType = type
                                errorMessage = ""
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = ownerExpanded,
                onExpandedChange = {
                    ownerExpanded = !ownerExpanded
                }
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
                    colors = editTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = ownerExpanded,
                    onDismissRequest = {
                        ownerExpanded = false
                    }
                ) {
                    ownerChoices.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                ownerExpanded = false
                                owner = item
                                errorMessage = ""
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
                    .clickable {
                        showDatePicker = true
                    },
                label = { Text("Document Date (Optional)") },
                placeholder = { Text("Leave blank to use current date") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Event,
                        contentDescription = null,
                        tint = SoftText
                    )
                },
                trailingIcon = {
                    Row {
                        if (date.isNotBlank()) {
                            IconButton(onClick = { date = "" }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear Date",
                                    tint = SoftText
                                )
                            }
                        }

                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = "Choose Date",
                                tint = MedGreen
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = editTextFieldColors()
            )

            OutlinedTextField(
                value = time,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showTimePicker = true
                    },
                label = { Text("Document Time (Optional)") },
                placeholder = { Text("Leave blank to use current time") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = SoftText
                    )
                },
                trailingIcon = {
                    Row {
                        if (time.isNotBlank()) {
                            IconButton(onClick = { time = "" }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear Time",
                                    tint = SoftText
                                )
                            }
                        }

                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = "Choose Time",
                                tint = MedGreen
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = editTextFieldColors()
            )

            EditDocumentInputField(
                label = "Doctor / Clinic / Hospital",
                value = clinic,
                icon = Icons.Filled.LocalHospital,
                placeholder = "e.g., City Health Laboratory",
                onValueChange = {
                    clinic = it
                }
            )

            EditDocumentInputField(
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
                    val now = System.currentTimeMillis()

                    when {
                        title.isBlank() -> {
                            errorMessage = "Please enter the document title."
                        }

                        docType.isBlank() -> {
                            errorMessage = "Please select the document type."
                        }

                        owner.isBlank() -> {
                            errorMessage = "Please select the profile owner."
                        }

                        fileUri.isBlank() -> {
                            errorMessage = "Please select a file."
                        }

                        else -> {
                            val updatedDocument = document.copy(
                                name = title.trim(),
                                type = docType.trim(),
                                owner = owner.trim(),
                                date = date.ifBlank {
                                    formatDocumentDate(now)
                                },
                                time = time.ifBlank {
                                    formatCurrentDocumentTime(now)
                                },
                                clinic = clinic.ifBlank { "Not specified" },
                                notes = notes.ifBlank { "No notes" },
                                fileUri = fileUri
                            )

                            documentViewModel.updateDocument(updatedDocument)

                            navController.navigate("document_details/${document.id}") {
                                popUpTo("edit_document/${document.id}") {
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
                    text = "Save Changes",
                    fontWeight = FontWeight.Bold
                )
            }
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

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = {
                showTimePicker = false
            },
            title = {
                Text(
                    text = "Select Time",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        time = formatDocumentTime(
                            hour = timePickerState.hour,
                            minute = timePickerState.minute
                        )

                        errorMessage = ""
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun EditFilePreviewBox(
    context: Context,
    fileUri: String,
    documentType: String,
    onChangeFile: () -> Unit
) {
    val accent = categoryColor(documentType)
    val icon = categoryIcon(documentType)
    val imageFile = isImageFile(context, fileUri)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clickable {
                onChangeFile()
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (imageFile) {
                AsyncImage(
                    model = Uri.parse(fileUri),
                    contentDescription = "Selected document image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(22.dp))
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.22f))
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF8FAFC))
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
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
                        text = "Current file",
                        color = DarkText,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = Uri.parse(fileUri).lastPathSegment ?: "File selected",
                        color = SoftText,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp),
                color = MedGreen,
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = "Tap to change file",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                )
            }
        }
    }
}

@Composable
private fun EditDocumentInputField(
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
        colors = editTextFieldColors()
    )
}

@Composable
private fun editTextFieldColors() = OutlinedTextFieldDefaults.colors(
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

private fun isImageFile(
    context: Context,
    fileUri: String
): Boolean {
    return try {
        val uri = Uri.parse(fileUri)
        val mimeType = context.contentResolver.getType(uri)
        mimeType?.startsWith("image/") == true
    } catch (_: Exception) {
        false
    }
}

private fun formatDocumentDate(
    millis: Long
): String {
    val formatter = SimpleDateFormat(
        "MMM dd, yyyy",
        Locale.getDefault()
    )

    return formatter.format(Date(millis))
}

private fun formatCurrentDocumentTime(
    millis: Long
): String {
    val formatter = SimpleDateFormat(
        "hh:mm a",
        Locale.getDefault()
    )

    return formatter.format(Date(millis))
}

private fun formatDocumentTime(
    hour: Int,
    minute: Int
): String {
    val amPm = if (hour >= 12) "PM" else "AM"

    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    return "$displayHour:${minute.toString().padStart(2, '0')} $amPm"
}

private fun parseHourFromTime(time: String): Int {
    return parseTimeParts(time).first
}

private fun parseMinuteFromTime(time: String): Int {
    return parseTimeParts(time).second
}

private fun parseTimeParts(time: String): Pair<Int, Int> {
    return try {
        if (time.isBlank()) return Pair(8, 0)

        val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
        val parsedDate = formatter.parse(time) ?: return Pair(8, 0)

        val calendar = java.util.Calendar.getInstance()
        calendar.time = parsedDate

        Pair(
            calendar.get(java.util.Calendar.HOUR_OF_DAY),
            calendar.get(java.util.Calendar.MINUTE)
        )
    } catch (_: Exception) {
        Pair(8, 0)
    }
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