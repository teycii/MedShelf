package com.example.medshelf.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun DocumentDetailsScreen(navController: NavController) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Document Name: Medical Certificate")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Date: 2023-10-27")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Description: This is a sample medical certificate.")
        }
    }
}