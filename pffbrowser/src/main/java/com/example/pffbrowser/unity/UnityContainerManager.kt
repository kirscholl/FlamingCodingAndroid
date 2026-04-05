package com.example.pffbrowser.unity

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.unity3d.player.UnityPlayer

@SuppressLint("StaticFieldLeak")
object UnityContainerManager {

    private var unityPlayer: UnityPlayer? = null
    private var unityContainer: FrameLayout? = null
    private var rootContainer: ViewGroup? = null  // Activity 的根容器
    private var isInitialized = false
    private var currentActivity: Activity? = null
    private var currentScene: String = "game"  // 当前场景标识

    /**
     * 在 Activity.onCreate() 中初始化
     * 只调用一次，创建 Unity 并添加到 Activity 根容器
     */
    fun initialize(activity: Activity, root: ViewGroup) {
        if (isInitialized) return

        currentActivity = activity
        rootContainer = root

        // 创建 UnityPlayer
        unityPlayer = UnityPlayer(activity)

        // 创建 Unity 容器，全屏
        unityContainer = FrameLayout(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            visibility = View.INVISIBLE  // 默认隐藏
            isClickable = true  // 确保可以接收触摸事件
            isFocusable = true
        }

        // Unity View 只添加一次，永不移除
        unityContainer?.addView(
            unityPlayer?.view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // 添加到 Activity 根容器
        root.addView(unityContainer)

        isInitialized = true
    }

    /**
     * 显示 Unity（切换到 Unity Tab 时调用）
     * @param scene 要切换的场景: "game" 或 "mine"
     */
    fun showUnity(scene: String) {
        if (!isInitialized) return

        // 确保在 NavHost 之上
        unityContainer?.bringToFront()
        unityContainer?.visibility = View.VISIBLE

        // 请求焦点以接收输入
        unityContainer?.requestFocus()
        unityPlayer?.view?.requestFocus()

        // 恢复渲染
        unityPlayer?.onResume()
        unityPlayer?.windowFocusChanged(true)

        // 场景切换（如果变化了）
        if (currentScene != scene) {
            currentScene = scene
            // 通知 Unity 切换场景
            notifyUnitySceneChange(scene)
        }
    }

    /**
     * 隐藏 Unity（切换到非 Unity Tab 时调用）
     */
    fun hideUnity() {
        if (!isInitialized) return

        unityContainer?.visibility = View.INVISIBLE

        // 暂停渲染以节省资源，但 Surface 保持
        unityPlayer?.windowFocusChanged(false)
        unityPlayer?.onPause()
    }

    /**
     * 检查 Unity 是否正在显示
     */
    fun isUnityVisible(): Boolean {
        return unityContainer?.visibility == View.VISIBLE
    }

    /**
     * 获取当前场景
     */
    fun getCurrentScene(): String = currentScene

    /**
     * 发送消息给 Unity 切换场景
     */
    private fun notifyUnitySceneChange(scene: String) {
        // 通过 UnityPlayer.UnitySendMessage 或接口通知 Unity
        // 具体实现根据 Unity 项目而定
        // unityPlayer?.UnitySendMessage("SceneManager", "SwitchScene", scene)
    }

    /**
     * 将 Unity 容器置于最底层（用于非 Unity Tab 时）
     * 可选：如果不想用 INVISIBLE，可以把 Unity 放到 NavHost 下面
     */
    fun sendToBack() {
        val navHost =
            rootContainer?.findViewById<View>(com.example.pffbrowser.R.id.home_host_fragment)
        navHost?.bringToFront()
        // BottomNavigationView 也要 bringToFront
        val bottomNav =
            rootContainer?.findViewById<View>(com.example.pffbrowser.R.id.home_bottom_navigation_view)
        bottomNav?.bringToFront()
    }

    fun destroy() {
        unityPlayer?.quit()
        unityPlayer = null
        unityContainer = null
        rootContainer = null
        isInitialized = false
        currentActivity = null
    }
}
