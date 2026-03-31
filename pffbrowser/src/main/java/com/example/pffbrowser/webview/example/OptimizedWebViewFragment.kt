package com.example.pffbrowser.webview.example

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import com.example.pffbrowser.base.BaseViewModel
import com.example.pffbrowser.databinding.PbFragmentOptimizedWebViewBinding
import com.example.pffbrowser.webview.PooledWebViewFragment

/**
 * 优化后的WebView使用示例
 * 展示如何使用WebView池化和各种优化功能
 */
class OptimizedWebViewFragment :
    PooledWebViewFragment<PbFragmentOptimizedWebViewBinding, OptimizedWebViewViewModel>() {

    companion object {
        private const val ARG_URL = "url"
        private const val DEFAULT_URL = "https://www.baidu.com"

        /**
         * 创建Fragment实例
         */
        fun newInstance(url: String): OptimizedWebViewFragment {
            return OptimizedWebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_URL, url)
                }
            }
        }
    }

    private val viewModel: BaseViewModel by viewModels()

    override val webViewContainer: FrameLayout
        get() = mViewBinding.webviewContainer
//
//    override fun getViewBinding(
//        inflater: LayoutInflater,
//        container: ViewGroup?
//    ): PbFragmentOptimizedWebViewBinding {
//        return PbFragmentOptimizedWebViewBinding.inflate(inflater, container, false)
//    }
//
//    override fun getVM(): BaseViewModel = viewModel

    override fun getUrlToLoad(): String {
        // 从arguments中获取URL，或使用默认URL
        return arguments?.getString(ARG_URL) ?: DEFAULT_URL
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 可以在这里添加额外的UI逻辑
        setupToolbar()
    }

    private fun setupToolbar() {
        // 设置标题等
    }
}
