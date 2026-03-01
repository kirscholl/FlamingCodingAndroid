package com.example.flamingcoding.kotlinCoroutinesTrials

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.flamingcoding.R
import com.example.flamingcoding.retrofitOkHttpDev.Repo
import com.example.flamingcoding.retrofitOkHttpDev.RetrofitServerInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CoroutinesTestActivity : ComponentActivity() {

    companion object {
        const val TAG = "CoroutinesTestActivity"
    }

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
            val note = KotlinCoroutinesNote()
            note.coroutinesException()
        }

        class CorViewModel : ViewModel() {
            fun createViewModelCoroutines() {
                // 开启自动监听ViewModel生命周期的协程
                viewModelScope.launch {

                }
            }
        }

        // 开启自动监听Activity生命周期的协程
        lifecycleScope.launch {

        }
    }

    suspend fun retroRequest(hostName: String, user: String): List<Repo> {
        val retro = Retrofit.Builder()
            .baseUrl(hostName)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retroService = retro.create(RetrofitServerInterface::class.java)
        // 调用了其他挂起函数
        return retroService.listReposCor(user)
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

    fun lifeScopeTest() {
        fun startLifeScope() = lifecycleScope.launch {
            //
        }

        val job1 = startLifeScope()
        val job2 = startLifeScope()
        // 单个取消
        job1.cancel()
        // 取消所有lifecycleScope创建的协程
        // 是多个和多层的 会取消所有由lifecycleScope所创建的多个协程，会取消lifecycleScope内部所创建的所有协程
        lifecycleScope.cancel()
    }

    // 多个协程并行
    fun coroutinesMultiRun() {
        lifecycleScope.launch {
            // 使用launch 实际上这两个协程会并行执行
            val repos1 = retroRequest("https://api.github.com", "octocat")
            val repos2 = retroRequest("https://api.github.com", "octocat")
            Log.d("TAG", repos1.toString() + repos2.toString())
        }

        // 使用async使两个协程并发执行
        val async1 = lifecycleScope.async {
            retroRequest("https://api.github.com", "octocat")
        }
        val async2 = lifecycleScope.async {
            retroRequest("https://api.github.com", "octocat")
        }
        lifecycleScope.launch {
            val res1 = async1.await()
            val res2 = async2.await()
            Log.d(TAG, res1.toString() + res2.toString())
        }

        fun initData(): String {
            return "111"
        }

        fun processData() {
            //...
        }

        lifecycleScope.launch {
            val initJob = launch {
                // #1.初始化数据
                initData()
            }
            // #2.发起一个网络请求
            val repos1 = retroRequest("https://api.github.com", "octocat")

            // 因为#1和#2互相不关联，所以#1和#2可以在两个协程中去分别执行
            // #3.处理初始化数据
            // 但是#3是和#1相关联的，所以#3要等待#1的完成
            // 使用join等待#1的完成，该方法不关心协程的返回值，但是协程一旦结束join就返回，因此这里使用join会比async更合适
            initJob.join()
            processData()

//            val initJob = async {
//                initData()
//            }
//            val repos1 = retroRequest("https://api.github.com", "octocat")
//            // 使用join等待#1的完成，该方法不关心协程的返回值，但是协程一旦结束join就返回，因此这里使用join会比async更合适
//            initJob.await()
//            processData()
        }
    }

    fun retroRequestWithCallback(baseUrl: String, user: String): Call<List<Repo>> {
        val retro = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val service = retro.create(RetrofitServerInterface::class.java)
        val repo = service.listRepos(user)
        return repo
//        repo.enqueue(object : Callback<List<Repo>> {
//            override fun onResponse(
//                call: Call<List<Repo>?>,
//                response: Response<List<Repo>?>
//            ) {
//                Log.d(TAG, "${response.code()}")
//            }
//
//            override fun onFailure(
//                call: Call<List<Repo>?>,
//                t: Throwable
//            ) {
//                //...
//            }
//        })
    }

    private suspend fun requestWithCallbackInvoke(): List<Repo>? {
        return suspendCoroutine {
            retroRequestWithCallback("https://api.github.com", "octocat")
                .enqueue(object : Callback<List<Repo>> {
                    override fun onResponse(
                        call: Call<List<Repo>?>,
                        response: Response<List<Repo>?>
                    ) {
                        it.resume(response.body()!!)
                    }

                    override fun onFailure(
                        call: Call<List<Repo>?>,
                        t: Throwable
                    ) {
                        it.resumeWithException(t)
                    }
                })
        }
    }

    // kotlin协程与回调函数的交互
    fun coroutinesThreadInter() {
        lifecycleScope.launch {
            // 使用该函数如果其中有调用传统Java回调式函数则会将此传统函数转化成一个挂起函数
            val response = suspendCoroutine<List<Repo>?> {
                retroRequestWithCallback("https://api.github.com", "octocat")
                    .enqueue(object : Callback<List<Repo>> {
                        override fun onResponse(
                            call: Call<List<Repo>?>,
                            response: Response<List<Repo>?>
                        ) {
                            it.resume(response.body())
                        }

                        override fun onFailure(
                            call: Call<List<Repo>?>,
                            t: Throwable
                        ) {
                            // 在请求失败时立即停止协程并抛出异常
                            it.resumeWithException(t)
                        }
                    })
            }
        }

        lifecycleScope.launch {
            // 支持取消的
            val response = suspendCancellableCoroutine {
                it.invokeOnCancellation { it ->
                    // 注册在协程取消的时候的回调
                    Log.d("TAG", it.toString())
                }
                retroRequestWithCallback("123", "231")
                    .enqueue(object : Callback<List<Repo>> {
                        override fun onResponse(
                            call: Call<List<Repo>?>,
                            response: Response<List<Repo>?>
                        ) {
                            it.resume(response.body())
                        }

                        override fun onFailure(
                            call: Call<List<Repo>?>,
                            t: Throwable
                        ) {
                            // 在请求失败时立即停止协程并抛出异常
                            it.resumeWithException(t)
                        }
                    })
            }
        }

        lifecycleScope.launch {
            try {
                val response = requestWithCallbackInvoke()
                Log.e(TAG, response.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }
}