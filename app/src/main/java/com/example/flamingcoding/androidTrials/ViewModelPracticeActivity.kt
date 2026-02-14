package com.example.flamingcoding.androidTrials

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.flamingcoding.R

class ViewModelPracticeActivity : AppCompatActivity() {

    lateinit var viewModel: PracticeViewModel
    lateinit var plusOneButton: Button
    lateinit var infoText: TextView
    lateinit var clearBtn: Button
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
    }

    private fun initView() {
        plusOneButton = findViewById(R.id.plusOneBtn)
        infoText = findViewById(R.id.infoText)
        clearBtn = findViewById(R.id.clearBtn)
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