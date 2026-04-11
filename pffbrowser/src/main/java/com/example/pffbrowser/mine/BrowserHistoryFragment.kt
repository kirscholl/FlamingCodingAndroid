package com.example.pffbrowser.mine

import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.databinding.PbFragmentBrowserHistoryBinding
import com.example.pffbrowser.mine.adapter.BrowserHistoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BrowserHistoryFragment :
    BaseFragment<PbFragmentBrowserHistoryBinding, BrowserHistoryViewModel>() {

    private val adapter by lazy {
        BrowserHistoryAdapter { history ->
            mViewModel.deleteHistory(history)
        }
    }

    override fun PbFragmentBrowserHistoryBinding.initView() {
        rvHistory.layoutManager = LinearLayoutManager(requireContext())
        rvHistory.adapter = adapter

        btnBack.setOnClickListener { findNavController().popBackStack() }

        etSearch.addTextChangedListener { text ->
            mViewModel.searchQuery.value = text?.toString() ?: ""
        }
    }

    override fun initViewObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            mViewModel.historyItems.collectLatest { items ->
                adapter.submitList(items)
                mViewBinding.llEmpty.visibility =
                    if (items.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
                mViewBinding.rvHistory.visibility =
                    if (items.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE
            }
        }
    }
}
