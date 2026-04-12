package com.example.flamingcoding.dagger2hilt

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flamingcoding.R
import com.example.flamingcoding.databinding.ActivityDaggerTestBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DaggerTestActivity : AppCompatActivity() {

    companion object {
        const val TAG = "DaggerTestActivity"
    }

    private lateinit var binding: ActivityDaggerTestBinding

    // 告诉 Dagger，我需要一个injectDaggerTest对象，请注入进来
    @Inject
    lateinit var injectDaggerTest: InjectDaggerTest

    @Inject
    lateinit var withProviderDaggerTest: WithProviderDaggerTest

    @Inject
    @MultiOne
    lateinit var multiOneDaggerTest: MultiOneDaggerTest

    @Inject
    @MultiOther
    lateinit var multiOtherTest: MultiOtherDaggerTest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaggerTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. 创建 Component (这是Dagger2生成的类)
        // 2. 执行注入，将 studentDaggerTest 对象注入到当前 Activity 中
        // 这里生成的类前缀会多一个Dagger
        // 使用Hilt的话就不需要手动注入Component了，根据声明周期绑定好了
//        DaggerDaggerTestComponent.create().inject(this)

        binding.daggerTestBtn.setOnClickListener {
            Log.d(TAG, "InjectDaggerTest value: ${injectDaggerTest.getDaggerValue()}")
            Log.d(TAG, "WithProviderDaggerTest value: ${withProviderDaggerTest.getDaggerValue()}")
            Log.d(TAG, "MultiOneDaggerTest value: ${multiOneDaggerTest.getDaggerData()}")
            Log.d(TAG, "MultiOneDaggerTest value: ${multiOtherTest.getDaggerData()}")
        }
    }
}