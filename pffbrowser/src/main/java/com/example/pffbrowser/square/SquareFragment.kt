package com.example.pffbrowser.square

import android.widget.TextView
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.databinding.PbFragmentSquareBinding
import com.google.android.material.tabs.TabLayoutMediator

class SquareFragment : BaseFragment<PbFragmentSquareBinding, SquareViewModel>() {

    companion object {
        val tabTitles = listOf<String>("关注", "发现", "同城")
        const val TAG = "SquareFragment"
    }

    override fun PbFragmentSquareBinding.initView() {
        val adapter = SquareViewPagerAdapter(this@SquareFragment)
        mViewBinding.viewPager2.adapter = adapter
        TabLayoutMediator(mViewBinding.tabLayout, mViewBinding.viewPager2) { tab, position ->
            // 自定义tab的View
            tab.setCustomView(R.layout.pb_custom_square_tab_item)
            val customView = tab.customView
            customView?.findViewById<TextView>(R.id.tab_text)?.text = tabTitles[position]
        }.attach()
    }
}