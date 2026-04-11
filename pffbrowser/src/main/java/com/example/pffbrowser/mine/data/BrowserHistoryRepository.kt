package com.example.pffbrowser.mine.data

import com.example.pffbrowser.mine.data.BrowserHistory
import com.example.pffbrowser.mine.data.BrowserHistoryDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BrowserHistoryRepository @Inject constructor(
    private val dao: BrowserHistoryDao
) {

    fun getAllHistory(): Flow<List<BrowserHistory>> = dao.getAllFlow()

    fun searchHistory(query: String): Flow<List<BrowserHistory>> = dao.searchByTitle(query)

    suspend fun addHistory(url: String, title: String, faviconUrl: String? = null) {
        dao.insert(
            BrowserHistory(
                url = url,
                title = title.ifBlank { url },
                visitTime = System.currentTimeMillis(),
                faviconUrl = faviconUrl
            )
        )
    }

    suspend fun deleteHistory(id: Long) = dao.deleteById(id)

    suspend fun clearAll() = dao.deleteAll()
}
