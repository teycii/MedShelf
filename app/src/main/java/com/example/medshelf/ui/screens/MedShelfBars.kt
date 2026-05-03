package com.example.medshelf.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedShelfTopBar(
    title: String,
    navController: NavController,
    showBackButton: Boolean = true
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        }
    )
}

@Composable
fun MedShelfBottomBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "dashboard",
            onClick = {
                navController.navigate("dashboard") {
                    launchSingleTop = true
                    popUpTo("dashboard") { inclusive = false }
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = currentRoute == "document_library",
            onClick = {
                navController.navigate("document_library") {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Description, contentDescription = "Documents") },
            label = { Text("Documents") }
        )

        NavigationBarItem(
            selected = currentRoute == "reminders",
            onClick = {
                navController.navigate("reminders") {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Reminders") },
            label = { Text("Reminders") }
        )

        NavigationBarItem(
            selected = currentRoute == "notes",
            onClick = {
                navController.navigate("notes") {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.NoteAlt, contentDescription = "Notes") },
            label = { Text("Notes") }
        )

        NavigationBarItem(
            selected = currentRoute == "more",
            onClick = {
                navController.navigate("more") {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "More") },
            label = { Text("More") }
        )
    }
}