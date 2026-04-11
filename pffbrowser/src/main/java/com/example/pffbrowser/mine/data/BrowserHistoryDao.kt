package com.example.pffbrowser.mine.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BrowserHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: BrowserHistory): Long

    @Query("SELECT * FROM browser_history ORDER BY visitTime DESC")
    fun getAllFlow(): Flow<List<BrowserHistory>>

    @Query("""
        SELECT bh.* FROM browser_history bh
        WHERE bh.id IN (
            SELECT rowid FROM browser_history_fts WHERE browser_history_fts MATCH :query || '*'
        )
        ORDER BY bh.visitTime DESC
        LIMIT 200
    """)
    fun searchByTitle(query: String): Flow<List<BrowserHistory>>

    @Query("DELETE FROM browser_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM browser_history")
    suspend fun deleteAll()
}
