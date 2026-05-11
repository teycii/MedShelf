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
                IconButton(
                    onClick = {
                        if (!navController.popBackStack()) {
                            navController.navigate("dashboard") {
                                launchSingleTop = true
                                popUpTo("dashboard") {
                                    inclusive = false
                                }
                            }
                        }
                    }
                ) {
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
            .height(92.dp),
        color = Color.White,
        shadowElevation = 14.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BottomNavItem(
                label = "Home",
                icon = Icons.Outlined.Home,
                selected = currentRoute == "dashboard",
                onClick = {
                    navigateBottom(
                        navController = navController,
                        route = "dashboard",
                        currentRoute = currentRoute
                    )
                }
            )

            BottomNavItem(
                label = "Documents",
                icon = Icons.Outlined.Folder,
                selected = currentRoute == "document_library",
                onClick = {
                    navigateBottom(
                        navController = navController,
                        route = "document_library",
                        currentRoute = currentRoute
                    )
                }
            )

            CenterAddButton(
                onClick = {
                    navigateBottom(
                        navController = navController,
                        route = "add_document",
                        currentRoute = currentRoute
                    )
                }
            )

            BottomNavItem(
                label = "Reminders",
                icon = Icons.Outlined.Notifications,
                selected = currentRoute == "reminders",
                onClick = {
                    navigateBottom(
                        navController = navController,
                        route = "reminders",
                        currentRoute = currentRoute
                    )
                }
            )

            BottomNavItem(
                label = "Profile",
                icon = Icons.Outlined.AccountCircle,
                selected = currentRoute == "edit_profile",
                onClick = {
                    navigateBottom(
                        navController = navController,
                        route = "edit_profile",
                        currentRoute = currentRoute
                    )
                }
            )
        }
    }
}

@Composable
private fun CenterAddButton(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(60.dp)
            .clickable { onClick() },
        shape = CircleShape,
        color = MedGreen,
        shadowElevation = 8.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Document",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(68.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    color = if (selected) MedGreen.copy(alpha = 0.13f) else Color.Transparent,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) MedGreen else SoftText,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = if (selected) MedGreen else SoftText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
    }
}

private fun navigateBottom(
    navController: NavController,
    route: String,
    currentRoute: String?
) {
    if (currentRoute == route) return

    navController.navigate(route) {
        launchSingleTop = true
        restoreState = true

        popUpTo("dashboard") {
            saveState = true
        }
    }
}