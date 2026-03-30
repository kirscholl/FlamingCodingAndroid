package com.example.pffbrowser.jsbridge.module.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.pffbrowser.jsbridge.callback.JSCallback
import com.example.pffbrowser.jsbridge.log.JSBridgeLog
import com.example.pffbrowser.jsbridge.model.JSBridgeError
import com.example.pffbrowser.jsbridge.module.IJSBridgeModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * 网络模块
 * 提供网络请求功能
 */
class NetworkModule(private val context: Context) : IJSBridgeModule {

    override fun getModuleName(): String = "network"

    override fun invoke(method: String, params: JSONObject, callback: JSCallback) {
        try {
            when (method) {
                "request" -> request(params, callback)
                "getNetworkType" -> getNetworkType(callback)
                "isNetworkAvailable" -> isNetworkAvailable(callback)
                else -> {
                    callback.onError(JSBridgeError.METHOD_NOT_FOUND, "方法不存在: $method")
                }
            }
        } catch (e: Exception) {
            JSBridgeLog.e("NetworkModule error: ${e.message}", e)
            callback.onError(JSBridgeError.SYSTEM_ERROR, e.message ?: "系统错误")
        }
    }

    /**
     * 发起网络请求
     * params: { url: String, method: String, headers: Object, body: String }
     */
    private fun request(params: JSONObject, callback: JSCallback) {
        val url = params.optString("url", "")
        val method = params.optString("method", "GET")
        val headers = params.optJSONObject("headers")
        val body = params.optString("body", "")

        if (url.isEmpty()) {
            callback.onError(JSBridgeError.PARAM_ERROR, "url不能为空")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = method
                connection.connectTimeout = 30000
                connection.readTimeout = 30000

                // 设置请求头
                headers?.let {
                    it.keys().forEach { key ->
                        connection.setRequestProperty(key, it.getString(key))
                    }
                }

                // 设置请求体
                if (body.isNotEmpty() && (method == "POST" || method == "PUT")) {
                    connection.doOutput = true
                    connection.outputStream.use { os ->
                        os.write(body.toByteArray())
                    }
                }

                // 读取响应
                val responseCode = connection.responseCode
                val inputStream = if (responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

                val response = BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }

                withContext(Dispatchers.Main) {
                    callback.onSuccess(JSONObject().apply {
                        put("statusCode", responseCode)
                        put("data", response)
                    })
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onError(JSBridgeError.NETWORK_ERROR, e.message ?: "网络请求失败")
                }
            }
        }
    }

    /**
     * 获取网络类型
     */
    private fun getNetworkType(callback: JSCallback) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            val type = when {
                capabilities == null -> "none"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "cellular"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ethernet"
                else -> "unknown"
            }

            callback.onSuccess(JSONObject().apply { put("type", type) })
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            val type = when (networkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> "wifi"
                ConnectivityManager.TYPE_MOBILE -> "cellular"
                ConnectivityManager.TYPE_ETHERNET -> "ethernet"
                else -> "none"
            }
            callback.onSuccess(JSONObject().apply { put("type", type) })
        }
    }

    /**
     * 检查网络是否可用
     */
    private fun isNetworkAvailable(callback: JSCallback) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val isAvailable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnected == true
        }

        callback.onSuccess(JSONObject().apply { put("isAvailable", isAvailable) })
    }
}