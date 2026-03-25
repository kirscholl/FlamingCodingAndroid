package com.example.flamingcoding.firstLineCodeAndroid

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.flamingcoding.R

class ViewModelPracticeActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ViewModelPracticeActivity"
    }

    lateinit var viewModel: PracticeViewModel
    lateinit var plusOneButton: Button
    lateinit var infoText: TextView
    lateinit var clearBtn: Button
    lateinit var stickyButton: Button
    lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_view_model_practice)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        initView()

        sp = getPreferences(Context.MODE_PRIVATE)
        val countReserved = sp.getInt("count_reserved", 0)
        viewModel = ViewModelProvider(
            this,
            PracticeViewModelFactory(countReserved)
        )[PracticeViewModel::class.java]
//        viewModel = ViewModelProvider(this)[PracticeViewModel::class.java]
        plusOneButton.setOnClickListener { v ->
            viewModel.plusOne()
        }
        clearBtn.setOnClickListener {
            viewModel.clear()
        }

        viewModel.counter.observe(this, Observer { count ->
            infoText.text = count.toString()
        })
        refreshCounter()

        viewModel.stickyLiveDataTest.value = 1
        viewModel.stickyLiveDataTest.value = 2
        viewModel.stickyLiveDataTest.value = 3

        stickyButton.setOnClickListener {
            viewModel.stickyLiveDataTest.observe(this, Observer { count ->
                // 所谓粘性是指当值变化时候，注册时候还是会粘着最后一个值，并不是说所有的值都会再收到一次
                // 这里会收到3，粘着最后一个值
                // 粘性的原因是版本号和事件补发同时造成的
                // 在这里注册时版本号为-1 < 实际的3，并且此时的状态为INITIALIZED追到 -> CREATED
                Log.d(TAG, "粘性测试：${count}")
            })
        }
    }

    private fun initView() {
        plusOneButton = findViewById(R.id.plusOneBtn)
        infoText = findViewById(R.id.infoText)
        clearBtn = findViewById(R.id.clearBtn)
        stickyButton = findViewById(R.id.btn_sticky)
    }

    private fun refreshCounter() {
        infoText.text = viewModel.counter.toString()
    }

    override fun onPause() {
        super.onPause()
        sp.edit {
            putInt("count_reserved", viewModel.counter.value ?: 0)
        }
    }
}