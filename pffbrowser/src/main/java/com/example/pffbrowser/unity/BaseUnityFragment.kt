package com.example.pffbrowser.unity

import UnityPlayerController
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment

abstract class BaseUnityFragment : Fragment() {

    companion object {
        const val TAG = "BaseUnityFragment"
    }

    protected var container: FrameLayout? = null
    protected val mainHandler = Handler(Looper.getMainLooper())

    // 子类提供唯一标识
    abstract fun getFragmentTag(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        retainInstance = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        this.container = FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        return this.container!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        view.post {
            // 延迟确保容器准备好
            attachUnity()
        }
    }

    private fun attachUnity() {
        Log.d(TAG, "attachUnity")
        container ?: return
        // 设置当前host
        UnityPlayerController.setCurrentHost(this)
        // 附加Unity到当前容器
        UnityPlayerController.attachToContainer(requireActivity(), container!!)
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
        // 如果当前host是自己，恢复渲染
        if (UnityPlayerController.getCurrentHost() == this) {
            UnityPlayerController.attachToContainer(requireActivity(), container ?: return)
        }
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
        UnityPlayerController.onPause()
        // 不要在这里detach，让新Fragment来处理
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        // 只有当前host是自己时才detach
        if (UnityPlayerController.getCurrentHost() == this) {
            UnityPlayerController.detachFromContainer()
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
        // 不销毁Unity，保持复用
    }
}