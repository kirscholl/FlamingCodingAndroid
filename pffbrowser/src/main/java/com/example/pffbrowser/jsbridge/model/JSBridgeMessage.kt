package com.example.pffbrowser.jsbridge.model

import org.json.JSONObject

/**
 * H5 -> Native 请求消息
 */
data class JSBridgeRequest(
    val callbackId: String,
    val module: String,
    val method: String,
    val params: JSONObject,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromJson(json: String): JSBridgeRequest? {
            return try {
                val obj = JSONObject(json)
                JSBridgeRequest(
                    callbackId = obj.getString("callbackId"),
                    module = obj.getString("module"),
                    method = obj.getString("method"),
                    params = obj.optJSONObject("params") ?: JSONObject(),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

/**
 * Native -> H5 响应消息
 */
data class JSBridgeResponse(
    val callbackId: String,
    val code: Int,
    val message: String,
    val data: Any? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toJson(): String {
        val obj = JSONObject()
        obj.put("callbackId", callbackId)
        obj.put("code", code)
        obj.put("message", message)
        data?.let { obj.put("data", it) }
        obj.put("timestamp", timestamp)
        return obj.toString()
    }
}

/**
 * Native -> H5 事件推送
 */
data class JSBridgeEvent(
    val event: String,
    val data: Any? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toJson(): String {
        val obj = JSONObject()
        obj.put("event", event)
        data?.let { obj.put("data", it) }
        obj.put("timestamp", timestamp)
        return obj.toString()
    }
}
