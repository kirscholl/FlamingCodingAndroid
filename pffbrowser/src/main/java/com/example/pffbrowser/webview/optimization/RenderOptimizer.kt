package com.example.pffbrowser.webview.optimization

import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView

/**
 * WebView渲染性能优化器
 * 配置硬件加速、分层渲染等，提升渲染性能
 */
object RenderOptimizer {

    /**
     * 配置WebView渲染优化
     */
    fun setupRenderOptimization(webView: WebView) {
        val settings = webView.settings

        // 开启硬件加速
        enableHardwareAcceleration(webView)

        // 开启分层渲染
        enableLayerType(webView)

        // 优化渲染设置
        optimizeRenderSettings(settings)

        // 优化滚动性能
        optimizeScrolling(webView)
    }

    /**
     * 开启硬件加速
     */
    private fun enableHardwareAcceleration(webView: WebView) {
        // Android 5.0+ 默认开启硬件加速
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    /**
     * 配置分层渲染
     */
    private fun enableLayerType(webView: WebView) {
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        // 根据Android版本选择最优的Layer类型
//        when {
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
//                webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
//            }
//
//            else -> {
//                webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
//            }
//        }
    }

    /**
     * 优化渲染设置
     */
    private fun optimizeRenderSettings(settings: WebSettings) {
        // 开启渲染优先级
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH)

        // 关闭不必要的功能以提升性能
        settings.setSupportZoom(false)
        settings.builtInZoomControls = false
        settings.displayZoomControls = false
    }

    /**
     * 优化滚动性能
     */
    private fun optimizeScrolling(webView: WebView) {
        // 开启滚动条淡出效果
        webView.isScrollbarFadingEnabled = true
        // 优化过度滚动模式
        webView.overScrollMode = View.OVER_SCROLL_NEVER
    }

    /**
     * 配置视口和布局优化
     */
    fun setupViewportOptimization(settings: WebSettings) {
        // 自适应屏幕
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true

        // 支持viewport meta标签
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL

        // 设置默认字体大小
        settings.defaultFontSize = 16
        settings.minimumFontSize = 12
    }

    /**
     * 配置JavaScript执行优化
     */
    fun setupJavaScriptOptimization(settings: WebSettings) {
        // 开启JavaScript
        settings.javaScriptEnabled = true

        // 允许JavaScript打开新窗口
        settings.javaScriptCanOpenWindowsAutomatically = true

        // 开启多窗口支持
        settings.setSupportMultipleWindows(false)
    }

    /**
     * 配置混合内容模式（HTTPS页面加载HTTP资源）
     */
    fun setupMixedContentMode(settings: WebSettings) {
        // 允许混合内容，提升兼容性
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }

    /**
     * 禁用不必要的功能以提升性能
     */
    fun disableUnnecessaryFeatures(settings: WebSettings) {
        // 禁用地理定位
        settings.setGeolocationEnabled(false)
        // 禁用保存密码
        settings.savePassword = false
        // 禁用保存表单数据
        settings.saveFormData = false
    }
}
