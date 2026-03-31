package com.example.pffbrowser.webview.optimization

import android.webkit.WebSettings
import android.webkit.WebView

/**
 * WebView图片加载优化器
 * 优化图片加载策略，提升页面加载速度
 */
object ImageLoadOptimizer {

    /**
     * 配置图片加载优化
     * @param webView WebView实例
     * @param autoLoadImages 是否自动加载图片
     */
    fun setupImageLoadOptimization(
        webView: WebView,
        autoLoadImages: Boolean = true
    ) {
        val settings = webView.settings

        if (autoLoadImages) {
            // 自动加载图片
            enableAutoLoadImages(settings)
        } else {
            // 延迟加载图片，先加载文本内容
//            enableManualLoadImages(webView, settings)
        }
    }

    /**
     * 开启自动加载图片
     */
    private fun enableAutoLoadImages(settings: WebSettings) {
        settings.loadsImagesAutomatically = true
        settings.blockNetworkImage = false
    }

    /**
     * 开启手动加载图片（先加载文本，后加载图片）
     */
    private fun enableManualLoadImages(webView: WebView, settings: WebSettings) {
        // 先不加载图片
//        settings.loadsImagesAutomatically = false
//        settings.blockNetworkImage = true
//
//        // 监听页面加载完成后再加载图片
//        webView.setOnPageFinishedListener {
//            settings.loadsImagesAutomatically = true
//            settings.blockNetworkImage = false
//        }
    }

    /**
     * 根据网络状态配置图片加载策略
     */
    fun setupImageLoadByNetwork(
        webView: WebView,
        isWifi: Boolean
    ) {
        val settings = webView.settings

        if (isWifi) {
            // WiFi环境下自动加载图片
            settings.loadsImagesAutomatically = true
            settings.blockNetworkImage = false
        } else {
            // 移动网络下不自动加载图片
            settings.loadsImagesAutomatically = false
            settings.blockNetworkImage = true
        }
    }

    /**
     * 手动触发加载图片
     */
    fun loadImages(webView: WebView) {
        val settings = webView.settings
        settings.loadsImagesAutomatically = true
        settings.blockNetworkImage = false
        webView.reload()
    }

    /**
     * 配置图片质量和压缩
     */
    fun setupImageQuality(settings: WebSettings) {
        // 这些设置需要在WebViewClient中通过拦截资源请求实现
        // 此处仅作为配置接口预留
    }
}

/**
 * WebView扩展函数：设置页面加载完成监听
 */
//private fun WebView.setOnPageFinishedListener(listener: () -> Unit) {
//    // 版本判断
//    val originalClient = webViewClient
//    webViewClient = object : android.webkit.WebViewClient() {
//        override fun onPageFinished(view: WebView?, url: String?) {
//            super.onPageFinished(view, url)
//            listener()
//            // 恢复原有的WebViewClient
//            if (originalClient != null) {
//                webViewClient = originalClient
//            }
//        }
//    }
//}
