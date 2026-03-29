package com.example.pffbrowser.webview

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import com.example.pffbrowser.download.DownloadDialogInfo
import com.example.pffbrowser.utils.FileUtil

class PbWebView : WebView {

    /**
     * 下载监听器接口
     */
    interface OnDownloadListener {
        fun onDownloadStart(downloadInfo: DownloadDialogInfo)
    }

    var onDownloadListener: OnDownloadListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        setDownLoaderListener()
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }
    }

    private fun setDownLoaderListener() {
        this.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            // 提取文件名
            val fileName = FileUtil.extractFileName(contentDisposition, url)

            // 创建下载信息
            val downloadInfo = DownloadDialogInfo(
                url = url,
                fileName = fileName,
                mimeType = mimeType,
                contentLength = contentLength,
                userAgent = userAgent,
                contentDisposition = contentDisposition
            )

            // 回调到Fragment
            onDownloadListener?.onDownloadStart(downloadInfo)
        }
    }
}
