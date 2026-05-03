package com.example.medshelf.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medshelf.viewmodel.DocumentViewModel

@Composable
fun AddDocumentScreen(
    navController: NavController,
    documentViewModel: DocumentViewModel
) {
    Scaffold(
        topBar = {
            MedShelfTopBar(
                title = "Add Document",
                navController = navController,
                showBackButton = true
            )
        }
    ) { paddingValues ->

        var docType by remember { mutableStateOf("") }
        var title by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Document Information", fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = docType,
                onValueChange = { docType = it },
                label = { Text("Document Type") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    documentViewModel.addDocument(
                        name = title,
                        type = docType,
                        fileUri = "sample_uri"
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Document")
            }
        }
    }
}