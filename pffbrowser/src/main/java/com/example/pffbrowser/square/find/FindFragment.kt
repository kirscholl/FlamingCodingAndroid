package com.example.pffbrowser.square.find

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseFragment

class FindFragment : BaseFragment() {

    companion object {
        fun newInstance() = FindFragment()
    }

    private val viewModel: FindViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.pb_fragment_find, container, false)
    }
}