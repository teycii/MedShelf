package com.example.medshelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medshelf.data.AppDatabase
import com.example.medshelf.ui.screens.*
import com.example.medshelf.ui.theme.MedShelfTheme
import com.example.medshelf.viewmodel.DocumentViewModel
import com.example.medshelf.viewmodel.UserViewModel
import com.example.medshelf.viewmodel.UserViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)

        setContent {
            MedShelfTheme {
                MedShelfApp(database)
            }
        }
    }
}

@Composable
fun MedShelfApp(database: AppDatabase) {
    val navController = rememberNavController()

    val documentViewModel: DocumentViewModel = viewModel()

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(database.userDao())
    )

    NavHost(
        navController = navController,
        startDestination = "loading"
    ) {
        composable("loading") {
            LaunchedEffect(Unit) {
                userViewModel.loadUser()
            }

            val isLoaded = userViewModel.isUserLoaded.value
            val user = userViewModel.user.value

            LaunchedEffect(isLoaded) {
                if (isLoaded) {
                    if (user == null) {
                        navController.navigate("registration") {
                            popUpTo("loading") { inclusive = true }
                        }
                    } else {
                        navController.navigate("dashboard") {
                            popUpTo("loading") { inclusive = true }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        composable("registration") {
            RegistrationScreen(navController, userViewModel)
        }

        composable("dashboard") {
            DashboardScreen(navController, userViewModel)
        }

        composable("document_library") {
            DocumentLibraryScreen(navController, documentViewModel)
        }

        composable("add_document") {
            AddDocumentScreen(navController, documentViewModel)
        }

        composable("emergency_snapshot") {
            EmergencySnapshotScreen(navController, userViewModel)
        }

        composable("edit_profile") {
            EditProfileScreen(navController, userViewModel)
        }

        composable("document_details") {
            DocumentDetailsScreen(navController)
        }

        composable("reminders") {
            Text("Reminders Screen")
        }

        composable("notes") {
            Text("Notes Screen")
        }

        composable("more") {
            Text("More Screen")
        }
    }
}