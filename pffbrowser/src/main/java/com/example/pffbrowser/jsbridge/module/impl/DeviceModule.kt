package com.example.pffbrowser.jsbridge.module.impl

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.example.pffbrowser.jsbridge.callback.JSCallback
import com.example.pffbrowser.jsbridge.log.JSBridgeLog
import com.example.pffbrowser.jsbridge.model.JSBridgeError
import com.example.pffbrowser.jsbridge.module.IJSBridgeModule
import org.json.JSONObject

/**
 * 设备信息模块
 * 提供设备和应用信息
 */
class DeviceModule(private val context: Context) : IJSBridgeModule {

    override fun getModuleName(): String = "device"

    override fun invoke(method: String, params: JSONObject, callback: JSCallback) {
        try {
            when (method) {
                "getDeviceInfo" -> getDeviceInfo(callback)
                "getAppInfo" -> getAppInfo(callback)
                "getSystemInfo" -> getSystemInfo(callback)
                else -> {
                    callback.onError(JSBridgeError.METHOD_NOT_FOUND, "方法不存在: $method")
                }
            }
        } catch (e: Exception) {
            JSBridgeLog.e("DeviceModule error: ${e.message}", e)
            callback.onError(JSBridgeError.SYSTEM_ERROR, e.message ?: "系统错误")
        }
    }

    /**
     * 获取设备信息
     */
    private fun getDeviceInfo(callback: JSCallback) {
        val deviceInfo = JSONObject().apply {
            put("brand", Build.BRAND)
            put("model", Build.MODEL)
            put("manufacturer", Build.MANUFACTURER)
            put("device", Build.DEVICE)
            put("product", Build.PRODUCT)
        }
        callback.onSuccess(deviceInfo)
    }

    /**
     * 获取应用信息
     */
    private fun getAppInfo(callback: JSCallback) {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val appInfo = JSONObject().apply {
                put("packageName", context.packageName)
                put("versionName", packageInfo.versionName)
                put("versionCode", if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode
                })
            }
            callback.onSuccess(appInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            callback.onError(JSBridgeError.SYSTEM_ERROR, "获取应用信息失败")
        }
    }

    /**
     * 获取系统信息
     */
    private fun getSystemInfo(callback: JSCallback) {
        val systemInfo = JSONObject().apply {
            put("osVersion", Build.VERSION.RELEASE)
            put("sdkVersion", Build.VERSION.SDK_INT)
            put("platform", "Android")
        }
        callback.onSuccess(systemInfo)
    }
}