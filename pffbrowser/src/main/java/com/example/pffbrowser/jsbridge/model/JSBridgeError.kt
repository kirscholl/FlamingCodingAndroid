package com.example.pffbrowser.jsbridge.model

/**
 * JSBridge 错误码
 */
object JSBridgeError {
    const val SUCCESS = 0
    const val PARAM_ERROR = 1000
    const val MODULE_NOT_FOUND = 1001
    const val METHOD_NOT_FOUND = 1002
    const val PERMISSION_DENIED = 1003
    const val TIMEOUT = 1004
    const val NETWORK_ERROR = 2000
    const val SYSTEM_ERROR = 3000
    const val UNKNOWN_ERROR = 9999

    fun getErrorMessage(code: Int): String {
        return when (code) {
            SUCCESS -> "success"
            PARAM_ERROR -> "参数错误"
            MODULE_NOT_FOUND -> "模块不存在"
            METHOD_NOT_FOUND -> "方法不存在"
            PERMISSION_DENIED -> "权限不足"
            TIMEOUT -> "请求超时"
            NETWORK_ERROR -> "网络错误"
            SYSTEM_ERROR -> "系统错误"
            UNKNOWN_ERROR -> "未知错误"
            else -> "未知错误"
        }
    }
}
