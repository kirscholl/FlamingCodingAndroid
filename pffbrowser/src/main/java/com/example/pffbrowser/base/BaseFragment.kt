package com.example.pffbrowser.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    companion object {
        const val TAG = "BaseFragment"
        const val LOG_FLAG = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logFragmentLifeCycle("onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logFragmentLifeCycle("onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logFragmentLifeCycle("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        logFragmentLifeCycle("onStart")
        super.onStart()
    }

    override fun onResume() {
        logFragmentLifeCycle("onResume")
        super.onResume()
    }

    override fun onPause() {
        logFragmentLifeCycle("onPause")
        super.onPause()
    }

    override fun onStop() {
        logFragmentLifeCycle("onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        logFragmentLifeCycle("onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        logFragmentLifeCycle("onDestroy")
        super.onDestroy()
    }

    fun logFragmentLifeCycle(lifeCycleName: String) {
        if (LOG_FLAG) {
            Log.d(TAG, "LifCycleName: $lifeCycleName ### className: ${javaClass.simpleName}")
        }
    }
}