package com.example.medshelf.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey
    val id: Int = 1,

    val firstName: String,
    val lastName: String,
    val age: Int,
    val bloodType: String,
    val allergies: String,
    val conditions: String,
    val medications: String,
    val emergencyContactName: String,
    val emergencyContactNumber: String
)