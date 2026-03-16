package com.example.pffbrowser.search

import androidx.navigation.fragment.findNavController
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.common.CommonConstValue
import com.example.pffbrowser.databinding.PbFragmentSearchResultBinding
import com.example.pffbrowser.webview.PbWebChromeClient
import com.example.pffbrowser.webview.PbWebViewClient

class SearchResultFragment : BaseFragment<PbFragmentSearchResultBinding, SearchResultViewModel>() {

    companion object {
        const val TAG = "SearchResultFragment"
    }

    override fun PbFragmentSearchResultBinding.initView() {
        viewBinding.editText.setText(arguments?.getString(CommonConstValue.SEARCH_WORD))
        initWebView()
    }

    fun initWebView() {
        viewBinding.searchWebView.loadUrl("https://www.baidu.com")
        viewBinding.searchWebView.webViewClient = PbWebViewClient()
        viewBinding.searchWebView.webChromeClient = PbWebChromeClient()
    }

    override fun PbFragmentSearchResultBinding.setOnClickListener() {
        viewBinding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        viewBinding.searchWebView.setOnClickListener {
            findNavController().navigate(R.id.pb_action_searchresultfragment_self)
        }
    }
}