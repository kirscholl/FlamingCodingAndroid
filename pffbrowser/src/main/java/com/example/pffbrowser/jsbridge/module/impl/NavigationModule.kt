package com.example.pffbrowser.jsbridge.module.impl

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import com.example.pffbrowser.jsbridge.callback.JSCallback
import com.example.pffbrowser.jsbridge.log.JSBridgeLog
import com.example.pffbrowser.jsbridge.model.JSBridgeError
import com.example.pffbrowser.jsbridge.module.IJSBridgeModule
import org.json.JSONObject

/**
 * 导航模块
 * 提供页面导航功能
 */
class NavigationModule(
    private val activity: Activity,
    private val webView: WebView
) : IJSBridgeModule {

    override fun getModuleName(): String = "navigation"

    override fun invoke(method: String, params: JSONObject, callback: JSCallback) {
        try {
            when (method) {
                "push" -> push(params, callback)
                "pop" -> pop(callback)
                "reload" -> reload(callback)
                "openNewWindow" -> openNewWindow(params, callback)
                "close" -> close(callback)
                else -> {
                    callback.onError(JSBridgeError.METHOD_NOT_FOUND, "方法不存在: $method")
                }
            }
        } catch (e: Exception) {
            JSBridgeLog.e("NavigationModule error: ${e.message}", e)
            callback.onError(JSBridgeError.SYSTEM_ERROR, e.message ?: "系统错误")
        }
    }

    /**
     * 加载新页面
     * params: { url: String }
     */
    private fun push(params: JSONObject, callback: JSCallback) {
        val url = params.optString("url", "")

        if (url.isEmpty()) {
            callback.onError(JSBridgeError.PARAM_ERROR, "url不能为空")
            return
        }

        webView.loadUrl(url)
        callback.onSuccess(null)
    }

    /**
     * 返回上一页
     */
    private fun pop(callback: JSCallback) {
        if (webView.canGoBack()) {
            webView.goBack()
            callback.onSuccess(null)
        } else {
            callback.onError(JSBridgeError.SYSTEM_ERROR, "无法返回")
        }
    }

    /**
     * 刷新当前页面
     */
    private fun reload(callback: JSCallback) {
        webView.reload()
        callback.onSuccess(null)
    }

    /**
     * 在新窗口打开URL
     * params: { url: String }
     */
    private fun openNewWindow(params: JSONObject, callback: JSCallback) {
        val url = params.optString("url", "")

        if (url.isEmpty()) {
            callback.onError(JSBridgeError.PARAM_ERROR, "url不能为空")
            return
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            activity.startActivity(intent)
            callback.onSuccess(null)
        } catch (e: Exception) {
            callback.onError(JSBridgeError.SYSTEM_ERROR, "打开失败: ${e.message}")
        }
    }

    /**
     * 关闭当前页面
     */
    private fun close(callback: JSCallback) {
        activity.finish()
        callback.onSuccess(null)
    }
}