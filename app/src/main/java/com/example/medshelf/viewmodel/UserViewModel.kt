package com.example.medshelf.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.medshelf.data.UserDao
import com.example.medshelf.model.UserEntity
import kotlinx.coroutines.launch

class UserViewModel(
    private val userDao: UserDao
) : ViewModel() {

    val user = mutableStateOf<UserEntity?>(null)
    val isUserLoaded = mutableStateOf(false)

    fun loadUser() {
        viewModelScope.launch {
            userDao.getUser().collect { savedUser ->
                user.value = savedUser
                isUserLoaded.value = true
            }
        }
    }

    fun saveUser(
        firstName: String,
        lastName: String,
        age: Int,
        birthday: String,
        sex: String,
        address: String,
        bloodType: String,
        allergies: String,
        conditions: String,
        medications: String,
        importantNote: String,
        emergencyContactName: String,
        emergencyContactNumber: String
    ) {
        viewModelScope.launch {

            val newUser = UserEntity(
                id = 1,

                firstName = firstName,
                lastName = lastName,
                age = age,
                birthday = birthday,
                sex = sex,
                address = address,

                bloodType = bloodType,
                allergies = allergies,
                conditions = conditions,
                medications = medications,

                importantNote = importantNote,

                emergencyContactName = emergencyContactName,
                emergencyContactNumber = emergencyContactNumber
            )

            userDao.saveUser(newUser)

            user.value = newUser
            isUserLoaded.value = true
        }
    }
}

class UserViewModelFactory(
    private val userDao: UserDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userDao) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}