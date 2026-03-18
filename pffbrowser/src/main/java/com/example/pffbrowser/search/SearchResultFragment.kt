package com.example.pffbrowser.search

import androidx.navigation.fragment.findNavController
import com.example.pffbrowser.common.CommonConstValue
import com.example.pffbrowser.databinding.PbFragmentSearchResultBinding
import com.example.pffbrowser.webview.BaseWebViewFragment
import com.example.pffbrowser.webview.PbWebView

class SearchResultFragment :
    BaseWebViewFragment<PbFragmentSearchResultBinding, SearchResultViewModel>() {

    companion object {
        const val TAG = "SearchResultFragment"
    }

    // 直接委托获取，确保第一时间初始化
    override val mWebView: PbWebView
        get() = mViewBinding.searchWebView

    override fun PbFragmentSearchResultBinding.initView() {
        mViewBinding.editText.setText(arguments?.getString(CommonConstValue.SEARCH_WORD))
    }

    override fun PbFragmentSearchResultBinding.setOnClickListener() {
        mViewBinding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        mViewBinding.btnResSearch.setOnClickListener {
//            findNavController().navigate(R.id.pb_action_searchresultfragment_self)
            // TODO 换成一个下载链接测试
            mWebView.loadUrl("https://speed.cloudflare.com/__down?bytes=10240")
        }
    }
}