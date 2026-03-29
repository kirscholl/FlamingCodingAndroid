package com.example.pffbrowser.search

import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pffbrowser.common.CommonConstValue
import com.example.pffbrowser.databinding.PbFragmentSearchResultBinding
import com.example.pffbrowser.download.DownloadDialogInfo
import com.example.pffbrowser.download.repository.DownloadRepository
import com.example.pffbrowser.webview.BaseWebViewFragment
import com.example.pffbrowser.webview.PbWebView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchResultFragment :
    BaseWebViewFragment<PbFragmentSearchResultBinding, SearchResultViewModel>() {

    @Inject
    lateinit var downloadRepository: DownloadRepository

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
            // 下载链接测试 10k
//            mWebView.loadUrl("https://speed.cloudflare.com/__down?bytes=10240")
            mWebView.loadUrl("http://speedtest.tele2.net/10MB.zip")
        }
    }

    override fun onDownloadConfirmed(fileName: String, url: String, downloadInfo: DownloadDialogInfo) {
        lifecycleScope.launch {
            try {
                val taskId = downloadRepository.createDownloadTask(downloadInfo, fileName)
                downloadRepository.startDownload(taskId)
                Toast.makeText(requireContext(), "开始下载: $fileName", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "下载失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}