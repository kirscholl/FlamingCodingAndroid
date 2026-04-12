package com.example.flamingcoding.aidltest

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flamingcoding.R

class AidlTestActivity : AppCompatActivity() {

    private val TAG = "AidlTestActivity"
    private var remoteService: IAIDLTestService? = null
    private var isServiceBound = false

    private lateinit var btnTestAidl: Button
    private lateinit var tvResult: TextView

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            remoteService = IAIDLTestService.Stub.asInterface(service)
            isServiceBound = true
            Log.d(TAG, "AIDL Service connected")
            appendResult("✓ AIDL服务已连接\n")
            Toast.makeText(this@AidlTestActivity, "AIDL服务已连接", Toast.LENGTH_SHORT).show()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            remoteService = null
            isServiceBound = false
            Log.d(TAG, "AIDL Service disconnected")
            appendResult("✗ AIDL服务已断开\n")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_aidl_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        bindAidlService()
    }

    private fun initView() {
        btnTestAidl = findViewById(R.id.btnTestAidl)
        tvResult = findViewById(R.id.tvResult)

        btnTestAidl.setOnClickListener {
            testAidlCommunication()
        }
    }

    private fun bindAidlService() {
        val intent = Intent(this, AidlService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        appendResult("正在连接AIDL服务...\n")
    }

    private fun testAidlCommunication() {
        Log.d(TAG, "testAidlCommunication called, isServiceBound=$isServiceBound, remoteService=$remoteService")

        if (!isServiceBound) {
            Toast.makeText(this, "AIDL服务未连接", Toast.LENGTH_SHORT).show()
            appendResult("✗ AIDL服务未连接（isServiceBound=false），请稍后重试\n")
            Log.e(TAG, "Service not bound")
            return
        }

        if (remoteService == null) {
            Toast.makeText(this, "AIDL服务对象为空", Toast.LENGTH_SHORT).show()
            appendResult("✗ AIDL服务对象为空（remoteService=null），请稍后重试\n")
            Log.e(TAG, "remoteService is null")
            return
        }

        try {
            tvResult.text = "开始测试AIDL跨进程通信...\n\n"

            // 1. 测试计算方法
            val a = 10
            val b = 20
            Log.d(TAG, "Calling calculate($a, $b)")
            val result = remoteService!!.calculate(a, b)
            appendResult("【测试1】计算方法\n")
            appendResult("  $a + $b = $result\n\n")
            Log.d(TAG, "Calculate: $a + $b = $result")

            // 2. 获取初始书籍数量
            Log.d(TAG, "Calling getBookCount()")
            val initialCount = remoteService!!.bookCount
            appendResult("【测试2】获取书籍数量\n")
            appendResult("  初始书籍数量: $initialCount\n\n")
            Log.d(TAG, "Initial book count: $initialCount")

            // 3. 获取初始书籍列表
            Log.d(TAG, "Calling getBookList()")
            val initialBooks = remoteService!!.bookList
            appendResult("【测试3】获取书籍列表\n")
            initialBooks.forEachIndexed { index, book ->
                appendResult("  ${index + 1}. ${book.bookName} (ID: ${book.bookId})\n")
                Log.d(TAG, "Book: ${book.bookId} - ${book.bookName}")
            }
            appendResult("\n")

            // 4. 添加新书
            val newBook = Book(3, "Kotlin核心编程")
            Log.d(TAG, "Calling addBook($newBook)")
            remoteService!!.addBook(newBook)
            appendResult("【测试4】添加新书\n")
            appendResult("  已添加: ${newBook.bookName}\n\n")
            Log.d(TAG, "Added new book: ${newBook.bookName}")

            // 5. 再次获取书籍数量
            Log.d(TAG, "Calling getBookCount() again")
            val newCount = remoteService!!.bookCount
            appendResult("【测试5】再次获取书籍数量\n")
            appendResult("  当前书籍数量: $newCount\n\n")
            Log.d(TAG, "New book count: $newCount")

            // 6. 再次获取书籍列表
            Log.d(TAG, "Calling getBookList() again")
            val updatedBooks = remoteService!!.bookList
            appendResult("【测试6】获取更新后的书籍列表\n")
            updatedBooks.forEachIndexed { index, book ->
                appendResult("  ${index + 1}. ${book.bookName} (ID: ${book.bookId})\n")
            }
            appendResult("\n")

            appendResult("=============================\n")
            appendResult("✓ AIDL跨进程通信测试完成！\n")
            appendResult("=============================\n")

            Toast.makeText(this, "AIDL测试成功！", Toast.LENGTH_SHORT).show()

        } catch (e: RemoteException) {
            Log.e(TAG, "AIDL communication error", e)
            appendResult("\n✗ AIDL通信失败: ${e.message}\n")
            appendResult("异常类型: ${e.javaClass.simpleName}\n")
            appendResult("堆栈信息: ${e.stackTraceToString()}\n")
            Toast.makeText(this, "AIDL通信失败: ${e.message}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error", e)
            appendResult("\n✗ 发生未知错误: ${e.message}\n")
            appendResult("异常类型: ${e.javaClass.simpleName}\n")
            appendResult("堆栈信息: ${e.stackTraceToString()}\n")
            Toast.makeText(this, "发生错误: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun appendResult(text: String) {
        tvResult.append(text)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
            Log.d(TAG, "Unbind AIDL service")
        }
    }
}