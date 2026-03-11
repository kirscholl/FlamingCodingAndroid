package com.example.flamingcoding.dagger2Hilt

import javax.inject.Inject

class HiltTest @Inject constructor() {

    fun getHiltTestString(): String {
        return "HiltTest"
    }
}