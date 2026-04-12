package com.example.flamingcoding.mvx.mvp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flamingcoding.R

class MVPActivityTest : AppCompatActivity(), MVPPresenterInterface {
    lateinit var model: MVPModel
    lateinit var presenter: MVPPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mvptest)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initModel()
        initPresenter()
    }

    private fun initModel() {
        model = MVPModel()
    }

    private fun initPresenter() {
        presenter = MVPPresenter(this, model)
    }

    override fun showListData(dataList: List<Int>) {

    }
}