package com.example.pffbrowser.base

import android.app.Application
import android.content.Context
import com.example.pffbrowser.webview.optimization.DnsPreResolver
import com.example.pffbrowser.webview.pool.WebViewPool
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class PbHiltApplication : Application() {
    companion object {
        private var _instance: PbHiltApplication? = null

        fun getInstance(): PbHiltApplication {
            return _instance!!
        }

        fun getApplicationContext(): Context {
            return _instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        _instance = this

        // 初始化WebView池
        initWebViewPool()
        // 初始化DNS预解析
        initDnsPreResolver()
    }

    /**
     * 初始化WebView池
     */
    private fun initWebViewPool() {
        WebViewPool.init(this)
    }

    /**
     * 初始化DNS预解析
     */
    private fun initDnsPreResolver() {
        // 使用默认域名列表
        val defaultDomains = DnsPreResolver.getDefaultDomains()

        // 添加自定义域名
        val customDomains = listOf(
            "www.zhihu.com",
        )

        // 合并并初始化
        val allDomains = defaultDomains + customDomains
        DnsPreResolver.init(allDomains)
    }
}
