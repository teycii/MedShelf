package com.example.medshelf.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.medshelf.viewmodel.DocumentViewModel

private val MedGreen = Color(0xFF009688)
private val DarkText = Color(0xFF111827)
private val SoftText = Color(0xFF64748B)
private val SoftBorder = Color(0xFFE2E8F0)

@Composable
fun DocumentDetailsScreen(
    navController: NavController,
    documentViewModel: DocumentViewModel,
    documentId: Int
) {
    val context = LocalContext.current
    val document = documentViewModel.selectedDocument.value

    LaunchedEffect(documentId) {
        documentViewModel.loadDocumentById(documentId)
    }

    Scaffold(
        topBar = {
            MedShelfTopBar(
                title = "Document Details",
                navController = navController,
                showBackButton = true
            )
        },
        bottomBar = {
            MedShelfBottomBar(navController)
        }
    ) { paddingValues ->

        if (document == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MedGreen)
            }
        } else {
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
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DocumentPreviewCard(
                    title = document.name,
                    type = document.type,
                    owner = document.owner,
                    fileUri = document.fileUri
                )

                InfoCard(
                    owner = document.owner,
                    category = document.type,
                    clinic = document.clinic,
                    date = document.date
                )

                NotesCard(notes = document.notes)

                Button(
                    onClick = {
                        navController.navigate("edit_document/${document.id}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Document", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(Uri.parse(document.fileUri), "*/*")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        context.startActivity(
                            Intent.createChooser(intent, "Open document")
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedGreen)
                ) {
                    Icon(Icons.Filled.Visibility, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open File", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun DocumentPreviewCard(
    title: String,
    type: String,
    owner: String,
    fileUri: String
) {
    val context = LocalContext.current
    val accent = categoryColor(type)
    val icon = categoryIcon(type)
    val imageFile = isImageFile(context, fileUri)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (imageFile) {
                AsyncImage(
                    model = Uri.parse(fileUri),
                    contentDescription = title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFE6F7F4))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            color = accent.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .background(
                                color = accent.copy(alpha = 0.15f),
                                shape = PaperFoldShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title.ifBlank { "Untitled Document" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Text(
                text = type.ifBlank { "Uncategorized" },
                color = SoftText,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = Color(0xFFEAFBF7),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = owner.ifBlank { "Main profile" },
                    color = MedGreen,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun InfoCard(
    owner: String,
    category: String,
    clinic: String,
    date: String
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailRow(Icons.Filled.Person, "Owner", owner)
            DetailRow(Icons.Filled.Folder, "Category", category)
            DetailRow(Icons.Filled.LocalHospital, "Clinic / Hospital", clinic)
            DetailRow(Icons.Filled.Description, "Date", date)
        }
    }
}

@Composable
private fun NotesCard(notes: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.NoteAlt, contentDescription = null, tint = MedGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Notes",
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = notes.ifBlank { "No notes added." },
                color = SoftText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MedGreen,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                color = SoftText,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = value.ifBlank { "Not specified" },
                color = DarkText,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

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