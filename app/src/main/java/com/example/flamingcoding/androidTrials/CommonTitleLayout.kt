package com.example.flamingcoding.androidTrials

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.example.flamingcoding.R

class CommonTitleLayout(context: Context, attributeSet: AttributeSet) :
    LinearLayout(context, attributeSet) {

    init {
        LayoutInflater.from(context).inflate(R.layout.common_title, this)
        val backBtn = findViewById<Button?>(R.id.titleBack)
        val editBtn = findViewById<Button?>(R.id.titleEdit)
        backBtn?.setOnClickListener {
            val activity = context as Activity
            activity.finish()
        }
        editBtn?.setOnClickListener {
            Toast.makeText(context, "You Click Edit Button", Toast.LENGTH_LONG).show()
        }
    }
}