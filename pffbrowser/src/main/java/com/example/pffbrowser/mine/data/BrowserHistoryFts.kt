package com.example.pffbrowser.mine.data

import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = BrowserHistory::class)
@Entity(tableName = "browser_history_fts")
data class BrowserHistoryFts(
    val title: String
)
