package com.example.medshelf.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
                                Color.White,
                                Color(0xFFEFFFF8)
                            )
                        )
                    )
                    .padding(paddingValues)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DocumentHeaderCard(
                    title = document.name,
                    type = document.type,
                    owner = document.owner
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
            }
        }
    }
}

@Composable
private fun DocumentHeaderCard(
    title: String,
    type: String,
    owner: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .background(Color(0xFFE6F7F4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = null,
                    tint = MedGreen,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Text(
                    text = type,
                    color = SoftText,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = Color(0xFFEAFBF7),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = owner,
                        color = MedGreen,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
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
            DetailRow(Icons.Filled.LocalHospital, "Clinic", clinic)
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
        Column(modifier = Modifier.padding(16.dp)) {
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