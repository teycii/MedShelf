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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.medshelf.data.AppDatabase
import androidx.compose.ui.platform.LocalContext
import com.example.medshelf.emergency.EmergencyNotificationHelper
import com.example.medshelf.ui.screens.EmergencySnapshotScreen
import com.example.medshelf.ui.screens.*
import com.example.medshelf.ui.theme.MedShelfTheme
import com.example.medshelf.viewmodel.DocumentViewModel
import com.example.medshelf.viewmodel.FamilyMemberViewModel
import com.example.medshelf.viewmodel.FamilyMemberViewModelFactory
import com.example.medshelf.viewmodel.NoteViewModel
import com.example.medshelf.viewmodel.ReminderViewModel
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
    val noteViewModel: NoteViewModel = viewModel()
    val reminderViewModel: ReminderViewModel = viewModel()

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(database.userDao())
    )

    val familyMemberViewModel: FamilyMemberViewModel = viewModel(
        factory = FamilyMemberViewModelFactory(database.familyMemberDao())
    )

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        EmergencyNotificationHelper(context).showEmergencyNotification()
    }

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

            LaunchedEffect(isLoaded, user) {
                if (isLoaded) {
                    val destination = if (user == null) {
                        "registration"
                    } else {
                        "dashboard"
                    }

                    navController.navigate(destination) {
                        popUpTo("loading") {
                            inclusive = true
                        }
                        launchSingleTop = true
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
            RegistrationScreen(
                navController = navController,
                userViewModel = userViewModel
            )
        }

        composable("dashboard") {
            DashboardScreen(
                navController = navController,
                userViewModel = userViewModel,
                documentViewModel = documentViewModel,
                reminderViewModel = reminderViewModel,
                familyMemberViewModel = familyMemberViewModel
            )
        }

        composable("document_library") {
            DocumentLibraryScreen(
                navController = navController,
                documentViewModel = documentViewModel,
                familyMemberViewModel = familyMemberViewModel
            )
        }

        composable(
            route = "document_library/{owner}",
            arguments = listOf(
                navArgument("owner") {
                    type = NavType.StringType
                    defaultValue = "All Profiles"
                }
            )
        ) { backStackEntry ->
            val owner = backStackEntry.arguments
                ?.getString("owner")
                ?: "All Profiles"

            DocumentLibraryScreen(
                navController = navController,
                documentViewModel = documentViewModel,
                familyMemberViewModel = familyMemberViewModel,
                initialOwnerFilter = owner
            )
        }

        composable("add_document") {
            AddDocumentScreen(
                navController = navController,
                documentViewModel = documentViewModel,
                familyMemberViewModel = familyMemberViewModel
            )
        }

        composable("document_details/{documentId}") { backStackEntry ->
            val documentId = backStackEntry.arguments
                ?.getString("documentId")
                ?.toIntOrNull() ?: 0

            DocumentDetailsScreen(
                navController = navController,
                documentViewModel = documentViewModel,
                documentId = documentId
            )
        }

        composable("edit_document/{documentId}") { backStackEntry ->
            val documentId = backStackEntry.arguments
                ?.getString("documentId")
                ?.toIntOrNull() ?: 0

            EditDocumentScreen(
                navController = navController,
                documentViewModel = documentViewModel,
                documentId = documentId
            )
        }

        composable("emergency_snapshot") {
            EmergencySnapshotScreen(
                navController = navController,
                userViewModel = userViewModel
            )
        }

        composable("edit_profile") {
            EditProfileScreen(
                navController = navController,
                userViewModel = userViewModel
            )
        }

        composable("add_family_member") {
            AddFamilyMemberProfilesScreen(
                navController = navController,
                familyMemberViewModel = familyMemberViewModel
            )
        }

        composable("reminders") {
            RemindersScreen(
                navController = navController,
                reminderViewModel = reminderViewModel,
                familyMemberViewModel = familyMemberViewModel
            )
        }

        composable("notes") {
            NotesScreen(
                navController = navController,
                noteViewModel = noteViewModel
            )
        }

        composable("more") {
            Text("More Screen")
        }
    }
}