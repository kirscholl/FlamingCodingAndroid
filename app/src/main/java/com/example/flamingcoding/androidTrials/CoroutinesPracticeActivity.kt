package com.example.flamingcoding.androidTrials

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.flamingcoding.R
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CoroutinesPracticeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_coroutines_practice)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // Global.launch函数每次创建的都是一个顶层协程，这种协程当应用程序运行结束时也会跟着一起结束
//        GlobalScope.launch {
//            Log.d("CoroutinesPracticeActivity", "codes run in coroutine scope")
//        }

        // runBlocking函数同样会创建一个协程的作用域，但是它可以保证在协程作用域内的所有代码和子协程没有全部执行完之前一直阻塞当前线程
//        runBlocking {
//            Log.d("CoroutinesPracticeActivity", "codes run in coroutine scope")
//            delay(1500)
//            Log.d("CoroutinesPracticeActivity", "codes run in coroutine scope finished")
//        }

        // 创建多个协程
//        runBlocking {
//            launch {
//                println("launch1")
//                delay(1000)
//                println("launch1 finished")
//            }
//            launch {
//                println("launch2")
//                delay(1000)
//                println("launch2 finished")
//            }
//        }

        //suspend关键字，使用它可以将任意函数声明成挂起函数，而挂起函数之间都是可以互相调用的
        // suspend关键字只能将一个函数声明成挂起函数，是无法给它提供协程作用域的
        // coroutineScope函数可以保证其作用域内的所有代码和子协程在全部执行完之前，外部的协程会一直被挂起

    }

    suspend fun printTest() = coroutineScope {
        launch {
            print("1")
            delay(100)
        }
    }

    fun asyncTest() {
        // 调用了async函数之后，代码块中的代码就会立刻开始执行。当调用await()方法时，如果代码块中的代码还没执行完，
        // 那么await()方法会将当前协程阻塞住，直到可以获得async函数的执行结果
        runBlocking {
            val result = async {
                5 + 5
            }.await()
            println(result)
        }
    }
}