package com.example.pffbrowser.pbdownload

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PbDownloadInfo(
    val url: String,
    val fileName: String,
    val mimeType: String?,
    val contentLength: Long,
    val contentDisposition: String?
) : Parcelable {
    companion object {
        const val ARG_KEY = "download_info"
    }
}