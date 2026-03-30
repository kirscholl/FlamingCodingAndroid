package com.example.pffbrowser.jsbridge.module.impl

import android.content.Context
import android.content.SharedPreferences
import com.example.pffbrowser.jsbridge.callback.JSCallback
import com.example.pffbrowser.jsbridge.log.JSBridgeLog
import com.example.pffbrowser.jsbridge.model.JSBridgeError
import com.example.pffbrowser.jsbridge.module.IJSBridgeModule
import org.json.JSONObject

/**
 * 存储模块
 * 提供本地存储功能
 */
class StorageModule(context: Context) : IJSBridgeModule {

    private val prefs: SharedPreferences = context.getSharedPreferences("jsbridge_storage", Context.MODE_PRIVATE)

    override fun getModuleName(): String = "storage"

    override fun invoke(method: String, params: JSONObject, callback: JSCallback) {
        try {
            when (method) {
                "setItem" -> setItem(params, callback)
                "getItem" -> getItem(params, callback)
                "removeItem" -> removeItem(params, callback)
                "clear" -> clear(callback)
                "getAllKeys" -> getAllKeys(callback)
                else -> {
                    callback.onError(JSBridgeError.METHOD_NOT_FOUND, "方法不存在: $method")
                }
            }
        } catch (e: Exception) {
            JSBridgeLog.e("StorageModule error: ${e.message}", e)
            callback.onError(JSBridgeError.SYSTEM_ERROR, e.message ?: "系统错误")
        }
    }

    /**
     * 存储数据
     * params: { key: String, value: String }
     */
    private fun setItem(params: JSONObject, callback: JSCallback) {
        val key = params.optString("key", "")
        val value = params.optString("value", "")

        if (key.isEmpty()) {
            callback.onError(JSBridgeError.PARAM_ERROR, "key不能为空")
            return
        }

        prefs.edit().putString(key, value).apply()
        callback.onSuccess(null)
    }

    /**
     * 获取数据
     * params: { key: String }
     */
    private fun getItem(params: JSONObject, callback: JSCallback) {
        val key = params.optString("key", "")

        if (key.isEmpty()) {
            callback.onError(JSBridgeError.PARAM_ERROR, "key不能为空")
            return
        }

        val value = prefs.getString(key, null)
        callback.onSuccess(JSONObject().apply { put("value", value) })
    }

    /**
     * 删除数据
     * params: { key: String }
     */
    private fun removeItem(params: JSONObject, callback: JSCallback) {
        val key = params.optString("key", "")

        if (key.isEmpty()) {
            callback.onError(JSBridgeError.PARAM_ERROR, "key不能为空")
            return
        }

        prefs.edit().remove(key).apply()
        callback.onSuccess(null)
    }

    /**
     * 清空所有数据
     */
    private fun clear(callback: JSCallback) {
        prefs.edit().clear().apply()
        callback.onSuccess(null)
    }

    /**
     * 获取所有key
     */
    private fun getAllKeys(callback: JSCallback) {
        val keys = prefs.all.keys.toList()
        val jsonArray = org.json.JSONArray().apply {
            keys.forEach { put(it) }
        }
        callback.onSuccess(JSONObject().apply { put("keys", jsonArray) })
    }
}