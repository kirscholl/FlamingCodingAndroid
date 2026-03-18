package com.example.pffbrowser.download

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 下载信息数据类
 * 用于在 Fragment 之间传递下载任务信息
 */
@Parcelize
data class DownloadInfo(
    val url: String,
    val fileName: String,
    val mimeType: String?,
    val contentLength: Long,
    val userAgent: String?,
    val contentDisposition: String?
) : Parcelable {
    companion object {
        const val ARG_KEY = "download_info"
    }
}
