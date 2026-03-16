package com.example.pffbrowser.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseFragment

class MineFragment : BaseFragment() {

    companion object {
        const val TAG = "MineFragment"
    }

    private val viewModel: MineViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.pb_fragment_mine, container, false)
    }
}