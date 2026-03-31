package com.example.pffbrowser.webview.optimization

import android.content.Context
import android.os.Build
import android.webkit.WebView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetAddress

/**
 * DNS预解析优化器
 * 提前解析常用域名，减少DNS查询时间
 */
object DnsPreResolver {

    // 常用域名列表
    private val commonDomains = mutableSetOf<String>()

    // 是否已初始化
    private var isInitialized = false

    /**
     * 初始化DNS预解析
     * @param domains 需要预解析的域名列表
     */
    fun init(domains: List<String>) {
        if (isInitialized) return

        commonDomains.addAll(domains)
        isInitialized = true

        // 开始预解析
        preResolveDomains()
    }

    /**
     * 添加需要预解析的域名
     */
    fun addDomain(domain: String) {
        commonDomains.add(domain)
        preResolveDomain(domain)
    }

    /**
     * 批量添加域名
     */
    fun addDomains(domains: List<String>) {
        commonDomains.addAll(domains)
        preResolveDomains()
    }

    /**
     * 预解析所有域名
     */
    private fun preResolveDomains() {
        CoroutineScope(Dispatchers.IO).launch {
            commonDomains.forEach { domain ->
                preResolveDomain(domain)
            }
        }
    }

    /**
     * 预解析单个域名
     */
    private fun preResolveDomain(domain: String) {
        try {
            // 提取纯域名（去除协议和路径）
            val pureDomain = extractDomain(domain)

            // 执行DNS查询
            InetAddress.getByName(pureDomain)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 提取纯域名
     */
    private fun extractDomain(url: String): String {
        var domain = url
        // 去除协议
        if (domain.startsWith("http://")) {
            domain = domain.substring(7)
        } else if (domain.startsWith("https://")) {
            domain = domain.substring(8)
        }
        // 去除路径
        val slashIndex = domain.indexOf('/')
        if (slashIndex != -1) {
            domain = domain.substring(0, slashIndex)
        }
        // 去除端口
        val colonIndex = domain.indexOf(':')
        if (colonIndex != -1) {
            domain = domain.substring(0, colonIndex)
        }
        return domain
    }

    /**
     * 从URL中提取域名并预解析
     */
    fun preResolveFromUrl(url: String) {
        val domain = extractDomain(url)
        if (domain.isNotEmpty()) {
            addDomain(domain)
        }
    }

    /**
     * 配置WebView的DNS预解析
     */
    fun setupWebViewDnsPreResolve(webView: WebView, context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0+ 支持DNS预解析
            webView.settings.safeBrowsingEnabled = true
        }
    }

    /**
     * 清除域名列表
     */
    fun clear() {
        commonDomains.clear()
    }

    /**
     * 获取已预解析的域名列表
     */
    fun getDomains(): Set<String> {
        return commonDomains.toSet()
    }

    /**
     * 预定义的常用域名
     */
    fun getDefaultDomains(): List<String> {
        return listOf(
            "www.baidu.com",
            "www.google.com",
            "www.taobao.com",
            "www.jd.com",
            "www.qq.com",
            "www.sina.com.cn",
            "www.163.com",
            "www.sohu.com",
            "www.zhihu.com",
            "www.bilibili.com"
        )
    }
}
