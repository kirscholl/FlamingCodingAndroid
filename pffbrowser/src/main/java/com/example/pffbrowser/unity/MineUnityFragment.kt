package com.example.pffbrowser.unity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class MineUnityFragment : BaseUnityFragment() {

    companion object {
        const val TAG = "MineUnityFragment"
    }

    override fun getFragmentTag(): String = TAG

    override fun onAttach(context: Context) {
        Log.d(TAG, "UnityLifeCycle onAttach")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "UnityLifeCycle onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "UnityLifeCycle onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "UnityLifeCycle onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        Log.d(TAG, "UnityLifeCycle onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "UnityLifeCycle onPause")
        super.onPause()
    }

    override fun onDestroyView() {
        Log.d(TAG, "UnityLifeCycle onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(TAG, "UnityLifeCycle onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d(TAG, "UnityLifeCycle onDetach")
        super.onDetach()
    }
}