package com.example.flamingcoding.firstlinecode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PracticeViewModelFactory(private val countReserved: Int) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PracticeViewModel(countReserved) as T
    }
}