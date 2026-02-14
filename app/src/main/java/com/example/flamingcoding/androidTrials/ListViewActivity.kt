package com.example.flamingcoding.androidTrials

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flamingcoding.R

class ListViewActivity : AppCompatActivity() {

//    private val data = listOf(
//        "Apple", "Banana", "Orange", "Watermelon",
//        "Pear", "Grape", "Pineapple", "Strawberry", "Cherry", "Mango",
//        "Apple", "Banana", "Orange", "Watermelon", "Pear", "Grape",
//        "Pineapple", "Strawberry", "Cherry", "Mango"
//    )
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_list_view)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data)
//        val listView = findViewById<ListView>(R.id.listView)
//        listView.adapter = adapter
//    }

    private val fruitList = ArrayList<Fruit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)
        initFruits() // 初始化水果数据
        val adapter = FruitListViewAdapter(this, R.layout.fruit_item, fruitList)
        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = adapter
        listView.setOnItemClickListener { parent, view, position, id ->
            val fruit = fruitList[position]
            Toast.makeText(this, fruit.name, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initFruits() {
        repeat(2) {
            fruitList.add(Fruit("Apple", R.drawable.ic_launcher_foreground))
            fruitList.add(Fruit("Banana", R.drawable.ic_launcher_foreground))
            fruitList.add(Fruit("Orange", R.drawable.ic_launcher_foreground))
            fruitList.add(Fruit("Watermelon", R.drawable.ic_launcher_foreground))
            fruitList.add(Fruit("Pear", R.drawable.ic_launcher_foreground))
            fruitList.add(Fruit("Grape", R.drawable.ic_launcher_foreground))
            fruitList.add(Fruit("Pineapple", R.drawable.ic_launcher_foreground))
            fruitList.add(Fruit("Strawberry", R.drawable.ic_launcher_foreground))
            fruitList.add(Fruit("Cherry", R.drawable.ic_launcher_foreground))
            fruitList.add(Fruit("Mango", R.drawable.ic_launcher_foreground))
        }
    }
}