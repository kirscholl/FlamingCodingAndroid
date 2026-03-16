package com.example.pffbrowser.square

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.databinding.PbFragmentSquareBinding
import com.google.android.material.tabs.TabLayoutMediator

class SquareFragment : BaseFragment() {

    companion object {
        fun newInstance() = SquareFragment()
        val tabTitles = listOf<String>("关注", "发现", "同城")
        const val TAG = "SquareFragment"
    }

    lateinit var binding: PbFragmentSquareBinding
    private val viewModel: SquareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = PbFragmentSquareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = SquareViewPagerAdapter(this)
        binding.viewPager2.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            // 自定义tab的View
            tab.setCustomView(R.layout.pb_custom_square_tab_item)
            val customView = tab.customView
            customView?.findViewById<TextView>(R.id.tab_text)?.text = tabTitles[position]
        }.attach()
    }

    override fun onStart() {
        super.onStart()
    }
}