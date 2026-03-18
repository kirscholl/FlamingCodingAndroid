package com.example.pffbrowser.base

import android.app.Application
import com.example.pffbrowser.download.OkDownloadManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
open class PbHiltApplication : Application() {

    @Inject
    lateinit var downloadManager: OkDownloadManager

    override fun onCreate() {
        super.onCreate()
        // 初始化 OkDownload 下载管理器
        downloadManager.init()
    }
}
