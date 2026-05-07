package com.example.medshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

private val MedGreen = Color(0xFF009688)
private val SoftText = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedShelfTopBar(
    title: String,
    navController: NavController,
    showBackButton: Boolean = true
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun MedShelfBottomBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp),
        color = Color.White,
        shadowElevation = 10.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BottomItem(
                label = "Home",
                icon = Icons.Outlined.Home,
                selected = currentRoute == "dashboard",
                onClick = {
                    navController.navigate("dashboard") {
                        launchSingleTop = true
                        popUpTo("dashboard") { inclusive = false }
                    }
                }
            )

            BottomItem(
                label = "Documents",
                icon = Icons.Outlined.Folder,
                selected = currentRoute == "document_library",
                onClick = {
                    navController.navigate("document_library") {
                        launchSingleTop = true
                    }
                }
            )

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(MedGreen, CircleShape)
                    .clickable {
                        navController.navigate("add_document") {
                            launchSingleTop = true
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Document",
                    tint = Color.White,
                    modifier = Modifier.size(31.dp)
                )
            }

            BottomItem(
                label = "Reminders",
                icon = Icons.Outlined.Notifications,
                selected = currentRoute == "reminders",
                onClick = {
                    navController.navigate("reminders") {
                        launchSingleTop = true
                    }
                }
            )

            BottomItem(
                label = "Profile",
                icon = Icons.Outlined.AccountCircle,
                selected = currentRoute == "edit_profile",
                onClick = {
                    navController.navigate("edit_profile") {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
private fun BottomItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) MedGreen else SoftText,
                modifier = Modifier.size(21.dp)
            )

            Text(
                text = label,
                color = if (selected) MedGreen else SoftText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}