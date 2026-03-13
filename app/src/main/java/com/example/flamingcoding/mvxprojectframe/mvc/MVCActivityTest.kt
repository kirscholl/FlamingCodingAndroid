package com.example.flamingcoding.mvxprojectframe.mvc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flamingcoding.R


// xml -> View
// activity -> Controller
// MVCModel -> Model
class MVCActivityTest : AppCompatActivity() {

    lateinit var model: MVCModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mvctest)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initData()
    }

    fun initData() {
        model = MVCModel()
    }

    fun showData() {
        val data = model.getData()
        for (d in data) {
            //...
        }
    }
}