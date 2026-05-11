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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

private val MedGreen = Color(0xFF009688)
private val DarkText = Color(0xFF111827)
private val SoftText = Color(0xFF64748B)
private val SoftBorder = Color(0xFFE2E8F0)
private val ScreenBg = Color(0xFFF8FFFC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedShelfTopBar(
    title: String,
    navController: NavController,
    showBackButton: Boolean = true
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = ScreenBg,
        shadowElevation = 0.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showBackButton) {
                    BackButton {
                        navigateBackFriendly(navController)
                    }

                    Spacer(modifier = Modifier.width(14.dp))
                }

                Text(
                    text = title,
                    color = DarkText,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(
                color = SoftBorder.copy(alpha = 0.65f),
                thickness = 1.dp
            )
        }
    }
}

@Composable
private fun BackButton(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(42.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            SoftBorder
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = DarkText,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun MedShelfBottomBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
        color = Color.White,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BottomNavItem(
                label = "Home",
                icon = Icons.Outlined.Home,
                selected = currentRoute == "dashboard",
                onClick = { navigateHome(navController) }
            )

            BottomNavItem(
                label = "Documents",
                icon = Icons.Outlined.Folder,
                selected = currentRoute == "document_library" ||
                        currentRoute == "document_library/{owner}" ||
                        currentRoute == "document_details/{documentId}" ||
                        currentRoute == "edit_document/{documentId}",
                onClick = {
                    navigateBottom(
                        navController = navController,
                        route = "document_library",
                        currentRoute = currentRoute
                    )
                }
            )

            CenterAddButton(
                selected = currentRoute == "add_document",
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
                selected = currentRoute == "edit_profile" ||
                        currentRoute == "add_family_member",
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
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(if (selected) 62.dp else 58.dp)
            .clickable { onClick() },
        shape = CircleShape,
        color = MedGreen,
        shadowElevation = if (selected) 9.dp else 6.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Document",
                tint = Color.White,
                modifier = Modifier.size(31.dp)
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
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    color = if (selected) MedGreen.copy(alpha = 0.12f) else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
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

private fun navigateHome(navController: NavController) {
    navController.navigate("dashboard") {
        launchSingleTop = true
        restoreState = true

        popUpTo("dashboard") {
            inclusive = false
            saveState = true
        }
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
            inclusive = false
            saveState = true
        }
    }
}

private fun navigateBackFriendly(navController: NavController) {
    when (navController.currentBackStackEntry?.destination?.route) {
        "dashboard" -> Unit

        "document_library",
        "reminders",
        "edit_profile",
        "notes",
        "add_document" -> {
            navigateHome(navController)
        }

        else -> {
            val didPop = navController.popBackStack()
            if (!didPop) {
                navigateHome(navController)
            }
        }
    }
}