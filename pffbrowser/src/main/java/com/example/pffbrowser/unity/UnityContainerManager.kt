package com.example.pffbrowser.unity

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.unity3d.player.UnityPlayer

object UnityContainerManager {

    private var unityPlayer: UnityPlayer? = null
    private var unityContainer: FrameLayout? = null  // 单一容器
    private var isInitialized = false
    private var currentActivity: Activity? = null

    fun initialize(activity: Activity) {
        if (unityPlayer != null) return

        currentActivity = activity
        unityPlayer = UnityPlayer(activity)

        // 创建统一容器，只创建一次
        unityContainer = FrameLayout(activity).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // UnityView 只添加一次，永不移除
        unityContainer?.addView(
            unityPlayer?.view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        isInitialized = true
    }

    /**
     * 将 Unity 容器附加到指定的 Fragment 容器
     * 使用 bringToFront 而不是 remove/add，避免 Surface 重建
     */
    fun attachToFragment(container: ViewGroup) {
        if (unityContainer == null) return

        // 如果已经在目标容器中，无需操作
        if (unityContainer?.parent === container) {
            unityContainer?.visibility = View.VISIBLE
            resumeUnity()
            return
        }

        // 从旧父容器移除（但不销毁 Surface）
        (unityContainer?.parent as? ViewGroup)?.let { oldParent ->
            // 先隐藏，再移除，减少闪动
            unityContainer?.visibility = View.INVISIBLE
            oldParent.removeView(unityContainer)
        }

        // 添加到新容器
        container.addView(
            unityContainer,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // 延迟显示，让布局完成
        unityContainer?.post {
            unityContainer?.visibility = View.VISIBLE
            resumeUnity()
        }
    }

    /**
     * 隐藏 Unity（切换到其他 Tab 时）
     */
    fun detachFromFragment() {
        unityContainer?.visibility = View.INVISIBLE
        pauseUnity()
    }

    /**
     * 获取 Unity 容器（用于检查是否已附加）
     */
    fun getUnityContainer(): View? = unityContainer

    fun isUnityInitialized(): Boolean = isInitialized

    private fun resumeUnity() {
        unityPlayer?.onResume()
        unityPlayer?.windowFocusChanged(true)
        unityPlayer?.view?.requestFocus()
    }

    private fun pauseUnity() {
        unityPlayer?.windowFocusChanged(false)
        unityPlayer?.onPause()
    }

    fun destroy() {
        unityPlayer?.quit()
        unityPlayer = null
        unityContainer = null
        isInitialized = false
        currentActivity = null
    }
}
