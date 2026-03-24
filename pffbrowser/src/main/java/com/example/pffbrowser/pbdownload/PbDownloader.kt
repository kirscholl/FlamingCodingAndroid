package com.example.pffbrowser.pbdownload

import android.content.Context
import android.content.Intent
import com.example.pffbrowser.base.PbHiltApplication
import com.example.pffbrowser.pbdownload.service.PbDownloadForegroundService


object PbDownloader {

    const val EXTRA_URL = "extra_url"
    const val EXTRA_FILE_NAME = "extra_file_name"
    const val EXTRA_MIME_TYPE = "extra_mime_type"
    const val EXTRA_CONTENT_LENGTH = "extra_content_length"

    fun statDownload(downloadInfo: PbDownloadInfo, action: PbDownloadAction) {
        startDownloadService(downloadInfo, action)
    }

    fun stopDownload(downloadInfo: PbDownloadInfo, action: PbDownloadAction) {
        startDownloadService(downloadInfo, action)
    }

    fun startDownloadService(downloadInfo: PbDownloadInfo, action: PbDownloadAction) {
        val intent = buildIntent(PbHiltApplication.getInstance(), downloadInfo, action.toString())
        PbHiltApplication.getInstance().startService(intent)
    }

    fun buildIntent(context: Context, downloadInfo: PbDownloadInfo, action: String): Intent {
        return Intent(context, PbDownloadForegroundService::class.java).apply {
            setAction(action)
            putExtra(EXTRA_URL, downloadInfo.url)
            putExtra(EXTRA_FILE_NAME, downloadInfo.fileName)
            putExtra(EXTRA_MIME_TYPE, downloadInfo.mimeType)
            putExtra(EXTRA_CONTENT_LENGTH, downloadInfo.contentLength)
        }
    }

    fun getDownloadInfoFromIntent(intent: Intent): PbDownloadInfo {
        val url = intent.getStringExtra(EXTRA_URL) ?: ""
        val fileName = intent.getStringExtra(EXTRA_FILE_NAME) ?: ""
        val mimeType = intent.getStringExtra(EXTRA_MIME_TYPE) ?: ""
        val contentLength = intent.getLongExtra(EXTRA_CONTENT_LENGTH, 0)
        val downloadInfo = PbDownloadInfo(
            url,
            fileName,
            mimeType,
            contentLength,
            ""
        )
        return downloadInfo
    }
}