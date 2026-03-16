package com.example.flamingcoding.common

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flamingcoding.Algorithm.MaxFrequency
import com.example.flamingcoding.R
import com.example.flamingcoding.dagger2Hilt.chaintest.Test1
import dagger.hilt.android.AndroidEntryPoint
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
            val maxFrequency = MaxFrequency()
            maxFrequency.maxFrequency(intArrayOf(1, 2, 4), 5)
        }
        println("自动Inject测试：${test.testStr}")
    }
}