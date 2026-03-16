package com.example.pffbrowser.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.example.pffbrowser.utils.CommonLog.logLifeCycle

open class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment(), IBaseView<VB> {

    companion object {
        const val TAG = "BaseFragment"
    }

    lateinit var viewModel: VM
    lateinit var viewBinding: VB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logLifeCycle(this, "onCreate")
        viewModel = createViewModel()
        initObserver()
        initRequestData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logLifeCycle(this, "onCreateView")
        viewBinding = BindingReflex.reflexViewBinding(javaClass, layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logLifeCycle(this, "onViewCreated")
        viewBinding.initView()
        viewBinding.setOnClickListener()
    }

    override fun onStart() {
        super.onStart()
        logLifeCycle(this, "onStart")
    }

    override fun onResume() {
        super.onResume()
        logLifeCycle(this, "onResume")
        refreshRequestData()
    }

    override fun onPause() {
        super.onPause()
        logLifeCycle(this, "onPause")
    }

    override fun onStop() {
        super.onStop()
        logLifeCycle(this, "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logLifeCycle(this, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        logLifeCycle(this, "onDestroy")
    }

    private fun <VM : BaseViewModel> createViewModel(): VM {
        val vmClass = getViewModelClass<VM>(this)
        return ViewModelProvider(this)[vmClass]
    }

    override fun VB.initView() {
    }

    override fun VB.setOnClickListener() {
    }

    override fun initObserver() {
    }

    override fun initRequestData() {
    }

    override fun refreshRequestData() {

    }

    override fun reLoadData() {
    }

    override fun isRecreate(): Boolean {
        return false
    }
}