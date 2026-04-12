package com.example.flamingcoding.common

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.flamingcoding.R
import com.example.flamingcoding.algorithm.OtherAlgorithm
import com.example.flamingcoding.dagger2hilt.chaintest.Test1
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class FunctionalTestActivity : AppCompatActivity() {

    @Inject
    lateinit var test: Test1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_functional_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val reqButton = findViewById<Button>(R.id.functionalTestButton)
        reqButton.setOnClickListener { _ ->
//            val maxFrequency = MaxFrequency()
//            maxFrequency.maxFrequency(intArrayOf(1, 2, 4), 5)
//            test()

//            val k = KotlinCoroutinesNote()
//            k.awaitAllTest()

//            val t = ObjectTestSingleton
//            val str1 = ObjectTestSingleton.TEST_STRING
//            println(str1)
//            val h = HungryTestSingleton.getInstance()
//            val str2 = HungryTestSingleton.TEST_STRING
//            println(str2)

            // java 和suspend交互
//            val test = SuspendJavaTest()
//            test.callSuspendTestForJava()
//            test.callSuspendTestForJavaAsync()
//            test.callSuspend()

            val other = OtherAlgorithm()
            val arr = intArrayOf(8, 7, 4, 4, 3, 5, 4, 1, 2, 3, 5)
            other.quickSort(arr)
            for (i in arr) {
                println("排序结果：$i")
            }
        }
//        println("自动Inject测试：${test.testStr}")
    }

    fun test() {
        lifecycleScope.launch {
            val ld = flow {
                delay(500)
                emit(1)
                println("FunctionalTestActivity 1")
                delay(500)
                emit(1)
                println("FunctionalTestActivity 2")
                delay(500)
                emit(1)
                println("FunctionalTestActivity 3")
                delay(500)
                emit(1)
                println("FunctionalTestActivity 4")
            }.asLiveData()
            delay(1000)
            ld.observe(this@FunctionalTestActivity) {
                println("FunctionalTestActivity ob $it")
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch(Dispatchers.IO) {

                }
            }
        }
    }
}