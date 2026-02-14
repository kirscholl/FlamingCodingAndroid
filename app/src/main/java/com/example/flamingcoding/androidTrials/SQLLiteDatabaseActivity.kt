package com.example.flamingcoding.androidTrials

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flamingcoding.R

class SQLLiteDatabaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sqllite_data_base)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val dbHelper = PracticeDatabaseHelper(this, "BookStore.db", 4)

        val createDatabaseButton = findViewById<Button>(R.id.createDatabaseButton)
        val insertDatabaseButton = findViewById<Button>(R.id.insertDatabaseButton)
        val queryDatabaseButton = findViewById<Button>(R.id.queryDatabaseButton)


        createDatabaseButton.setOnClickListener {
            dbHelper.writableDatabase
        }

        insertDatabaseButton.setOnClickListener {
            val db = dbHelper.writableDatabase
            val values1 = ContentValues().apply {
                // 开始组装第一条数据
                put("name", "The Da Vinci Code")
                put("author", "Dan Brown")
                put("pages", 454)
                put("price", 16.96)
            }
            db.insert("Book", null, values1) // 插入第一条数据
            val values2 = ContentValues().apply {
                // 开始组装第二条数据
                put("name", "The Lost Symbol")
                put("author", "Dan Brown")
                put("pages", 510)
                put("price", 19.95)
            }
            db.insert("Book", null, values2) // 插入第二条数据
        }

        queryDatabaseButton.setOnClickListener { v ->
            val db = dbHelper.writableDatabase
            // 查询Book表中所有的数据
            val cursor = db.query("Book", null, null, null, null, null, null)
            if (cursor.moveToFirst()) {
                do {
                    // 遍历Cursor对象，取出数据并打印
                    val name = cursor.getString(cursor.run { getColumnIndex("name") })
                    val author = cursor.getString(cursor.run { getColumnIndex("author") })
                    val pages = cursor.getInt(cursor.run { getColumnIndex("pages") })
                    val price = cursor.getDouble(cursor.run { getColumnIndex("price") })
                    Log.d("SQLLiteDatabaseActivity", "book name is $name")
                    Log.d("SQLLiteDatabaseActivity", "book author is $author")
                    Log.d("SQLLiteDatabaseActivity", "book pages is $pages")
                    Log.d("SQLLiteDatabaseActivity", "book price is $price")
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
    }
}