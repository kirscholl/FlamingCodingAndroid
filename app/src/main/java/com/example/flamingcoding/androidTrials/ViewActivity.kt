package com.example.flamingcoding.androidTrials

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flamingcoding.R

class ViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
    }

    private fun initView() {
        val button = findViewById<Button>(R.id.btn_view_activity)
        val image = findViewById<ImageView>(R.id.image_view_view_activity)
        val editText = findViewById<EditText>(R.id.edit_text_view_activity)
        val progressbar = findViewById<ProgressBar>(R.id.progress_bar_view_activity)

        button.setOnClickListener { v ->
            // 动态设置图片
            image.setImageResource(R.drawable.board_icon_nine)
            // 动态获取EditText的内容
            val editCurrentText = editText.text
            val builder = StringBuilder().apply {
                append("ViewActivity Edit Text Is : ")
                append(editCurrentText)
            }
            Log.d("ViewActivity", builder.toString())

            progressbar.progress += 10;
            if (progressbar.progress == 100) {
                progressbar.progress = 0
            }

            AlertDialog.Builder(this).apply {
                setTitle("This is Dialog")
                setMessage("Something important.")
                setCancelable(false)
                setPositiveButton("OK") { dialog, which ->
                }
                setNegativeButton("Cancel") { dialog, which ->
                }
                show()
            }
        }

    }
}