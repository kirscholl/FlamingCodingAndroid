package com.example.pffbrowser.base

import android.app.Application
import android.content.Context
import com.example.pffbrowser.download.OkDownloadManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

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

    @Inject
    lateinit var downloadManager: OkDownloadManager

    override fun onCreate() {
        super.onCreate()
        _instance = this
        // 初始化 OkDownload 下载管理器
        downloadManager.init()
    }
}
