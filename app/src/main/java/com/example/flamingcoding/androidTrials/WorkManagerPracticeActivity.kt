package com.example.flamingcoding.androidTrials

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flamingcoding.R
import com.example.flamingcoding.databinding.ActivityWorkManagerPracticeBinding

class WorkManagerPracticeActivity : AppCompatActivity() {
    lateinit var binding: ActivityWorkManagerPracticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWorkManagerPracticeBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_work_manager_practice)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}