package com.example.medshelf.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TextDecrease
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medshelf.settings.AppSettingsManager

private val MedGreen = Color(0xFF009688)
private val DarkText = Color(0xFF111827)
private val SoftText = Color(0xFF64748B)
private val SoftBorder = Color(0xFFE2E8F0)
private val ErrorRed = Color(0xFFEF4444)
private val Purple = Color(0xFF7C3AED)

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsManager: AppSettingsManager
) {
    var appLockEnabled by remember {
        mutableStateOf(settingsManager.isAppLockEnabled())
    }

    var passcode by remember {
        mutableStateOf("")
    }

    var confirmPasscode by remember {
        mutableStateOf("")
    }

    var fontScale by remember {
        mutableFloatStateOf(settingsManager.getFontScale())
    }

    var message by remember {
        mutableStateOf("")
    }

    var isErrorMessage by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            MedShelfTopBar(
                title = "Settings",
                navController = navController,
                showBackButton = true
            )
        },
        bottomBar = {
            MedShelfBottomBar(navController)
        },
        containerColor = Color.Transparent
    ) { paddingValues ->

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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            SettingsHeaderCard()

            SettingsSectionCard(
                title = "Display",
                subtitle = "Adjust readability",
                icon = Icons.Filled.FontDownload
            ) {
                Text(
                    text = "Font Size",
                    color = DarkText,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = when {
                        fontScale < 0.95f -> "Small"
                        fontScale > 1.10f -> "Large"
                        else -> "Default"
                    },
                    color = SoftText,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            fontScale = (fontScale - 0.10f).coerceAtLeast(0.85f)
                            settingsManager.setFontScale(fontScale)
                            message = "Font size updated. Restart screen/app if changes are not visible."
                            isErrorMessage = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TextDecrease,
                            contentDescription = "Decrease Font",
                            tint = MedGreen
                        )
                    }

                    Slider(
                        value = fontScale,
                        onValueChange = {
                            fontScale = it
                            settingsManager.setFontScale(it)
                            message = "Font size updated. Restart screen/app if changes are not visible."
                            isErrorMessage = false
                        },
                        valueRange = 0.85f..1.25f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = MedGreen,
                            activeTrackColor = MedGreen
                        )
                    )

                    IconButton(
                        onClick = {
                            fontScale = (fontScale + 0.10f).coerceAtMost(1.25f)
                            settingsManager.setFontScale(fontScale)
                            message = "Font size updated. Restart screen/app if changes are not visible."
                            isErrorMessage = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TextIncrease,
                            contentDescription = "Increase Font",
                            tint = MedGreen
                        )
                    }
                }

                OutlinedButton(
                    onClick = {
                        fontScale = 1.0f
                        settingsManager.setFontScale(1.0f)
                        message = "Font size reset to default."
                        isErrorMessage = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.RestartAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset Font Size")
                }
            }

            SettingsSectionCard(
                title = "App Lock",
                subtitle = "Protect your medical documents with a passcode",
                icon = Icons.Filled.Security
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF8FFFC),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFD6F5EF),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = MedGreen
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable App Lock",
                            color = DarkText,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (appLockEnabled) {
                                "Passcode required when opening MedShelf"
                            } else {
                                "No passcode required"
                            },
                            color = SoftText,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Switch(
                        checked = appLockEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled && !settingsManager.hasPasscode()) {
                                message = "Set a passcode first before enabling App Lock."
                                isErrorMessage = true
                            } else {
                                appLockEnabled = enabled
                                settingsManager.setAppLockEnabled(enabled)
                                message = if (enabled) {
                                    "App Lock enabled."
                                } else {
                                    "App Lock disabled."
                                }
                                isErrorMessage = false
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MedGreen
                        )
                    )
                }

                OutlinedTextField(
                    value = passcode,
                    onValueChange = {
                        passcode = it.filter { char -> char.isDigit() }.take(6)
                        message = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("New Passcode") },
                    placeholder = { Text("4 to 6 digits") },
                    leadingIcon = {
                        Icon(Icons.Filled.Password, contentDescription = null)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp)
                )

                OutlinedTextField(
                    value = confirmPasscode,
                    onValueChange = {
                        confirmPasscode = it.filter { char -> char.isDigit() }.take(6)
                        message = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Confirm Passcode") },
                    placeholder = { Text("Re-enter passcode") },
                    leadingIcon = {
                        Icon(Icons.Filled.Password, contentDescription = null)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp)
                )

                Button(
                    onClick = {
                        when {
                            passcode.length < 4 -> {
                                message = "Passcode must be at least 4 digits."
                                isErrorMessage = true
                            }

                            passcode != confirmPasscode -> {
                                message = "Passcodes do not match."
                                isErrorMessage = true
                            }

                            else -> {
                                settingsManager.savePasscode(passcode)
                                settingsManager.setAppLockEnabled(true)
                                appLockEnabled = true
                                passcode = ""
                                confirmPasscode = ""
                                message = "Passcode saved and App Lock enabled."
                                isErrorMessage = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedGreen,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Filled.Security, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Passcode", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        settingsManager.clearPasscode()
                        appLockEnabled = false
                        passcode = ""
                        confirmPasscode = ""
                        message = "Passcode removed and App Lock disabled."
                        isErrorMessage = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Remove Passcode")
                }
            }

            SettingsSectionCard(
                title = "About",
                subtitle = "App information",
                icon = Icons.Filled.Settings
            ) {
                SettingsInfoRow("App Name", "MedShelf")
                SettingsInfoRow("Purpose", "Medical document storage and emergency access")
                SettingsInfoRow("Storage", "Local offline database")
            }

            if (message.isNotBlank()) {
                MessageBox(
                    message = message,
                    isError = isErrorMessage
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsHeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
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
                    .size(58.dp)
                    .background(Color(0xFFEAFBF7), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null,
                    tint = MedGreen,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "App Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkText
                )

                Text(
                    text = "Customize MedShelf and protect your records.",
                    color = SoftText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFFEAFBF7), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MedGreen
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = title,
                        color = DarkText,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = subtitle,
                        color = SoftText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            content()
        }
    }
}

@Composable
private fun SettingsInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = SoftText,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            color = DarkText,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun MessageBox(
    message: String,
    isError: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isError) Color(0xFFFFEEEE) else Color(0xFFEAFBF7),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = if (isError) Color(0xFFFFCACA) else Color(0xFFD6F5EF),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = if (isError) ErrorRed else MedGreen
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = message,
            color = if (isError) ErrorRed else MedGreen,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodySmall
        )
    }
}