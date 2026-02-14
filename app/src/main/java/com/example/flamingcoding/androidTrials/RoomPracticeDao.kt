package com.example.flamingcoding.androidTrials

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RoomPracticeDao {

    @Insert
    fun insertUrl(data: RoomPracticeData): Long

    @Update
    fun updateUrl(newUrl: RoomPracticeData)

    @Query("select * from RoomPracticeData")
    fun loadAllUrl(): List<RoomPracticeData>

    @Delete
    fun deleteUrlData(url: RoomPracticeData)

    @Query("delete from RoomPracticeData where linkName = :linkName")
    fun deleteUrlByLinkName(linkName: String)
}