package com.example.pffbrowser.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.example.pffbrowser.utils.LogUtil.logLifeCycle

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment(), IBaseView<VB> {

    companion object {
        const val TAG = "BaseFragment"
    }

    lateinit var mViewModel: VM
    private var _binding: VB? = null
    protected val mViewBinding get() = _binding!!
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        logLifeCycle(this, "onSaveInstanceState")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logLifeCycle(this, "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logLifeCycle(this, "onCreate")
        mViewModel = createViewModel()
        initRequestData()
        initObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logLifeCycle(this, "onCreateView")
        _binding = BindingReflex.reflexViewBinding(javaClass, layoutInflater)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logLifeCycle(this, "onViewCreated")
        mViewBinding.initView()
        mViewBinding.setOnClickListener()
        initViewObserver()
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
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        logLifeCycle(this, "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        logLifeCycle(this, "onDetach")
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

    override fun initViewObserver() {
    }

    override fun initRequestData() {
        if (mViewModel is BaseNetWorkViewModel) {
            (mViewModel as BaseNetWorkViewModel).requestInitData()
        }
    }

    override fun refreshRequestData() {
        if (mViewModel is BaseNetWorkViewModel) {
            (mViewModel as BaseNetWorkViewModel).requestRefreshData()
        }
    }

    override fun reLoadData() {
    }

    override fun isRecreate(): Boolean {
        return false
    }
}