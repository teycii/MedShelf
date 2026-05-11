package com.example.medshelf.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val firstName: String,
    val lastName: String,

    // User types this manually: Mother, Father, Child, Guardian, Patient, etc.
    val relationship: String,

    val age: Int?,
    val sex: String,
    val bloodType: String,

    val allergies: String,
    val conditions: String,
    val medications: String,

    val emergencyContactName: String,
    val emergencyContactNumber: String,

    val notes: String,

    val createdAt: Long = System.currentTimeMillis()
) {
    val fullName: String
        get() = "$firstName $lastName".trim()

    val displayLabel: String
        get() = if (relationship.isBlank()) fullName else "$fullName • $relationship"
}