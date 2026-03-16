package com.example.pffbrowser.square.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseFragment

class FollowFragment : BaseFragment() {

    companion object {
        fun newInstance() = FollowFragment()
    }

    private val viewModel: FollowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.pb_fragment_follow, container, false)
    }
}