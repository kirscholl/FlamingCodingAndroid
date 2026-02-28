package com.example.flamingcoding.kotlinCoroutinesTrials

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flamingcoding.R
import com.example.flamingcoding.retrofitOkHttpDev.RetrofitServerInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CoroutinesTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_coroutines_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val reqButton = findViewById<Button>(R.id.corBeginRequestBtn)
        reqButton.setOnClickListener { _ ->
            testRetroReq()
        }
    }

    fun testRetroReq() {
        CoroutineScope(Dispatchers.Main).launch {
            val retro = Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val retroService = retro.create(RetrofitServerInterface::class.java)
            val repos = retroService.listReposCor("octocat")
            val testTextView = findViewById<TextView>(R.id.corTestTextView)
            testTextView.text = repos[1].name
        }
    }
}