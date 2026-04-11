package com.example.pffbrowser.mine.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "browser_history",
    indices = [Index(value = ["url"]), Index(value = ["visitTime"])]
)
data class BrowserHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val title: String,
    val visitTime: Long,
    val faviconUrl: String? = null
)
