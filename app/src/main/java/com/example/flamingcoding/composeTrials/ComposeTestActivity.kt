package com.example.flamingcoding.composeTrials

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class ComposeTestActivity : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onCreate(savedInstanceState, persistentState)
        setContent {
            MaterialTheme {
                Surface {
                    Greeting("Test")
                }
            }
        }
    }

    @Composable
    fun Greeting(name: String) {
        Text(text = "Compose Test $name")
    }

    // 预览 必须是无参数的函数
    @Preview(showSystemUi = true)
    @Composable
    fun PreViewComposeTest() {
        Greeting("Test")
    }
}