package com.example.pffbrowser.jsbridge.core

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.example.pffbrowser.jsbridge.callback.CallbackManager
import com.example.pffbrowser.jsbridge.callback.JSCallback
import com.example.pffbrowser.jsbridge.log.JSBridgeLog
import com.example.pffbrowser.jsbridge.model.JSBridgeError
import com.example.pffbrowser.jsbridge.model.JSBridgeEvent
import com.example.pffbrowser.jsbridge.model.JSBridgeRequest
import com.example.pffbrowser.jsbridge.model.JSBridgeResponse
import com.example.pffbrowser.jsbridge.module.IJSBridgeModule
import com.example.pffbrowser.jsbridge.security.SecurityConfig
import java.util.concurrent.ConcurrentHashMap

/**
 * JSBridge核心类
 * 负责消息分发、回调管理、安全校验
 */
class JSBridgeCore(
    private val webView: WebView,
    private var securityConfig: SecurityConfig = SecurityConfig()
) {
    private val modules = ConcurrentHashMap<String, IJSBridgeModule>()
    private val callbackManager = CallbackManager()

    companion object {
        private const val JS_BRIDGE_NAME = "AndroidJSBridge"
        private const val JS_CALLBACK_FUNCTION = "window._jsbridge_callback"
        private const val JS_EVENT_FUNCTION = "window._jsbridge_event"
    }

    init {
        // 注入JSBridge到WebView
        webView.addJavascriptInterface(this, JS_BRIDGE_NAME)
        JSBridgeLog.i("JSBridge initialized")
    }

    /**
     * 处理来自H5的调用
     */
    @JavascriptInterface
    fun call(message: String): String? {
        JSBridgeLog.d("Received call: $message")

        // 检查URL安全性
        val url = webView.url
        if (!securityConfig.isUrlAllowed(url)) {
            JSBridgeLog.w("URL not allowed: $url")
            return createErrorResponse("", JSBridgeError.PERMISSION_DENIED, "域名不在白名单中")
        }

        // 解析请求
        val request = JSBridgeRequest.fromJson(message)
        if (request == null) {
            JSBridgeLog.e("Failed to parse request: $message")
            return createErrorResponse("", JSBridgeError.PARAM_ERROR, "请求格式错误")
        }

        JSBridgeLog.logCall(request.module, request.method, request.params.toString())

        // 查找模块
        val module = modules[request.module]
        if (module == null) {
            JSBridgeLog.e("Module not found: ${request.module}")
            sendErrorResponse(request.callbackId, JSBridgeError.MODULE_NOT_FOUND, "模块不存在")
            return null
        }

        // 创建回调
        val callback = object : JSCallback {
            override fun onSuccess(data: Any?) {
                sendSuccessResponse(request.callbackId, data)
            }

            override fun onError(code: Int, message: String) {
                sendErrorResponse(request.callbackId, code, message)
            }

            override fun onProgress(progress: Int) {
                // 进度回调暂不处理
            }
        }

        // 调用模块方法
        try {
            module.invoke(request.method, request.params, callback)
        } catch (e: Exception) {
            JSBridgeLog.e("Failed to invoke method: ${request.method}", e)
            sendErrorResponse(request.callbackId, JSBridgeError.SYSTEM_ERROR, e.message ?: "系统错误")
        }

        return null
    }

    /**
     * 注册模块
     */
    fun registerModule(module: IJSBridgeModule) {
        modules[module.getModuleName()] = module
        JSBridgeLog.i("Module registered: ${module.getModuleName()}")
    }

    /**
     * 取消注册模块
     */
    fun unregisterModule(moduleName: String) {
        modules.remove(moduleName)
        JSBridgeLog.i("Module unregistered: $moduleName")
    }

    /**
     * 设置安全配置
     */
    fun setSecurityConfig(config: SecurityConfig) {
        this.securityConfig = config
        JSBridgeLog.i("Security config updated")
    }

    /**
     * 向H5发送事件
     */
    fun sendEventToJs(event: String, data: Any?) {
        val eventObj = JSBridgeEvent(event, data)
        val js = "$JS_EVENT_FUNCTION(${eventObj.toJson()})"
        webView.post {
            webView.evaluateJavascript(js, null)
        }
        JSBridgeLog.d("Event sent: $event")
    }

    /**
     * 发送成功响应
     */
    private fun sendSuccessResponse(callbackId: String, data: Any?) {
        val response = JSBridgeResponse(
            callbackId = callbackId,
            code = JSBridgeError.SUCCESS,
            message = "success",
            data = data
        )
        sendResponse(response)
    }

    /**
     * 发送错误响应
     */
    private fun sendErrorResponse(callbackId: String, code: Int, message: String) {
        val response = JSBridgeResponse(
            callbackId = callbackId,
            code = code,
            message = message
        )
        sendResponse(response)
    }

    /**
     * 发送响应到H5
     */
    private fun sendResponse(response: JSBridgeResponse) {
        JSBridgeLog.logResponse(response.callbackId, response.code, response.message, response.data)
        val js = "$JS_CALLBACK_FUNCTION(${response.toJson()})"
        webView.post {
            webView.evaluateJavascript(js, null)
        }
    }

    /**
     * 创建错误响应（同步返回）
     */
    private fun createErrorResponse(callbackId: String, code: Int, message: String): String {
        return JSBridgeResponse(callbackId, code, message).toJson()
    }

    /**
     * 销毁
     */
    fun destroy() {
        callbackManager.clear()
        modules.clear()
        JSBridgeLog.i("JSBridge destroyed")
    }
}
