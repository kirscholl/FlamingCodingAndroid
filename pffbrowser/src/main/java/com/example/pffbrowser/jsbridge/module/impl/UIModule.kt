package com.example.pffbrowser.jsbridge.module.impl

import android.content.Context
import android.os.Vibrator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pffbrowser.jsbridge.callback.JSCallback
import com.example.pffbrowser.jsbridge.log.JSBridgeLog
import com.example.pffbrowser.jsbridge.model.JSBridgeError
import com.example.pffbrowser.jsbridge.module.IJSBridgeModule
import org.json.JSONObject

/**
 * UI模块
 * 提供UI相关的功能
 */
class UIModule(private val context: Context) : IJSBridgeModule {

    override fun getModuleName(): String = "ui"

    override fun invoke(method: String, params: JSONObject, callback: JSCallback) {
        try {
            when (method) {
                "showToast" -> showToast(params, callback)
                "showDialog" -> showDialog(params, callback)
                "vibrate" -> vibrate(params, callback)
                else -> {
                    callback.onError(JSBridgeError.METHOD_NOT_FOUND, "方法不存在: $method")
                }
            }
        } catch (e: Exception) {
            JSBridgeLog.e("UIModule error: ${e.message}", e)
            callback.onError(JSBridgeError.SYSTEM_ERROR, e.message ?: "系统错误")
        }
    }

    /**
     * 显示Toast
     * params: { message: String, duration: Int }
     */
    private fun showToast(params: JSONObject, callback: JSCallback) {
        val message = params.optString("message", "")
        val duration = params.optInt("duration", 2000)

        if (message.isEmpty()) {
            callback.onError(JSBridgeError.PARAM_ERROR, "message不能为空")
            return
        }

        val toastDuration = if (duration > 2000) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        Toast.makeText(context, message, toastDuration).show()
        callback.onSuccess(null)
    }

    /**
     * 显示对话框
     * params: { title: String, message: String, buttons: [String] }
     */
    private fun showDialog(params: JSONObject, callback: JSCallback) {
        val title = params.optString("title", "提示")
        val message = params.optString("message", "")
        val buttonsArray = params.optJSONArray("buttons")

        if (message.isEmpty()) {
            callback.onError(JSBridgeError.PARAM_ERROR, "message不能为空")
            return
        }

        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        // 处理按钮
        if (buttonsArray != null && buttonsArray.length() > 0) {
            when (buttonsArray.length()) {
                1 -> {
                    builder.setPositiveButton(buttonsArray.getString(0)) { _, _ ->
                        callback.onSuccess(JSONObject().apply { put("index", 0) })
                    }
                }
                2 -> {
                    builder.setNegativeButton(buttonsArray.getString(0)) { _, _ ->
                        callback.onSuccess(JSONObject().apply { put("index", 0) })
                    }
                    builder.setPositiveButton(buttonsArray.getString(1)) { _, _ ->
                        callback.onSuccess(JSONObject().apply { put("index", 1) })
                    }
                }
                else -> {
                    builder.setNegativeButton(buttonsArray.getString(0)) { _, _ ->
                        callback.onSuccess(JSONObject().apply { put("index", 0) })
                    }
                    builder.setNeutralButton(buttonsArray.getString(1)) { _, _ ->
                        callback.onSuccess(JSONObject().apply { put("index", 1) })
                    }
                    builder.setPositiveButton(buttonsArray.getString(2)) { _, _ ->
                        callback.onSuccess(JSONObject().apply { put("index", 2) })
                    }
                }
            }
        } else {
            builder.setPositiveButton("确定") { _, _ ->
                callback.onSuccess(JSONObject().apply { put("index", 0) })
            }
        }

        builder.setOnCancelListener {
            callback.onError(JSBridgeError.UNKNOWN_ERROR, "用户取消")
        }

        builder.show()
    }

    /**
     * 震动
     * params: { duration: Int }
     */
    private fun vibrate(params: JSONObject, callback: JSCallback) {
        val duration = params.optLong("duration", 100)
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.vibrate(duration)
        callback.onSuccess(null)
    }
}