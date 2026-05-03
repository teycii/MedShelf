package com.example.medshelf.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medshelf.data.AppDatabase
import com.example.medshelf.model.UserEntity
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).userDao()

    var user = mutableStateOf<UserEntity?>(null)
        private set

    var isUserLoaded = mutableStateOf(false)
        private set

    fun loadUser() {
        viewModelScope.launch {
            user.value = dao.getUser()
            isUserLoaded.value = true
        }
    }

    fun saveUser(
        firstName: String,
        lastName: String,
        age: Int,
        bloodType: String,
        allergies: String,
        conditions: String,
        medications: String,
        emergencyContactName: String,
        emergencyContactNumber: String
    ) {
        viewModelScope.launch {
            val newUser = UserEntity(
                id = 1,
                firstName = firstName,
                lastName = lastName,
                age = age,
                bloodType = bloodType,
                allergies = allergies,
                conditions = conditions,
                medications = medications,
                emergencyContactName = emergencyContactName,
                emergencyContactNumber = emergencyContactNumber
            )

            dao.saveUser(newUser)
            user.value = newUser
            isUserLoaded.value = true
        }
    }
}