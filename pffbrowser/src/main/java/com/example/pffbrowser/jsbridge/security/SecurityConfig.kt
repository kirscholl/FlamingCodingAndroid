package com.example.pffbrowser.jsbridge.security

import android.net.Uri
import com.example.pffbrowser.BuildConfig


/**
 * 安全配置
 */
data class SecurityConfig(
    // 允许的域名列表
    val allowedDomains: List<String> = emptyList(),
    // 开发环境允许localhost
    val allowLocalhost: Boolean = BuildConfig.DEBUG,
    // 是否强制HTTPS
    val forceHttps: Boolean = !BuildConfig.DEBUG
) {
    /**
     * 检查URL是否在白名单中
     */
    fun isUrlAllowed(url: String?): Boolean {
        if (url.isNullOrEmpty()) return false

        try {
            val uri = Uri.parse(url)
            val scheme = uri.scheme ?: return false

            // 允许本地文件 (file://)
            if (scheme == "file") {
                return true
            }

            val host = uri.host ?: return false

            // 检查是否强制HTTPS
            if (forceHttps && scheme != "https") {
                return false
            }

            // 检查localhost
            if (allowLocalhost && (host == "localhost" || host == "127.0.0.1" || host == "10.0.2.2")) {
                return true
            }

            // 检查白名单
            return allowedDomains.any { allowedDomain ->
                host == allowedDomain || host.endsWith(".$allowedDomain")
            }
        } catch (e: Exception) {
            return false
        }
    }
}
