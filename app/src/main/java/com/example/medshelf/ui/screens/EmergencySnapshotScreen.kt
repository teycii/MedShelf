package com.example.medshelf.ui.screens

import com.example.medshelf.viewmodel.UserViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.medshelf.ui.theme.Navy

@Composable
fun EmergencySnapshotScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    LaunchedEffect(Unit) {
        userViewModel.loadUser()
    }

    val user = userViewModel.user.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "EMERGENCY INFORMATION",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "This information can save my life.",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            EmergencyInfoCard(
                "Name",
                if (user != null) "${user.firstName} ${user.lastName}" else "Not set"
            )
            EmergencyInfoCard("Age", user?.age?.toString() ?: "Not set")
            EmergencyInfoCard("Blood Type", user?.bloodType ?: "Not set")
            EmergencyInfoCard("Allergies", user?.allergies ?: "Not set")
            EmergencyInfoCard("Medical Conditions", user?.conditions ?: "Not set")
            EmergencyInfoCard("Current Medications", user?.medications ?: "Not set")
            EmergencyInfoCard(
                "Emergency Contact",
                if (user != null)
                    "${user.emergencyContactName} (${user.emergencyContactNumber})"
                else "Not set"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
        }
    }
}

@Composable
fun EmergencyInfoCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.5f)
            )
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}