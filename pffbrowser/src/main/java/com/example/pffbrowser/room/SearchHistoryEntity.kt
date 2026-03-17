package com.example.pffbrowser.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchHistoryEntity(
    var url: String,
    var favicon: String,
    var urlName: String,
    var searchTimeStamp: Int,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}