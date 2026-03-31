package com.example.pffbrowser.webview.optimization

import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView

/**
 * WebView缓存优化器
 * 配置缓存策略，提升资源加载速度
 */
object CacheOptimizer {

    /**
     * 配置WebView缓存策略
     */
    fun setupCache(webView: WebView, context: Context) {
        val settings = webView.settings

        // 开启DOM Storage
        settings.domStorageEnabled = true

        // ######### Api过期 #########
        // 开启数据库缓存
//       settings.databaseEnabled = true
        // 开启Application Cache
//        settings.setAppCacheEnabled(true)
//        val appCachePath = context.cacheDir.absolutePath + "/webview_cache"
//        File(appCachePath).mkdirs()
//        settings.setAppCachePath(appCachePath)
//        settings.setAppCacheMaxSize(50 * 1024 * 1024) // 50MB


        // 设置缓存模式
        settings.cacheMode = getCacheMode(context)

        // 允许文件访问
        settings.allowFileAccess = true
        settings.allowContentAccess = true
    }

    /**
     * 根据网络状态获取缓存模式
     */
    private fun getCacheMode(context: Context): Int {
        return if (isNetworkAvailable(context)) {
            // 有网络时，优先使用缓存，缓存过期则从网络加载
            WebSettings.LOAD_DEFAULT
        } else {
            // 无网络时，只使用缓存
            WebSettings.LOAD_CACHE_ELSE_NETWORK
        }
    }

    /**
     * 检查网络是否可用
     */
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as? android.net.ConnectivityManager
        val activeNetwork = connectivityManager?.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }

//    /**
//     * 清除WebView缓存
//     */
//    fun clearCache(webView: WebView, includeDiskFiles: Boolean = false) {
//        webView.clearCache(includeDiskFiles)
//        webView.clearFormData()
//        webView.clearHistory()
//    }

//    /**
//     * 清除所有缓存数据
//     */
//    fun clearAllCache(context: Context) {
//        // 清除缓存目录
//        val cacheDir = File(context.cacheDir, "webview_cache")
//        deleteDirectory(cacheDir)
//
//        // 清除WebView数据
//        context.deleteDatabase("webview.db")
//        context.deleteDatabase("webviewCache.db")
//    }

//    /**
//     * 递归删除目录
//     */
//    private fun deleteDirectory(directory: File): Boolean {
//        if (directory.exists()) {
//            directory.listFiles()?.forEach { file ->
//                if (file.isDirectory) {
//                    deleteDirectory(file)
//                } else {
//                    file.delete()
//                }
//            }
//        }
//        return directory.delete()
//    }

//    /**
//     * 获取缓存大小
//     */
//    fun getCacheSize(context: Context): Long {
//        val cacheDir = File(context.cacheDir, "webview_cache")
//        return getDirectorySize(cacheDir)
//    }
//
//    /**
//     * 计算目录大小
//     */
//    private fun getDirectorySize(directory: File): Long {
//        var size = 0L
//        if (directory.exists()) {
//            directory.listFiles()?.forEach { file ->
//                size += if (file.isDirectory) {
//                    getDirectorySize(file)
//                } else {
//                    file.length()
//                }
//            }
//        }
//        return size
//    }
}
