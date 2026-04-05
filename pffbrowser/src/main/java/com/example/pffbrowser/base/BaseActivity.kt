package com.example.pffbrowser.base

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.example.pffbrowser.utils.LogUtil.logLifeCycle

abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity(),
    IBaseView<VB> {

    lateinit var viewModel: VM
    protected val viewBinding: VB by lazy(mode = LazyThreadSafetyMode.NONE) {
        BindingReflex.reflexViewBinding(javaClass, layoutInflater)
    }

    companion object {
        const val TAG = "BaseActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logLifeCycle(this, "onCreate")
        // 适配系统栏边距
        enableEdgeToEdge()
        setContentView(viewBinding.root)
        // 当窗口insets发生变化时（例如系统栏显示/隐藏、屏幕旋转、键盘弹出等），
        // 会回调该监听器根据 insets 调整视图的布局，从而避免内容被系统 UI 遮挡
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        viewModel = createViewModel()
        viewBinding.initView()
        viewBinding.setOnClickListener()
        initRequestData()
        initObserver()
    }

    override fun onStart() {
        logLifeCycle(this, "onStart")
        super.onStart()
    }

    override fun onResume() {
        logLifeCycle(this, "onResume")
        initViewObserver()
        super.onResume()
    }

    override fun onPause() {
        logLifeCycle(this, "onPause")
        super.onPause()
    }

    override fun onStop() {
        logLifeCycle(this, "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        logLifeCycle(this, "onDestroy")
        super.onDestroy()
    }

    // 自动创建 ViewModel
    private fun <VM : BaseViewModel> createViewModel(): VM {
        val vmClass = getViewModelClass<VM>(this)
        return ViewModelProvider(this)[vmClass]
    }

    override fun VB.initView() {
    }

    override fun VB.setOnClickListener() {
    }

    override fun initObserver() {
    }

    override fun initViewObserver() {

    }

    override fun initRequestData() {
    }

    override fun refreshRequestData() {

    }

    override fun reLoadData() {
    }

    override fun isRecreate(): Boolean {
        return false
    }
}