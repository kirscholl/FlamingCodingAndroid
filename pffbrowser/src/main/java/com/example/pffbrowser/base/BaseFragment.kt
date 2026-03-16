package com.example.pffbrowser.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pffbrowser.utils.CommonLog.logLifeCycle

open class BaseFragment : Fragment() {

    companion object {
        const val TAG = "BaseFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logLifeCycle(this, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logLifeCycle(this, "onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logLifeCycle(this, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        logLifeCycle(this, "onStart")
        super.onStart()
    }

    override fun onResume() {
        logLifeCycle(this, "onResume")
        super.onResume()
    }

    override fun onPause() {
        logLifeCycle(this, "onPause")
        super.onPause()
    }

    override fun onStop() {
        logLifeCycle(this, "onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        logLifeCycle(this, "onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        logLifeCycle(this, "onDestroy")
        super.onDestroy()
    }
}