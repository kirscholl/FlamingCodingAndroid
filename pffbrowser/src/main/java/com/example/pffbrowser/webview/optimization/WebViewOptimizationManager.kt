package com.example.pffbrowser.webview.optimization

import android.content.Context
import com.example.pffbrowser.webview.PbWebView

/**
 * WebView优化管理器
 * 统一管理所有WebView优化功能
 */
object WebViewOptimizationManager {

    /**
     * 应用所有优化配置
     * @param webView WebView实例
     * @param context Context
     * @param config 优化配置
     */
    fun applyAllOptimizations(
        webView: PbWebView,
        context: Context,
        config: OptimizationConfig = OptimizationConfig()
    ) {
        // 1. 缓存优化
        if (config.enableCache) {
            CacheOptimizer.setupCache(webView, context)
        }

        // 2. 渲染优化
        if (config.enableRenderOptimization) {
            RenderOptimizer.setupRenderOptimization(webView)
            RenderOptimizer.setupViewportOptimization(webView.settings)
            RenderOptimizer.setupJavaScriptOptimization(webView.settings)
            RenderOptimizer.setupMixedContentMode(webView.settings)
        }

        // 3. 图片加载优化
        if (config.enableImageOptimization) {
            ImageLoadOptimizer.setupImageLoadOptimization(
                webView,
                config.autoLoadImages
            )
        }

        // 4. DNS预解析
        if (config.enableDnsPreResolve) {
            DnsPreResolver.setupWebViewDnsPreResolve(webView, context)
        }
    }

    /**
     * 快速配置（推荐配置）
     */
    fun applyRecommendedOptimizations(webView: PbWebView, context: Context) {
        applyAllOptimizations(
            webView,
            context,
            OptimizationConfig(
                enableCache = true,
                enableRenderOptimization = true,
                enableImageOptimization = true,
                autoLoadImages = true,
                enableDnsPreResolve = true
            )
        )
    }
}

/**
 * 优化配置类
 */
data class OptimizationConfig(
    val enableCache: Boolean = true,
    val enableRenderOptimization: Boolean = true,
    val enableImageOptimization: Boolean = true,
    val autoLoadImages: Boolean = true,
    val enableDnsPreResolve: Boolean = true
)