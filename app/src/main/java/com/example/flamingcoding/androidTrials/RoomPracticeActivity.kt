package com.example.flamingcoding.androidTrials

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.flamingcoding.databinding.ActivityRoomPracticeBinding
import kotlin.concurrent.thread

// ORM（Object Relational Mapping）也叫对象关系映射
// 用面向对象的思维来和数据库进行交互，绝大多数情况下不用再和SQL语句打交道
class RoomPracticeActivity : AppCompatActivity() {

    // 使用ViewBinding
    private lateinit var binding: ActivityRoomPracticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRoomPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val practiceDao = PracticeRoomDatabase.getDatabase(this).roomPracticeDao()
        val url1 = RoomPracticeData("https://www.baidu,com", "baidu", "baidu", 111)
        val url2 = RoomPracticeData("https://www.bing.com", "bing", "bing", 333)
        binding.addDataBtn.setOnClickListener {
            thread {
                url1.urlId = practiceDao.insertUrl(url1)
                url2.urlId = practiceDao.insertUrl(url2)
            }
        }
        binding.updateDataBtn.setOnClickListener {
            thread {
                url1.url = "https://www.huya.com"
                practiceDao.updateUrl(url1)
            }
        }
        binding.deleteDataBtn.setOnClickListener {
            thread {
                practiceDao.deleteUrlByLinkName("baidu")
            }
        }
        binding.queryDataBtn.setOnClickListener {
            thread {
                for (url in practiceDao.loadAllUrl()) {
                    Log.d("RoomPracticeActivity", url.toString())
                }
            }
        }
    }
}