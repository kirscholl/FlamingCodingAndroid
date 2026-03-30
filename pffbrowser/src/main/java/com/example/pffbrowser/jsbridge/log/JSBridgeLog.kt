package com.example.pffbrowser.jsbridge.log

import android.util.Log

/**
 * JSBridge日志工具
 */
object JSBridgeLog {
    private const val TAG = "JSBridge"
    var isDebugMode = true

    fun d(message: String) {
        if (isDebugMode) {
            Log.d(TAG, message)
        }
    }

    fun i(message: String) {
        if (isDebugMode) {
            Log.i(TAG, message)
        }
    }

    fun w(message: String) {
        if (isDebugMode) {
            Log.w(TAG, message)
        }
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (isDebugMode) {
            if (throwable != null) {
                Log.e(TAG, message, throwable)
            } else {
                Log.e(TAG, message)
            }
        }
    }

    /**
     * 记录调用链路
     */
    fun logCall(module: String, method: String, params: String) {
        d("Call -> module: $module, method: $method, params: $params")
    }

    /**
     * 记录响应
     */
    fun logResponse(callbackId: String, code: Int, message: String, data: Any?) {
        d("Response -> callbackId: $callbackId, code: $code, message: $message, data: $data")
    }
}
