package com.example.pffbrowser.base

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class PbHiltApplication : Application() {
    companion object {
        private var _instance: PbHiltApplication? = null

        fun getInstance(): PbHiltApplication {
            return _instance!!
        }

        fun getApplicationContext(): Context {
            return _instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        _instance = this
    }
}
