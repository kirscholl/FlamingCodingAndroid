package com.example.flamingcoding.retrofit

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flamingcoding.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitTestActivity : AppCompatActivity() {

    companion object {
        const val TAG = "RetrofitTestActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_retrofit_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val reqButton = findViewById<Button>(R.id.retroBeginRequestBtn)
        reqButton.setOnClickListener { v ->
            testRetroReq()
        }
    }

    fun testRetroReq() {
        // 发起retrofit请求
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com")
            // 在构建Retrofit对象时，指明使用的Converter工厂，否则Crash！！！
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: TestServerInterface = retrofit.create(TestServerInterface::class.java)
        val repos: Call<List<Repo>> = service.listRepos("octocat")
        // 同步
//        repos.execute()
        // 异步
        repos.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(
                call: Call<List<Repo>?>,
                response: Response<List<Repo>?>
            ) {
                // retrofit的onResponse()是在UI主线程中回调！！！
                val textView = findViewById<TextView>(R.id.retroTestTextView)
                textView.text = response.code().toString()
            }

            override fun onFailure(
                call: Call<List<Repo>?>,
                t: Throwable
            ) {
                
            }
        })
    }
}