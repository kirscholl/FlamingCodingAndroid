package com.example.pffbrowser.webview

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.viewbinding.ViewBinding
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.base.BaseViewModel
import com.example.pffbrowser.download.DownloadDialogFragment
import com.example.pffbrowser.download.DownloadDialogInfo

abstract class BaseWebViewFragment<VB : ViewBinding, VM : BaseViewModel> : BaseFragment<VB, VM>() {

    // 改为抽象属性，由子类实现
    abstract val mWebView: PbWebView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWebView()
        setInterceptBackPress()
        setupDownloadListener()
    }

    fun initWebView() {
        mWebView.webViewClient = PbWebViewClient(mViewModel)
        mWebView.webChromeClient = PbWebChromeClient(mViewModel)
    }

    /**
     * 设置下载监听器
     */
    private fun setupDownloadListener() {
        mWebView.onDownloadListener = object : PbWebView.OnDownloadListener {
            override fun onDownloadStart(downloadInfo: DownloadDialogInfo) {
                showDownloadDialog(downloadInfo)
            }
        }
    }

    /**
     * 显示下载弹窗
     */
    private fun showDownloadDialog(downloadInfo: DownloadDialogInfo) {
        val dialog = DownloadDialogFragment.newInstance(downloadInfo)
        dialog.setOnDownloadConfirmListener(object : DownloadDialogFragment.OnDownloadConfirmListener {
            override fun onDownloadConfirm(fileName: String, url: String) {
                // 子类可以重写此方法来处理下载
                onDownloadConfirmed(fileName, url, downloadInfo)
            }
        })
        dialog.show(childFragmentManager, "DownloadDialog")
    }

    /**
     * 下载确认回调，子类可以重写此方法来处理实际的下载逻辑
     */
    protected open fun onDownloadConfirmed(fileName: String, url: String, downloadInfo: DownloadDialogInfo) {
        // 默认实现：打印日志
        // 子类可以重写此方法来启动实际的下载任务
    }

    fun setInterceptBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack()
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
    }

    override fun onResume() {
        super.onResume()
        mWebView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mWebView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mWebView.destroy()
    }
}
