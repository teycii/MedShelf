package com.example.medshelf.ui.screens

import com.example.medshelf.viewmodel.UserViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    LaunchedEffect(Unit) {
        userViewModel.loadUser()
    }

    val user = userViewModel.user.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MedShelf", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            MedShelfBottomBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_document") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Document")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Hi, ${user?.firstName ?: "User"}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Here's your health overview.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Edit Profile",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(42.dp)
                            .clickable {
                                navController.navigate("edit_profile")
                            }
                    )
                }
            }

            item {
                Button(
                    onClick = { navController.navigate("emergency_snapshot") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Emergency Snapshot")
                }
            }

            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { navController.navigate("document_library") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Documents")
                    }

                    Button(
                        onClick = { navController.navigate("reminders") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reminders")
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { navController.navigate("notes") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Notes")
                    }

                    Button(
                        onClick = { navController.navigate("more") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("More")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}