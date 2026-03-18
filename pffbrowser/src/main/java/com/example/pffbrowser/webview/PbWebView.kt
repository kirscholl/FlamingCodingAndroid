package com.example.pffbrowser.webview

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.webkit.WebView
import androidx.navigation.findNavController
import com.example.pffbrowser.R
import com.example.pffbrowser.download.DownloadInfo
import com.example.pffbrowser.utils.FileUtil

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
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }
    }

    private fun setDownLoaderListener() {
        this.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            // 提取文件名
            val fileName = FileUtil.extractFileName(contentDisposition, url)
            // 创建下载信息对象
            val downloadInfo = DownloadInfo(
                url = url,
                fileName = fileName,
                mimeType = mimeType,
                contentLength = contentLength,
                userAgent = userAgent,
                contentDisposition = contentDisposition
            )
            // 使用 Navigation 跳转到下载弹窗
            val bundle = Bundle().apply {
                putParcelable(DownloadInfo.ARG_KEY, downloadInfo)
            }
            findNavController().navigate(
                R.id.action_global_to_downloaddialogfragment,
                bundle
            )
        }
    }
}