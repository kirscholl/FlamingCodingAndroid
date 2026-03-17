package com.example.pffbrowser.webview

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView

class PbWebView : WebView {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        setDownLoaderListener()
    }

    fun setDownLoaderListener() {
        this.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            // 在这里处理下载逻辑
            // 1. 通过 mimeType 判断文件类型（例如 PDF、图片、APK 等）
            if (mimeType != null) {
                when {
                    mimeType == "application/pdf" -> {
                        // 处理 PDF 下载或使用 Google Docs 预览 [citation:2]
                    }

                    mimeType.startsWith("image/") -> {
                        // 处理图片下载
                    }
                    // 其他类型判断...
                }
            }
            // 2. 从 contentDisposition 或 URL 中提取文件名 [citation:1][citation:6]
            val filename = extractFileName(contentDisposition, url)
            // showDialog
            // 3. 启动下载 (例如使用 DownloadManager)
//            startDownload(url, filename, mimeType)
        }
    }

    // 辅助函数：提取文件名
    fun extractFileName(contentDisposition: String?, url: String): String {
        return if (contentDisposition != null && contentDisposition.contains("filename=")) {
            // 从 Content-Disposition 中解析文件名 (例如: attachment; filename="example.pdf") [citation:1][citation:8]
            contentDisposition.substringAfter("filename=").replace("\"", "")
        } else {
            // 备选方案：从 URL 路径中截取最后一段
            url.substringAfterLast("/")
        }
    }
}