package com.example.medshelf.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medshelf.data.FamilyMemberDao
import com.example.medshelf.model.FamilyMemberEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FamilyMemberViewModel(
    private val familyMemberDao: FamilyMemberDao
) : ViewModel() {

    val familyMembers = familyMemberDao
        .getAllFamilyMembers()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun addFamilyMember(
        firstName: String,
        lastName: String,
        relationship: String,
        age: Int?,
        sex: String,
        bloodType: String,
        allergies: String,
        conditions: String,
        medications: String,
        emergencyContactName: String,
        emergencyContactNumber: String,
        notes: String
    ) {
        viewModelScope.launch {
            familyMemberDao.insertFamilyMember(
                FamilyMemberEntity(
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    relationship = relationship.trim(),
                    age = age,
                    sex = sex.ifBlank { "Not set" },
                    bloodType = bloodType.ifBlank { "Not set" },
                    allergies = allergies.ifBlank { "None" },
                    conditions = conditions.ifBlank { "None" },
                    medications = medications.ifBlank { "None" },
                    emergencyContactName = emergencyContactName.ifBlank { "Not set" },
                    emergencyContactNumber = emergencyContactNumber.ifBlank { "Not set" },
                    notes = notes.ifBlank { "None" }
                )
            )
        }
    }

    fun updateFamilyMember(member: FamilyMemberEntity) {
        viewModelScope.launch {
            familyMemberDao.updateFamilyMember(member)
        }
    }

    fun deleteFamilyMember(member: FamilyMemberEntity) {
        viewModelScope.launch {
            familyMemberDao.deleteFamilyMember(member)
        }
    }

    fun deleteFamilyMemberById(id: Int) {
        viewModelScope.launch {
            familyMemberDao.deleteFamilyMemberById(id)
        }
    }
}