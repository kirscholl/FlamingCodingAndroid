package com.example.flamingcoding.androidTrials

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flamingcoding.R

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ActivityLifeCycle", "onCreate")
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val extraData = intent.getStringExtra("extra_data")
        Log.d("SecondActivity", "extra data is $extraData")
        initView()
    }

    private fun initView() {
        val button1: Button = findViewById(R.id.SecondButton1)
        button1.setOnClickListener {
            // 跳转到HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val button2: Button = findViewById(R.id.SecondButton2)
        button2.setOnClickListener {
            // 销毁SecondActivity并传递数据
            val intent = Intent()
            intent.putExtra("data_return", "Second Finish To Next Activity")
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("ActivityLifeCycle", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("ActivityLifeCycle", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("ActivityLifeCycle", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("ActivityLifeCycle", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ActivityLifeCycle", "onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("ActivityLifeCycle", "onRestart")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // 调用的是getMenuInflater()
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_item -> Toast.makeText(
                this, "You clicked Add",
                Toast.LENGTH_SHORT
            ).show()

            R.id.remove_item -> Toast.makeText(
                this, "You clicked Remove",
                Toast.LENGTH_SHORT
            ).show()
        }
        return true
    }
}