package com.example.pffbrowser.jsbridge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pffbrowser.databinding.PbActivityJsbridgeDemoBinding
import com.example.pffbrowser.jsbridge.module.impl.DeviceModule
import com.example.pffbrowser.jsbridge.module.impl.NavigationModule
import com.example.pffbrowser.jsbridge.module.impl.NetworkModule
import com.example.pffbrowser.jsbridge.module.impl.StorageModule
import com.example.pffbrowser.jsbridge.module.impl.UIModule
import com.example.pffbrowser.jsbridge.security.SecurityConfig

/**
 * JSBridge演示Activity
 */
class JSBridgeDemoActivity : AppCompatActivity() {

    private lateinit var binding: PbActivityJsbridgeDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PbActivityJsbridgeDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initWebView()
        loadDemoPage()
    }

    private fun initWebView() {
        val jsBridge = binding.webView.getJSBridge() ?: return

        // 注册所有模块
        jsBridge.registerModule(UIModule(this))
        jsBridge.registerModule(StorageModule(this))
        jsBridge.registerModule(DeviceModule(this))
        jsBridge.registerModule(NetworkModule(this))
        jsBridge.registerModule(NavigationModule(this, binding.webView))

        // 设置安全配置（开发环境允许所有域名）
        jsBridge.setSecurityConfig(
            SecurityConfig(
                allowedDomains = emptyList(),
                allowLocalhost = true,
                forceHttps = false
            )
        )
    }

    private fun loadDemoPage() {
        // 加载演示页面
        binding.webView.loadUrl("file:///android_asset/jsbridge_demo.html")
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webView.destroy()
    }
}
