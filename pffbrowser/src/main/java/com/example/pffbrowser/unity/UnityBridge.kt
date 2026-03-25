package com.example.pffbrowser.unity

import android.app.Activity
import android.os.Handler
import android.os.Looper
import com.unity3d.player.UnityPlayer
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

class UnityBridge private constructor() {
    private var unityActivity: Activity? = null
    private var unityGameObject: String = "AndroidBridge"
    private val mainHandler = Handler(Looper.getMainLooper())
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // 请求映射表（确保回调不丢失）
    private val pendingRequests = ConcurrentHashMap<String, RequestContext>()

    data class RequestContext(
        val requestId: String,
        val methodName: String,
        val deferred: CompletableDeferred<String>,
        val timestamp: Long = System.currentTimeMillis()
    )

    companion object {
        @JvmStatic
        val instance: UnityBridge by lazy { UnityBridge() }

        private const val CALLBACK_METHOD = "OnAndroidCallback"
        private const val TAG = "UnityBridge"
    }

    fun init(activity: Activity, gameObjectName: String) {
        this.unityActivity = activity
        this.unityGameObject = gameObjectName
    }

    /**
     * 异步调用入口（Unity调用）
     */
    fun callAsync(methodName: String, paramsJson: String, requestId: String) {
        // 确保在主线程执行Android API调用
        mainHandler.post {
            coroutineScope.launch(CoroutineExceptionHandler { _, e ->
                sendErrorResponse(requestId, "Coroutine error: ${e.message}")
            }) {
                try {
                    val result = executeBusinessLogic(methodName, paramsJson, requestId)
                    sendSuccessResponse(requestId, result)
                } catch (e: Exception) {
                    sendErrorResponse(requestId, e.message ?: "Unknown error")
                }
            }
        }
    }

    /**
     * 同步调用入口（阻塞式）
     */
    fun callSync(methodName: String, paramsJson: String): String {
        return runBlocking(Dispatchers.Main) {
            try {
                executeBusinessLogic(methodName, paramsJson, "sync_${System.currentTimeMillis()}")
            } catch (e: Exception) {
                JSONObject().apply {
                    put("error", e.message)
                    put("success", false)
                }.toString()
            }
        }
    }

    /**
     * 业务逻辑分发器
     */
    private suspend fun executeBusinessLogic(
        methodName: String,
        params: String,
        requestId: String
    ): String {
//        withContext(Dispatchers.IO) {
//            when (methodName) {
//                "getDeviceId" -> DeviceService.getDeviceId()
//                "requestPermission" -> {
//                    val json = JSONObject(params)
//                    PermissionService.request(
//                        unityActivity!!,
//                        json.getString("permission")
//                    )
//                }
//
//                "getNetworkStatus" -> NetworkService.getStatus(unityActivity!!)
//                // 更多业务方法...
//                else -> throw IllegalArgumentException("Unknown method: $methodName")
//            }
//        }
        return ""
    }


    /**
     * 向Unity发送回调（高可靠性实现）
     */
    fun sendToUnity(method: String, data: String) {
        mainHandler.post {
            try {
                // UnityPlayer.UnitySendMessage必须在主线程调用
                UnityPlayer.UnitySendMessage(unityGameObject, method, data)
            } catch (e: Exception) {
                // 降级方案：尝试延迟重试
                mainHandler.postDelayed({
                    try {
                        UnityPlayer.UnitySendMessage(unityGameObject, method, data)
                    } catch (e2: Exception) {
                        // 记录日志，后续可以通过日志系统补偿
                        android.util.Log.e(TAG, "Failed to send to Unity: $e2")
                    }
                }, 100)
            }
        }
    }

    private fun sendSuccessResponse(requestId: String, data: String) {
        val json = JSONObject().apply {
            put("requestId", requestId)
            put("success", true)
            put("data", data)
        }
        sendToUnity(CALLBACK_METHOD, json.toString())
    }

    private fun sendErrorResponse(requestId: String, error: String) {
        val json = JSONObject().apply {
            put("requestId", requestId)
            put("success", false)
            put("error", error)
        }
        sendToUnity(CALLBACK_METHOD, json.toString())
    }

    fun release() {
        coroutineScope.cancel()
        pendingRequests.clear()
    }
}