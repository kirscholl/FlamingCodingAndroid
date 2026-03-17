package com.example.pffbrowser.webview

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.viewbinding.ViewBinding
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.base.BaseViewModel

abstract class BaseWebViewFragment<VB : ViewBinding, VM : BaseViewModel> : BaseFragment<VB, VM>() {

    // 改为抽象属性，由子类实现
    abstract val mWebView: PbWebView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWebView()
        setInterceptBackPress()
    }

    fun initWebView() {
        mWebView.webViewClient = PbWebViewClient(mViewModel)
        mWebView.webChromeClient = PbWebChromeClient(mViewModel)
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