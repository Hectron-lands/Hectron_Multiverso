package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase

class UniverseViewModelFactory(private val database: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UniverseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UniverseViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
