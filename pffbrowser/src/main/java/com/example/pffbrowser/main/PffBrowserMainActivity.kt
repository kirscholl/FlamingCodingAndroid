package com.example.pffbrowser.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.pffbrowser.R
import com.example.pffbrowser.base.BaseActivity
import com.example.pffbrowser.databinding.PbActivityPffBrowserMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PffBrowserMainActivity : BaseActivity() {

    lateinit var binding: PbActivityPffBrowserMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = PbActivityPffBrowserMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 获取 NavController
        val navController =
            (supportFragmentManager.findFragmentById(R.id.home_host_fragment) as NavHostFragment).navController
        // NavController与bottomView绑定
        binding.homeBottomNavigationView.setupWithNavController(navController)
        // TODO 根据不同情况控制bottomNavigationView的按钮数量
    }

    override fun onSupportNavigateUp(): Boolean {
        // TODO 返回键处理
        return false
    }

}