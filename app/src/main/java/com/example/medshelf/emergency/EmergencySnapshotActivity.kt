package com.example.medshelf.emergency

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.example.medshelf.data.AppDatabase
import com.example.medshelf.ui.screens.EmergencySnapshotContent
import com.example.medshelf.ui.theme.MedShelfTheme
import com.example.medshelf.viewmodel.UserViewModel
import com.example.medshelf.viewmodel.UserViewModelFactory

class EmergencySnapshotActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }

        @Suppress("DEPRECATION")
        window.addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)

        val database = AppDatabase.getDatabase(applicationContext)

        val userViewModel: UserViewModel =
            UserViewModelFactory(database.userDao())
                .create(UserViewModel::class.java)

        setContent {
            MedShelfTheme {
                LaunchedEffect(Unit) {
                    userViewModel.loadUser()
                }

                val user by userViewModel.user

                EmergencySnapshotContent(
                    name = user?.let { "${it.firstName} ${it.lastName}" } ?: "Not set",
                    age = user?.age?.toString() ?: "Not set",
                    birthday = user?.birthday ?: "Not set",
                    sex = user?.sex ?: "Not set",
                    address = user?.address ?: "Not set",
                    bloodType = user?.bloodType ?: "Not set",
                    allergies = user?.allergies ?: "None",
                    conditions = user?.conditions ?: "None",
                    medications = user?.medications ?: "None",
                    importantNote = user?.importantNote ?: "None",
                    emergencyContact = user?.let {
                        "${it.emergencyContactName} (${it.emergencyContactNumber})"
                    } ?: "Not set",
                    onClose = { finish() }
                )
            }
        }
    }
}