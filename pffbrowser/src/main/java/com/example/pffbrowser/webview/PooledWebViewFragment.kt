package com.example.pffbrowser.webview

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.viewbinding.ViewBinding
import com.example.pffbrowser.base.BaseViewModel
import com.example.pffbrowser.webview.optimization.DnsPreResolver
import com.example.pffbrowser.webview.pool.WebViewPool

/**
 * 使用WebView池的Fragment基类
 * 自动从池中获取和回收WebView
 */
abstract class PooledWebViewFragment<VB : ViewBinding, VM : BaseViewModel> :
    BaseWebViewFragment<VB, VM>() {

    // WebView实例（从池中获取）
    private var _pooledWebView: PbWebView? = null
    override val mWebView: PbWebView
        get() = _pooledWebView ?: throw IllegalStateException("WebView未初始化")

    // WebView容器
    protected abstract val webViewContainer: FrameLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 从池中获取WebView
        _pooledWebView = WebViewPool.obtain(requireContext())

        // 添加到容器
        webViewContainer.addView(_pooledWebView)

        // 调用父类初始化
        super.onViewCreated(view, savedInstanceState)

        // 预解析域名
        getUrlToLoad()?.let { url ->
            DnsPreResolver.preResolveFromUrl(url)
        }

        // 加载URL
        loadUrl()
    }

    /**
     * 获取要加载的URL
     * 子类需要实现此方法
     */
    protected abstract fun getUrlToLoad(): String?

    /**
     * 加载URL
     */
    private fun loadUrl() {
        getUrlToLoad()?.let { url ->
            mWebView.loadUrl(url)
        }
    }

    override fun onDestroyView() {
        // 从容器中移除WebView
        webViewContainer.removeView(_pooledWebView)

        // 回收到池中
        WebViewPool.recycle(_pooledWebView)
        _pooledWebView = null

        super.onDestroyView()
    }

    override fun onDestroy() {
        // 注意：不要在这里调用mWebView.destroy()
        // 因为WebView已经回收到池中，由池管理其生命周期
        // 只需调用父类的其他清理逻辑
        super.onDestroy()
    }
}
