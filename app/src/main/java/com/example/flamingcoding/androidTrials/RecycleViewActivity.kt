package com.example.flamingcoding.androidTrials

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.flamingcoding.R

class RecycleViewActivity : AppCompatActivity() {

    private val fruitList = ArrayList<Fruit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recycle_view)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        initFruits() // 初始化水果数据
//        val layoutManager = LinearLayoutManager(this)
//        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        val adapter = FruitRecycleViewAdapter(fruitList)
        recyclerView.adapter = adapter
        recyclerView.setOnClickListener { v ->
            Log.d("RecycleViewActivity", v.toString())
        }
    }

    private fun initFruits() {
        repeat(2) {
            fruitList.add(Fruit(getRandomLengthString("Banana"), R.drawable.ic_launcher_foreground))
            fruitList.add(Fruit(getRandomLengthString("Apple"), R.drawable.ic_launcher_foreground))
            fruitList.add(Fruit(getRandomLengthString("Orange"), R.drawable.ic_launcher_foreground))
            fruitList.add(
                Fruit(
                    getRandomLengthString("Watermelon"),
                    R.drawable.ic_launcher_foreground
                )
            )
            fruitList.add(Fruit(getRandomLengthString("Pear"), R.drawable.ic_launcher_foreground))
            fruitList.add(Fruit(getRandomLengthString("Grape"), R.drawable.ic_launcher_foreground))
            fruitList.add(
                Fruit(
                    getRandomLengthString("Pineapple"),
                    R.drawable.ic_launcher_foreground
                )
            )
            fruitList.add(
                Fruit(
                    getRandomLengthString("Strawberry"),
                    R.drawable.ic_launcher_foreground
                )
            )
            fruitList.add(Fruit(getRandomLengthString("Cherry"), R.drawable.ic_launcher_foreground))
            fruitList.add(Fruit(getRandomLengthString("Mango"), R.drawable.ic_launcher_foreground))
        }
    }

    private fun getRandomLengthString(str: String): String {
        val n = (1..20).random()
        val builder = StringBuilder()
        repeat(n) {
            builder.append(str)
        }
        return builder.toString()
    }
}