package com.example.flamingcoding.aidltest

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class AidlService : Service() {

    private val TAG = "AidlService"
    private val books = ArrayList<Book>()  // 改用 ArrayList 并重命名

    private val binder = object : IAIDLTestService.Stub() {
        override fun addBook(book: Book?) {
            book?.let {
                books.add(it)
                Log.d(TAG, "addBook: ${it.bookName}, current count: ${books.size}")
            }
        }

        override fun getBookList(): MutableList<Book> {
            Log.d(TAG, "getBookList: size = ${books.size}")
            return ArrayList(books)
        }

        override fun getBookCount(): Int {
            val count = books.size
            Log.d(TAG, "getBookCount: $count")
            return count
        }

        override fun calculate(a: Int, b: Int): Int {
            val result = a + b
            Log.d(TAG, "calculate: $a + $b = $result")
            return result
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "AidlService onCreate")
        // 初始化一些测试数据
        books.add(Book(1, "Android开发艺术探索"))
        books.add(Book(2, "第一行代码"))
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "AidlService onBind")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "AidlService onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "AidlService onDestroy")
    }
}
