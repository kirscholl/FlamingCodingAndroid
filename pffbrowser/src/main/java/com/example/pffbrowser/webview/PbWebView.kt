package com.example.pffbrowser.webview

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import com.example.pffbrowser.download.DownloadDialogInfo
import com.example.pffbrowser.jsbridge.core.JSBridgeCore
import com.example.pffbrowser.jsbridge.security.SecurityConfig
import com.example.pffbrowser.utils.FileUtil
import com.example.pffbrowser.webview.optimization.WebViewOptimizationManager

class PbWebView : WebView {

    /**
     * 下载监听器接口
     */
    interface OnDownloadListener {
        fun onDownloadStart(downloadInfo: DownloadDialogInfo)
    }

    var onDownloadListener: OnDownloadListener? = null

    /**
     * JSBridge核心
     */
    private var jsBridgeCore: JSBridgeCore? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        setDownLoaderListener()
        // 初始化JSBridge
        initJSBridge()
        // 应用WebView优化
        applyOptimizations()
    }

    /**
     * 应用WebView优化配置
     */
    private fun applyOptimizations() {
        WebViewOptimizationManager.applyRecommendedOptimizations(this, context)
    }

    /**
     * 初始化JSBridge
     */
    private fun initJSBridge() {
        jsBridgeCore = JSBridgeCore(this)
    }

    /**
     * 获取JSBridge实例
     */
    fun getJSBridge(): JSBridgeCore? = jsBridgeCore

    /**
     * 设置JSBridge安全配置
     */
    fun setJSBridgeSecurityConfig(config: SecurityConfig) {
        jsBridgeCore?.setSecurityConfig(config)
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

    /**
     * 销毁时清理JSBridge
     */
    override fun destroy() {
        jsBridgeCore?.destroy()
        jsBridgeCore = null
        super.destroy()
    }
}
