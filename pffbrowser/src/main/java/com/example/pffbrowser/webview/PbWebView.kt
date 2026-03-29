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
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }
    }

    private fun setDownLoaderListener() {
        this.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->

        }
    }
}