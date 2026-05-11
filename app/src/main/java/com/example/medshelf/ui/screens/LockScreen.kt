package com.example.medshelf.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medshelf.R
import com.example.medshelf.settings.AppSettingsManager

private val MedGreen = Color(0xFF009688)
private val DarkText = Color(0xFF111827)
private val SoftText = Color(0xFF64748B)
private val SoftBorder = Color(0xFFE2E8F0)
private val ErrorRed = Color(0xFFEF4444)

@Composable
fun LockScreen(
    navController: NavController,
    settingsManager: AppSettingsManager
) {
    var passcode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(containerColor = Color.Transparent) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFF3FFFB),
                            Color(0xFFE8FFF7)
                        )
                    )
                )
                .padding(paddingValues)
                .padding(horizontal = 22.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(5.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.medshelf_icon),
                        contentDescription = "MedShelf Logo",
                        modifier = Modifier.size(92.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "MedShelf Locked",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkText
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Enter your passcode to continue.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftText
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = passcode,
                        onValueChange = {
                            passcode = it.filter { char -> char.isDigit() }.take(6)
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Passcode") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = SoftText
                            )
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword
                        ),
                        isError = errorMessage.isNotBlank(),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (errorMessage.isNotBlank()) ErrorRed else MedGreen,
                            unfocusedBorderColor = if (errorMessage.isNotBlank()) ErrorRed else SoftBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            errorBorderColor = ErrorRed,
                            cursorColor = MedGreen
                        )
                    )

                    if (errorMessage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFFFEEEE),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFFFCACA),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = ErrorRed
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = errorMessage,
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Button(
                        onClick = {
                            val savedPasscode = settingsManager.getPasscode()

                            if (passcode == savedPasscode) {
                                navController.navigate("dashboard") {
                                    popUpTo("lock_screen") {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            } else {
                                errorMessage = "Incorrect passcode. Please try again."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MedGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Filled.LockOpen, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Unlock", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}