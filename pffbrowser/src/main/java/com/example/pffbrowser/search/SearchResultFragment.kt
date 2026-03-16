package com.example.pffbrowser.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.common.CommonConstValue
import com.example.pffbrowser.databinding.PbFragmentSearchResultBinding
import com.example.pffbrowser.webview.PbWebChromeClient
import com.example.pffbrowser.webview.PbWebViewClient

class SearchResultFragment : BaseFragment() {

    companion object {
        const val TAG = "SearchResultFragment"
    }

    lateinit var binding: PbFragmentSearchResultBinding

    private val viewModel: SearchResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PbFragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.searchWebView.setOnClickListener {
            findNavController().navigate(R.id.pb_action_searchresultfragment_self)
        }
        binding.editText.setText(arguments?.getString(CommonConstValue.SEARCH_WORD))
        initWebView()
    }

    fun initWebView() {
        binding.searchWebView.loadUrl("https://www.baidu.com")
        binding.searchWebView.webViewClient = PbWebViewClient()
        binding.searchWebView.webChromeClient = PbWebChromeClient()
    }
}