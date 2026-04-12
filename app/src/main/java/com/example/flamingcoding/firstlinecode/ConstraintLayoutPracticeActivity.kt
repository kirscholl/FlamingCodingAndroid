package com.example.flamingcoding.firstlinecode

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flamingcoding.R

class ConstraintLayoutPracticeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_constraint_layout_prctice)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


//        val button = findViewById<Button>(R.id.groupTestButton)
//        button.setOnClickListener { v: View? ->
//            val group = findViewById<Group>(R.id.consTestGroup)
//            // 整体全部隐藏
////            group.visibility = View.GONE
//
//            val layer = findViewById<Layer>(R.id.consTestLayer)
//            // 整体旋转 位移
//            layer.rotation = 45f
//            layer.translationX = 100f
//            layer.translationY = 100f
//        }
    }
}