package com.example.medshelf.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medshelf.data.FamilyMemberDao

class FamilyMemberViewModelFactory(
    private val familyMemberDao: FamilyMemberDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FamilyMemberViewModel::class.java)) {
            return FamilyMemberViewModel(familyMemberDao) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}