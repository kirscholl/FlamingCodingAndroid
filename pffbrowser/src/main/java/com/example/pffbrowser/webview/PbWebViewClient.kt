package com.example.pffbrowser.webview

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Message
import android.view.KeyEvent
import android.webkit.ClientCertRequest
import android.webkit.HttpAuthHandler
import android.webkit.RenderProcessGoneDetail
import android.webkit.SafeBrowsingResponse
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.pffbrowser.base.BaseViewModel
import com.example.pffbrowser.utils.LogUtil

class PbWebViewClient(
    mViewModel: BaseViewModel,
    private val onPageVisited: ((url: String, title: String) -> Unit)? = null
) : WebViewClient() {

    // 通知应用 WebView 的访问历史记录已更新
    override fun doUpdateVisitedHistory(
        view: WebView?,
        url: String?,
        isReload: Boolean
    ) {
        super.doUpdateVisitedHistory(view, url, isReload)
    }

    override fun onFormResubmission(
        view: WebView?,
        dontResend: Message?,
        resend: Message?
    ) {
        super.onFormResubmission(view, dontResend, resend)
    }

    // 通知应用 WebView 即将加载某个资源（图片、CSS、JS、iframe 等）
    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
    }

    // 通知应用页面的内容已经开始渲染并可见（但尚未完全加载完成）
    override fun onPageCommitVisible(view: WebView?, url: String?) {
        super.onPageCommitVisible(view, url)
    }

    // 通知应用页面开始加载。可以在此显示加载进度条或更改 UI
    override fun onPageStarted(
        view: WebView?,
        url: String?,
        favicon: Bitmap?
    ) {
        super.onPageStarted(view, url, favicon)
        LogUtil.logWebViewClient("onPageStarted -> url: $url --- favicon: $favicon")
    }

    // 通知应用页面加载完成。可以隐藏进度条、执行 JS 注入等
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        LogUtil.logWebViewClient("onPageFinished -> url: $url")
        val finalUrl = url ?: return
        if (finalUrl.isBlank() || finalUrl == "about:blank" || finalUrl == "about:newtab") return
        val title = view?.title?.takeIf { it.isNotBlank() } ?: finalUrl
        onPageVisited?.invoke(finalUrl, title)
    }

    // 处理服务端要求客户端提供 SSL 证书进行身份验证的请求
    override fun onReceivedClientCertRequest(
        view: WebView?,
        request: ClientCertRequest?
    ) {
        super.onReceivedClientCertRequest(view, request)
    }

    // 通知应用主资源（或子资源）加载过程中发生错误（如网络不可达、DNS 解析失败）
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
    }

    // 处理 HTTP 基础认证（Basic/Digest）请求。
    //回调时机：当服务器返回 401 状态码并要求提供用户名/密码时触发
    override fun onReceivedHttpAuthRequest(
        view: WebView?,
        handler: HttpAuthHandler?,
        host: String?,
        realm: String?
    ) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm)
    }

    // 专门通知 HTTP 错误状态码（4xx、5xx）的响应
    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
    }

    override fun onReceivedLoginRequest(
        view: WebView?,
        realm: String?,
        account: String?,
        args: String?
    ) {
        super.onReceivedLoginRequest(view, realm, account, args)
    }

    // 通知应用 SSL 证书验证失败（证书过期、域名不匹配等）
    override fun onReceivedSslError(
        view: WebView?,
        handler: SslErrorHandler?,
        error: SslError?
    ) {
        super.onReceivedSslError(view, handler, error)
    }

    // 通知应用渲染进程意外终止（崩溃或被系统杀死）
    // 回调时机：当 WebView 的内部渲染进程因内存不足、崩溃等原因被杀死时触发
    override fun onRenderProcessGone(
        view: WebView?,
        detail: RenderProcessGoneDetail?
    ): Boolean {
        return super.onRenderProcessGone(view, detail)
    }

    // Google 安全浏览服务检测到当前 URL 为恶意/钓鱼网站时进行拦截
    // 回调时机：当 WebView 准备加载的 URL 被 Safe Browsing 标记为威胁时调用
    override fun onSafeBrowsingHit(
        view: WebView?,
        request: WebResourceRequest?,
        threatType: Int,
        callback: SafeBrowsingResponse?
    ) {
        super.onSafeBrowsingHit(view, request, threatType, callback)
    }

    // 通知应用页面缩放比例发生变化
    // 回调时机：用户双指缩放或通过脚本改变缩放时触发
    override fun onScaleChanged(
        view: WebView?,
        oldScale: Float,
        newScale: Float
    ) {
        super.onScaleChanged(view, oldScale, newScale)
    }

    // 通知应用有一个按键事件未被 WebView 内部处理
    // 回调时机：当 WebView 不处理某个按键事件（比如它不关心 BACK 键）时调用
    override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?) {
        super.onUnhandledKeyEvent(view, event)
    }

    // 拦截页面的每一个网络请求，允许应用返回自定义数据作为响应（例如离线资源、缓存替换、广告屏蔽）
    // 当 WebView 准备发出任何资源请求（主框架、子资源、Ajax 等）时调用，在 UI 线程之外执行（不会阻塞 UI）
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        return super.shouldInterceptRequest(view, request)
    }

    // 决定是否由 WebView 处理按键事件。
    // 回调时机：当 WebView 获得焦点且有按键事件发生时（例如音量键、返回键等）
    override fun shouldOverrideKeyEvent(
        view: WebView?,
        event: KeyEvent?
    ): Boolean {
        return super.shouldOverrideKeyEvent(view, event)
    }

    // 当页面内即将发生导航行为（点击链接、重定向、表单提交等）并且WebView即将加载新的URL时调用
    // 当WebView内的主框架即将从一个旧URL切换到新URL时
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        val url = request?.url?.toString() ?: return false
        // 只处理需要特殊导航逻辑的场景（如自定义 scheme）
        if (url.startsWith("myapp://")) {
            handleCustomScheme(url)
            return true  // 拦截
        }

        // 对于可能的下载链接，返回 false 让 DownloadListener 处理
        // 因为 DownloadListener 能获取 MIME Type 等更精确信息
        return false
    }

    fun handleCustomScheme(url: String?) {

    }
}