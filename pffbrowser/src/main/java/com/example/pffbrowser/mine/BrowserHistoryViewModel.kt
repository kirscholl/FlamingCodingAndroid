package com.example.pffbrowser.mine

import androidx.lifecycle.viewModelScope
import com.example.pffbrowser.base.BaseViewModel
import com.example.pffbrowser.mine.adapter.HistoryListItem
import com.example.pffbrowser.mine.data.BrowserHistory
import com.example.pffbrowser.mine.data.BrowserHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BrowserHistoryViewModel @Inject constructor(
    private val repository: BrowserHistoryRepository
) : BaseViewModel() {

    val searchQuery = MutableStateFlow("")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val historyItems: StateFlow<List<HistoryListItem>> = searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getAllHistory()
            else repository.searchHistory(query.trim())
        }
        .map { list -> groupByDate(list) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteHistory(history: BrowserHistory) {
        viewModelScope.launch { repository.deleteHistory(history.id) }
    }

    private fun groupByDate(list: List<BrowserHistory>): List<HistoryListItem> {
        if (list.isEmpty()) return emptyList()
        val result = mutableListOf<HistoryListItem>()
        val today = dayStart(System.currentTimeMillis())
        val yesterday = today - 86_400_000L
        var lastLabel: String? = null
        for (item in list) {
            val label = when {
                item.visitTime >= today -> "今天"
                item.visitTime >= yesterday -> "昨天"
                else -> SimpleDateFormat("yyyy年M月d日", Locale.CHINESE).format(Date(item.visitTime))
            }
            if (label != lastLabel) {
                result.add(HistoryListItem.Header(label))
                lastLabel = label
            }
            result.add(HistoryListItem.Entry(item))
        }
        return result
    }

    private fun dayStart(ts: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = ts
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}
