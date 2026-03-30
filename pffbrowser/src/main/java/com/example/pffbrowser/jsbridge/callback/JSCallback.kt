package com.example.pffbrowser.jsbridge.callback

/**
 * JSBridge 回调接口
 */
interface JSCallback {
    /**
     * 成功回调
     */
    fun onSuccess(data: Any?)

    /**
     * 失败回调
     */
    fun onError(code: Int, message: String)

    /**
     * 进度回调（可选）
     */
    fun onProgress(progress: Int) {}
}
