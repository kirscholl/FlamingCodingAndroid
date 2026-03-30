package com.example.pffbrowser.jsbridge.callback

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ConcurrentHashMap

/**
 * 回调管理器
 * 负责管理所有的回调，包括超时清理
 */
class CallbackManager(
    private val timeout: Long = 30_000L  // 默认30秒超时
) {
    private val callbacks = ConcurrentHashMap<String, CallbackWrapper>()
    private val handler = Handler(Looper.getMainLooper())

    /**
     * 添加回调
     */
    fun addCallback(callbackId: String, callback: JSCallback) {
        val wrapper = CallbackWrapper(callback, System.currentTimeMillis())
        callbacks[callbackId] = wrapper
        scheduleCleanup(callbackId)
    }

    /**
     * 执行成功回调
     */
    fun invokeSuccess(callbackId: String, data: Any?) {
        callbacks.remove(callbackId)?.let { wrapper ->
            handler.post {
                wrapper.callback.onSuccess(data)
            }
        }
    }

    /**
     * 执行失败回调
     */
    fun invokeError(callbackId: String, code: Int, message: String) {
        callbacks.remove(callbackId)?.let { wrapper ->
            handler.post {
                wrapper.callback.onError(code, message)
            }
        }
    }

    /**
     * 执行进度回调
     */
    fun invokeProgress(callbackId: String, progress: Int) {
        callbacks[callbackId]?.let { wrapper ->
            handler.post {
                wrapper.callback.onProgress(progress)
            }
        }
    }

    /**
     * 移除回调
     */
    fun removeCallback(callbackId: String) {
        callbacks.remove(callbackId)
    }

    /**
     * 清理所有回调
     */
    fun clear() {
        callbacks.clear()
        handler.removeCallbacksAndMessages(null)
    }

    /**
     * 调度超时清理
     */
    private fun scheduleCleanup(callbackId: String) {
        handler.postDelayed({
            callbacks.remove(callbackId)?.let {
                it.callback.onError(1004, "请求超时")
            }
        }, timeout)
    }

    /**
     * 回调包装类
     */
    private data class CallbackWrapper(
        val callback: JSCallback,
        val createTime: Long
    )
}
