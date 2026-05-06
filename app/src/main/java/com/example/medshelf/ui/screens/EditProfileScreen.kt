package com.example.medshelf.ui.screens

import com.example.medshelf.viewmodel.UserViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun EditProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val user = userViewModel.user.value

    var firstName by remember(user) { mutableStateOf(user?.firstName ?: "") }
    var lastName by remember(user) { mutableStateOf(user?.lastName ?: "") }

    var age by remember(user) { mutableStateOf(user?.age?.toString() ?: "") }
    var bloodType by remember(user) { mutableStateOf(user?.bloodType ?: "") }
    var allergies by remember(user) { mutableStateOf(user?.allergies ?: "") }
    var conditions by remember(user) { mutableStateOf(user?.conditions ?: "") }
    var medications by remember(user) { mutableStateOf(user?.medications ?: "") }

    // ✅ FIXED: split emergency contact
    var emergencyContactName by remember(user) { mutableStateOf(user?.emergencyContactName ?: "") }
    var emergencyContactNumber by remember(user) { mutableStateOf(user?.emergencyContactNumber ?: "") }

    Scaffold(
        topBar = {
            MedShelfTopBar(
                title = "Edit Profile",
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = bloodType,
                onValueChange = { bloodType = it },
                label = { Text("Blood Type") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = allergies,
                onValueChange = { allergies = it },
                label = { Text("Allergies") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = conditions,
                onValueChange = { conditions = it },
                label = { Text("Medical Conditions") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = medications,
                onValueChange = { medications = it },
                label = { Text("Current Medications") },
                modifier = Modifier.fillMaxWidth()
            )

            // ✅ NEW FIELDS
            OutlinedTextField(
                value = emergencyContactName,
                onValueChange = { emergencyContactName = it },
                label = { Text("Emergency Contact Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = emergencyContactNumber,
                onValueChange = { emergencyContactNumber = it },
                label = { Text("Emergency Contact Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    userViewModel.saveUser(
                        firstName = firstName,
                        lastName = lastName,
                        age = age.toIntOrNull() ?: 0,
                        bloodType = bloodType,
                        allergies = allergies,
                        conditions = conditions,
                        medications = medications,
                        emergencyContactName = emergencyContactName,
                        emergencyContactNumber = emergencyContactNumber
                    )

                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}