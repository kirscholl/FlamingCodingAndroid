package com.example.pffbrowser.unity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Unity Fragment 不再持有 Unity View
 * 只作为生命周期控制器，通知 Manager 显示/隐藏 Unity
 */
abstract class BaseUnityViewFragment : Fragment() {

    companion object {
        const val TAG = "BaseUnityViewFragment"
    }

    // 子类提供场景标识
    abstract fun getSceneName(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ${getSceneName()}")
        super.onCreate(savedInstanceState)
    }

    /**
     * 创建空容器作为占位
     * 实际 Unity View 在 Activity 层
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ${getSceneName()}")
        // 返回一个空的 FrameLayout 作为占位
        // 这个 View 不会显示，因为 Unity 在 Activity 层
        return View(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(0, 0)
            visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: ${getSceneName()}")
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * Fragment 可见时，通知 Manager 显示 Unity
     */
    override fun onResume() {
        Log.d(TAG, "onResume: ${getSceneName()}")
        super.onResume()
        UnityContainerManager.showUnity(getSceneName())
    }

    /**
     * Fragment 不可见时，通知 Manager 隐藏 Unity
     */
    override fun onPause() {
        Log.d(TAG, "onPause: ${getSceneName()}")
        // 先隐藏 Unity，再执行 super.onPause()
        UnityContainerManager.hideUnity()
        super.onPause()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView: ${getSceneName()}")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ${getSceneName()}")
        super.onDestroy()
    }
}
