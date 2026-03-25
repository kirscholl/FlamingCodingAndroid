import android.app.Activity
import android.view.ViewGroup
import com.example.pffbrowser.unity.BaseUnityFragment
import com.unity3d.player.UnityPlayer

object UnityPlayerController {
    // 内存泄漏风险，Activity被UnityPlayer持有会释放不了，在Activity中应该调用UnityPlayer.quit()
    private var mUnityPlayer: UnityPlayer? = null
    private var initFlag = false
    private var currentHost: BaseUnityFragment? = null

    fun getUnityPlayer(activity: Activity): UnityPlayer? {
        if (mUnityPlayer == null) {
            mUnityPlayer = UnityPlayer(activity)
            initFlag = true
        }
        return mUnityPlayer
    }

    fun attachToContainer(activity: Activity, container: ViewGroup) {
        val player = getUnityPlayer(activity) ?: return
        val unityView = player.view

        // 从当前parent移除（无论在哪个Fragment中）
        (unityView.parent as? ViewGroup)?.removeView(unityView)

        // 添加到新容器
        container.addView(
            unityView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        // 恢复渲染
        player.onResume()
        player.windowFocusChanged(true)
        unityView.requestFocus()
    }

    fun detachFromContainer() {
        mUnityPlayer?.let { player ->
//            player.windowFocusChanged(false)
//            player.onPause()

            player.view?.let { view ->
                (view.parent as? ViewGroup)?.removeView(view)
            }
        }
    }

    fun onPause() {
        mUnityPlayer?.let { player ->
            player.windowFocusChanged(false)
            player.onPause()
        }
    }

    fun setCurrentHost(host: BaseUnityFragment?) {
        currentHost = host
    }

    fun getCurrentHost(): Any? = currentHost

    fun isUnityInitialized(): Boolean = initFlag

    // 应用退出时调用
    fun destroy() {
        mUnityPlayer?.quit()
        mUnityPlayer = null
        initFlag = false
        currentHost = null
    }
}