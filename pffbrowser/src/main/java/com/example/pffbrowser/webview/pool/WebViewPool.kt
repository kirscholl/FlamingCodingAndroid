package com.example.pffbrowser.webview.pool

import android.content.Context
import android.content.MutableContextWrapper
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.example.pffbrowser.webview.PbWebView
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * WebView池化管理器
 * 用于复用WebView实例，减少初始化时间，提升性能
 */
object WebViewPool {

    // 空闲WebView队列
    private val availableWebViews = ConcurrentLinkedQueue<PbWebView>()

    // 使用中的WebView集合
    private val usedWebViews = mutableSetOf<PbWebView>()

    // 最大池大小
    private const val MAX_POOL_SIZE = 3

    // 预创建数量
    private const val PRE_CREATE_SIZE = 2

    // 是否已初始化
    private var isInitialized = false

    // Application Context
    private lateinit var applicationContext: Context

    /**
     * 初始化WebView池
     * 建议在Application.onCreate()中调用
     */
    fun init(context: Context) {
        if (isInitialized) return

        applicationContext = context.applicationContext
        isInitialized = true

        // 预创建WebView
        preCreateWebViews()
    }

    /**
     * 预创建WebView实例
     */
    private fun preCreateWebViews() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            for (i in 0 until PRE_CREATE_SIZE) {
                createWebView()
            }
        } else {
            Handler(Looper.getMainLooper()).post {
                for (i in 0 until PRE_CREATE_SIZE) {
                    createWebView()
                }
            }
        }
    }

    /**
     * 创建新的WebView实例
     */
    private fun createWebView(): PbWebView? {
        return try {
            val contextWrapper = MutableContextWrapper(applicationContext)
            val webView = PbWebView(contextWrapper)

            // 配置WebView基础设置
            configureWebView(webView)

            availableWebViews.offer(webView)
            webView
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 配置WebView基础设置
     */
    private fun configureWebView(webView: PbWebView) {
        webView.apply {
            // 移除父容器
            (parent as? ViewGroup)?.removeView(this)
            // 清空历史记录
            clearHistory()
            // 清空缓存（不清除持久化缓存）
            clearFormData()
        }
    }

    /**
     * 从池中获取WebView
     * @param context 当前Activity的Context
     */
    fun obtain(context: Context): PbWebView {
        checkInitialized()

        var webView = availableWebViews.poll()

        if (webView == null) {
            // 池中没有可用的，创建新的
            val contextWrapper = MutableContextWrapper(context)
            webView = PbWebView(contextWrapper)
            configureWebView(webView)
        } else {
            // 更新Context
            val contextWrapper = webView.context as? MutableContextWrapper
            contextWrapper?.baseContext = context
        }

        synchronized(usedWebViews) {
            usedWebViews.add(webView)
        }

        return webView
    }

    /**
     * 回收WebView到池中
     * @param webView 需要回收的WebView
     */
    fun recycle(webView: PbWebView?) {
        if (webView == null) return

        synchronized(usedWebViews) {
            usedWebViews.remove(webView)
        }

        // 清理WebView状态
        cleanWebView(webView)

        // 如果池未满，回收到池中
        if (availableWebViews.size < MAX_POOL_SIZE) {
            availableWebViews.offer(webView)
        } else {
            // 池已满，销毁WebView
            destroyWebView(webView)
        }

        // 如果池中WebView不足，补充新的
        if (availableWebViews.size < PRE_CREATE_SIZE) {
            createWebView()
        }
    }

    /**
     * 清理WebView状态
     */
    private fun cleanWebView(webView: PbWebView) {
        try {
            webView.apply {
                // 停止加载
                stopLoading()

                // 清空回调（设置为新的空实例，避免持有外部引用）
                webViewClient = WebViewClient()
                webChromeClient = null
                onDownloadListener = null

                // 移除所有View
                removeAllViews()

                // 清空历史
                clearHistory()

                // 清理JSBridge
                getJSBridge()?.destroy()

                // 加载空白页
                loadUrl("about:blank")
            }

            // 重置Context为applicationContext，避免持有Activity引用
            val contextWrapper = webView.context as? MutableContextWrapper
            contextWrapper?.baseContext = applicationContext
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 销毁WebView
     */
    private fun destroyWebView(webView: PbWebView?) {
        try {
            webView?.apply {
                (parent as? ViewGroup)?.removeView(this)
                removeAllViews()
                destroy()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清空池中所有WebView
     */
    fun clear() {
        availableWebViews.forEach { destroyWebView(it) }
        availableWebViews.clear()

        synchronized(usedWebViews) {
            usedWebViews.forEach { destroyWebView(it) }
            usedWebViews.clear()
        }
    }

    /**
     * 检查是否已初始化
     */
    private fun checkInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("WebViewPool未初始化，请先调用init()方法")
        }
    }

    /**
     * 获取池状态信息
     */
    fun getPoolStatus(): String {
        return "可用: ${availableWebViews.size}, 使用中: ${usedWebViews.size}"
    }
}
