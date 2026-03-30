package com.example.pffbrowser.jsbridge.module

import com.example.pffbrowser.jsbridge.callback.JSCallback
import org.json.JSONObject

/**
 * JSBridge模块接口
 * 所有功能模块都需要实现此接口
 */
interface IJSBridgeModule {
    /**
     * 获取模块名称
     */
    fun getModuleName(): String

    /**
     * 调用模块方法
     * @param method 方法名
     * @param params 参数
     * @param callback 回调
     */
    fun invoke(method: String, params: JSONObject, callback: JSCallback)
}
