package com.example.pffbrowser.main

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseActivity
import com.example.pffbrowser.databinding.PbActivityPffBrowserMainBinding
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
        viewBinding.homeBottomNavigationView.setupWithNavController(navController)
        // TODO 根据不同情况控制bottomNavigationView的按钮数量
    }
}