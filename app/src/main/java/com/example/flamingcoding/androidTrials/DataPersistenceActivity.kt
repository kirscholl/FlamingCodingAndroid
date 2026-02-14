package com.example.flamingcoding.androidTrials

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.flamingcoding.R
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class DataPersistenceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_persistence)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
    }

    private fun initView() {
        val saveFileButton = findViewById<Button>(R.id.saveEditTextToFileButton)
        val editText = findViewById<EditText>(R.id.dataSaveEditText)
        val loadFileButton = findViewById<Button>(R.id.loadEditTextToFileButton)
        val loadDisplayTextView = findViewById<TextView>(R.id.loadDisplayTextView)
        val savePreferenceButton = findViewById<Button>(R.id.savePreferenceButton)
        val loadPreferenceButton = findViewById<Button>(R.id.loadPreferenceButton)

        saveFileButton.setOnClickListener { v ->
            val editTextStr = editText.text.toString()
            save(editTextStr)
        }

        loadFileButton.setOnClickListener { v ->
            val loadTextStr = load()
            loadDisplayTextView.text = loadTextStr
        }

        savePreferenceButton.setOnClickListener { v ->
            getSharedPreferences("SharedPreferencesTest", MODE_PRIVATE).edit {
                putString("name", "Tom")
                putInt("age", 28)
                putBoolean("married", false)
            }
        }

        loadPreferenceButton.setOnClickListener { v ->
            val prefs = getSharedPreferences("SharedPreferencesTest", Context.MODE_PRIVATE)
            val name = prefs.getString("name", "")
            val age = prefs.getInt("age", 0)
            val married = prefs.getBoolean("married", false)
            Log.d("DataPersistenceActivity", "name is $name")
            Log.d("DataPersistenceActivity", "age is $age")
            Log.d("DataPersistenceActivity", "married is $married")
        }
    }

    // 通过写入文件持久化数据, 不适合复杂结构
    private fun save(inputText: String) {
        try {
            val output = openFileOutput("data", Context.MODE_PRIVATE)
            val writer = BufferedWriter(OutputStreamWriter(output))
            writer.use {
                it.write(inputText)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun load(): String {
        val content = StringBuilder()
        try {
            val input = openFileInput("data")
            val reader = BufferedReader(InputStreamReader(input))
            reader.use {
                reader.forEachLine {
                    content.append(it)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return content.toString()
    }
}