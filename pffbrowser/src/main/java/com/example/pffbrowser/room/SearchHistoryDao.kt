package com.example.pffbrowser.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SearchHistoryDao {

    @Insert
    fun insertData(entity: SearchHistoryEntity): Long

    @Update
    fun updateUrl(newEntity: SearchHistoryEntity)

    @Query("select * from SearchHistoryEntity")
    fun loadAllUrl(): List<SearchHistoryEntity>

    @Delete
    fun deleteUrlData(url: SearchHistoryEntity)

    @Query("delete from SearchHistoryEntity where url = :url")
    fun deleteUrlByLinkName(url: String)
}