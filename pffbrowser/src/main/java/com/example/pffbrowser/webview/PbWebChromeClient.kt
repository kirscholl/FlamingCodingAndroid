package com.example.pffbrowser.webview

import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.GeolocationPermissions
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.example.pffbrowser.base.BaseViewModel
import com.example.pffbrowser.utils.LogUtil

class PbWebChromeClient(mViewModel: BaseViewModel) : WebChromeClient() {

    // 当网页中的 <video> 标签没有设置 poster 属性（封面图），且视频正在缓冲或未播放时
    override fun getDefaultVideoPoster(): Bitmap? {
        return super.getDefaultVideoPoster()
    }

    // 全屏视频正在缓冲加载时
    override fun getVideoLoadingProgressView(): View? {
        return super.getVideoLoadingProgressView()
    }

    // WebView 首次加载页面，需要知道哪些链接已经被访问过，以便正确渲染 CSS 的 :visited 伪类（例如已访问的链接显示紫色）
    override fun getVisitedHistory(callback: ValueCallback<Array<out String?>?>?) {
        super.getVisitedHistory(callback)
    }

    // JS 调用 window.close() 时
    override fun onCloseWindow(window: WebView?) {
        super.onCloseWindow(window)
    }

    // 网页 JS 执行了 console.log、console.error 等日志输出代码时
    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        return super.onConsoleMessage(consoleMessage)
    }

    // 网页试图打开一个新窗口（如点击了 <a target="_blank"> 且 WebSettings 开启了 setSupportMultipleWindows(true)）时
    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
    }

    // 地理位置请求被网页取消时
    override fun onGeolocationPermissionsHidePrompt() {
        super.onGeolocationPermissionsHidePrompt()
    }

    // JS 调用 navigator.geolocation 获取地理位置时
    override fun onGeolocationPermissionsShowPrompt(
        origin: String?,
        callback: GeolocationPermissions.Callback?
    ) {
        super.onGeolocationPermissionsShowPrompt(origin, callback)
    }

    // 网页退出全屏模式时（例如用户点击了退出全屏按钮，或按了返回键）
    override fun onHideCustomView() {
        super.onHideCustomView()
    }

    // 前端 JS 调用 alert('xxx') 时
    override fun onJsAlert(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        return super.onJsAlert(view, url, message, result)
    }

    // 当页面即将导航离开或关闭，且网页 JS 监听了 onbeforeunload 事件（通常用于提示用户“有未保存的表单数据”）时
    override fun onJsBeforeUnload(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        return super.onJsBeforeUnload(view, url, message, result)
    }

    // 前端 JS 调用 confirm('xxx')（通常包含确认和取消两个按钮）时
    override fun onJsConfirm(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        return super.onJsConfirm(view, url, message, result)
    }

    // 前端 JS 调用 prompt('xxx')（要求用户输入一段文本）时
    // 弹出一个带有 EditText 的原生对话框。将用户输入的字符串通过 result.confirm(text) 传给网页
    override fun onJsPrompt(
        view: WebView?,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult?
    ): Boolean {
        return super.onJsPrompt(view, url, message, defaultValue, result)
    }

    // 网页通过 getUserMedia 等 API 申请如摄像头 (RESOURCE_VIDEO_CAPTURE)、麦克风 (RESOURCE_AUDIO_CAPTURE) 权限时
    override fun onPermissionRequest(request: PermissionRequest?) {
        super.onPermissionRequest(request)
    }

    // 网页主动取消了刚才发起的权限请求时
    override fun onPermissionRequestCanceled(request: PermissionRequest?) {
        super.onPermissionRequestCanceled(request)
    }

    // 网页加载进度发生变化时频繁触发
    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        LogUtil.logWebChromeClient("onProgressChanged: $newProgress")
    }

    // 接收到网页的 favicon（网站图标）时
    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
        super.onReceivedIcon(view, icon)
        LogUtil.logWebChromeClient("onReceivedIcon: $icon")
    }

    //
    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
        LogUtil.logWebChromeClient("onReceivedTitle: $title")
    }

    // 解析到苹果定义的 Apple Touch Icon（用于添加到主屏幕时的图标）时
    override fun onReceivedTouchIconUrl(
        view: WebView?,
        url: String?,
        precomposed: Boolean
    ) {
        super.onReceivedTouchIconUrl(view, url, precomposed)
    }

    // 网页元素请求获取焦点时（例如某个输入框调用了 .focus()）
    override fun onRequestFocus(view: WebView?) {
        super.onRequestFocus(view)
    }

    // 网页中的视频/元素请求全屏显示时（例如用户点击了视频播放器自带的全屏按钮）
    override fun onShowCustomView(
        view: View?,
        callback: CustomViewCallback?
    ) {
        super.onShowCustomView(view, callback)
    }

    // 用户点击了网页上的 <input type="file"> 标签时
    // H5 无法直接调起 Android 相册/相机。触发此回调后，需要通过原生的 Intent（如 ACTION_GET_CONTENT 或调用相机）
    // 打开原生文件选择器/相机。用户选择/拍照完成后，拿到文件的 Uri 数组，
    // 通过 filePathCallback.onReceiveValue(uris) 传回给网页，实现文件上传功能
    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<out Uri?>?>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }
}