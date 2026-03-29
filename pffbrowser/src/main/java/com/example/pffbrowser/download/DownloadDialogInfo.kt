package com.example.pffbrowser.download

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 下载弹窗信息数据类
 * 仅用于展示下载弹窗，不用于实际下载任务
 */
@Parcelize
data class DownloadDialogInfo(
    val url: String,                        // 下载URL
    val fileName: String,                   // 原始文件名
    val mimeType: String?,                  // MIME类型
    val contentLength: Long,                // 文件大小（字节），-1表示未知
    val userAgent: String?,                 // User-Agent
    val contentDisposition: String?         // Content-Disposition头
) : Parcelable
