package com.example.flamingcoding.lifecycleViewModelLiveDataTrials

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TestViewModel : ViewModel() {
    val testLiveData = MutableLiveData<String>()
}