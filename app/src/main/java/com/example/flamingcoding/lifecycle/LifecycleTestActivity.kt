package com.example.flamingcoding.lifecycle

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.flamingcoding.R

class LifecycleTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lifecycle_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 将Activity中的lifecycle与自定义的DefaultLifecycleObserver(LifecycleObserver)绑定
        lifecycle.addObserver(TestLifecycleObserver2())

        // 将自定义的testViewModel与Activity(ViewModelStoreOwner)绑定
        val testViewModel = ViewModelProvider(this)[TestViewModel::class.java]

        // 将LiveData与Activity(LifecycleOwner)绑定
        testViewModel.testLiveData.observe(this, Observer {

        })
    }
}