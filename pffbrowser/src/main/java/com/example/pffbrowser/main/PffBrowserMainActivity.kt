package com.example.pffbrowser.main

import UnityPlayerController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseActivity
import com.example.pffbrowser.databinding.PbActivityPffBrowserMainBinding
import com.example.pffbrowser.unity.GameUnityFragment
import com.example.pffbrowser.unity.MineUnityFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PffBrowserMainActivity : BaseActivity<PbActivityPffBrowserMainBinding, PffBrowserMainVm>() {

    override fun onSupportNavigateUp(): Boolean {
        // TODO 返回键处理
        return false
    }

    override fun PbActivityPffBrowserMainBinding.initView() {
        // 获取 NavController
        val navController =
            (supportFragmentManager.findFragmentById(R.id.home_host_fragment) as NavHostFragment).navController
        // NavController与bottomView绑定
//        viewBinding.homeBottomNavigationView.setupWithNavController(navController)

        // 阻止 UnityFragment 被销毁（使用 hide/show 代替 replace）
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val transaction = supportFragmentManager.beginTransaction()

            supportFragmentManager.fragments.forEach { fragment ->
                if (fragment is GameUnityFragment || fragment is MineUnityFragment) {
                    if (destination.id == R.id.GameUnityFragment) {
                        transaction.show(fragment)
                    } else {
                        transaction.hide(fragment)
                    }
                }
            }
            transaction.commitNowAllowingStateLoss()
        }
        viewBinding.homeBottomNavigationView.setupWithNavController(navController)
        // TODO 根据不同情况控制bottomNavigationView的按钮数量
    }

    override fun onDestroy() {
        super.onDestroy()
        // 应用退出时销毁Unity
        UnityPlayerController.destroy()
    }
}