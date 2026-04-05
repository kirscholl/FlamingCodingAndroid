package com.example.pffbrowser.main

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseActivity
import com.example.pffbrowser.databinding.PbActivityPffBrowserMainBinding
import com.example.pffbrowser.unity.UnityContainerManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PffBrowserMainActivity : BaseActivity<PbActivityPffBrowserMainBinding, PffBrowserMainVm>() {

    // Unity Tab 的 ID 列表
    private val unityTabIds = setOf(R.id.GameUnityFragment, R.id.MineUnityFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化 Unity，传入根容器
        UnityContainerManager.initialize(this, viewBinding.root)
    }

    override fun PbActivityPffBrowserMainBinding.initView() {
        val navController =
            (supportFragmentManager.findFragmentById(R.id.home_host_fragment) as NavHostFragment).navController

        // 监听导航变化
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isUnityTab = destination.id in unityTabIds

            if (isUnityTab) {
                // Unity Tab：UnityContainer 会自动处理（在 Fragment.onResume 中）
                // 这里确保底部导航在顶层
                homeBottomNavigationView.bringToFront()
            } else {
                // 非 Unity Tab：确保 Unity 隐藏
                // 这一步是保险，正常应该在 Fragment.onPause 中处理了
                UnityContainerManager.hideUnity()

                // 确保 NavHost 在顶层
                homeHostFragment.bringToFront()
                homeBottomNavigationView.bringToFront()
            }
        }

        viewBinding.homeBottomNavigationView.setupWithNavController(navController)
    }

//    /**
//     * 处理返回键：如果 Unity 正在显示，先交给 Unity 处理
//     */
//    @Deprecated("Deprecated in Java")
//    override fun onBackPressed() {
//        if (UnityContainerManager.isUnityVisible()) {
//            // 通知 Unity 处理返回键
//            // 如果 Unity 不消费，再执行默认行为
//            // UnityPlayer.currentActivity.onBackPressed()
//        }
//        super.onBackPressed()
//    }
//


    override fun onDestroy() {
        super.onDestroy()
        UnityContainerManager.destroy()
    }
}
