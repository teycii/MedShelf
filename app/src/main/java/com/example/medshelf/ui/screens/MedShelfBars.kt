package com.example.medshelf.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Description
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
private fun BackButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(42.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, SoftBorder)
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .height(88.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 10.dp,
            border = BorderStroke(1.dp, SoftBorder.copy(alpha = 0.45f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavItem(
                        label = "Home",
                        icon = if (currentRoute == "dashboard") Icons.Filled.Home else Icons.Outlined.Home,
                        selected = currentRoute == "dashboard",
                        enabled = currentRoute != "dashboard",
                        onClick = { navigateRoot(navController, "dashboard") }
                    )

                    BottomNavItem(
                        label = "Library",
                        icon = if (isDocumentRoute(currentRoute)) Icons.Filled.Description else Icons.Outlined.Description,
                        selected = isDocumentRoute(currentRoute),
                        enabled = currentRoute != "document_library",
                        onClick = { navigateRoot(navController, "document_library") }
                    )
                }

                Spacer(modifier = Modifier.width(78.dp))

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavItem(
                        label = "Reminders",
                        icon = if (currentRoute == "reminders") Icons.Filled.Notifications else Icons.Outlined.Notifications,
                        selected = currentRoute == "reminders",
                        enabled = currentRoute != "reminders",
                        onClick = { navigateRoot(navController, "reminders") }
                    )

                    BottomNavItem(
                        label = "Profile",
                        icon = if (isProfileRoute(currentRoute)) Icons.Filled.AccountCircle else Icons.Outlined.AccountCircle,
                        selected = isProfileRoute(currentRoute),
                        enabled = currentRoute != "edit_profile",
                        onClick = { navigateRoot(navController, "edit_profile") }
                    )
                }
            }
        }

        CenterAddButton(
            selected = currentRoute == "add_document",
            enabled = currentRoute != "add_document",
            onClick = { navigateRoot(navController, "add_document") },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(76.dp)
            .clickable(enabled = enabled) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = if (selected) MedGreen.copy(alpha = 0.14f) else Color.Transparent
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (selected) MedGreen else SoftText,
                    modifier = Modifier.size(if (selected) 25.dp else 22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = if (selected) MedGreen else SoftText,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
private fun CenterAddButton(
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(66.dp)
            .clickable(enabled = enabled) { onClick() },
        shape = CircleShape,
        color = MedGreen,
        shadowElevation = 12.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Document",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

private fun isDocumentRoute(currentRoute: String?): Boolean {
    return currentRoute == "document_library" ||
            currentRoute == "document_library/{owner}" ||
            currentRoute == "document_details/{documentId}" ||
            currentRoute == "edit_document/{documentId}"
}

private fun isProfileRoute(currentRoute: String?): Boolean {
    return currentRoute == "edit_profile" ||
            currentRoute == "add_family_member" ||
            currentRoute == "family_member_details/{familyMemberId}" ||
            currentRoute == "edit_family_member/{familyMemberId}"
}

private fun navigateRoot(
    navController: NavController,
    route: String
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    if (currentRoute == route) return

    navController.navigate(route) {
        launchSingleTop = true

        popUpTo("dashboard") {
            inclusive = route == "dashboard"
            saveState = false
        }

        restoreState = false
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
            navigateRoot(navController, "dashboard")
        }

        "document_details/{documentId}",
        "edit_document/{documentId}" -> {
            navController.navigate("document_library") {
                launchSingleTop = true

                popUpTo("dashboard") {
                    inclusive = false
                    saveState = false
                }

                restoreState = false
            }
        }

        else -> {
            val didPop = navController.popBackStack()

            if (!didPop) {
                navigateRoot(navController, "dashboard")
            }
        }
    }
}