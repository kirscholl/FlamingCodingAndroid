package com.example.flamingcoding.customViewTrials

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flamingcoding.R
import com.example.flamingcoding.databinding.ActivityViewRenderProcessTestBinding
import kotlin.concurrent.thread

class ViewRenderProcessTestActivity : AppCompatActivity() {

    lateinit var binding: ActivityViewRenderProcessTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewRenderProcessTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        thread {
//            sleep(1000)
//            binding.testViewTv.text = "初始化之后延迟更新"
//        }

        binding.testViewTv.setOnClickListener {
            binding.testViewTv.text = "点击TextView本身更新"
        }

        binding.testBtnView1.setOnClickListener {
            thread {
                binding.testViewTv.text = "点击按钮1更新"
            }
        }
        binding.testBtnView2.setOnClickListener {
            thread {
                // 此时将TextView布局从固定dp改成了 wrapContent -> 崩溃
                binding.testViewTv.text = "点击按钮2更新"
            }
        }
        binding.testBtnView3.setOnClickListener {
            binding.testViewTv.requestLayout()
            thread {
                // 此时将TextView布局从固定dp改成了 wrapContent
                // 但是先调用TextView的requestLayout
                binding.testViewTv.text = "点击按钮3更新"
            }
        }
    }
}