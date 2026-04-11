package com.example.pffbrowser.mine.adapter

import com.example.pffbrowser.mine.data.BrowserHistory

sealed class HistoryListItem {
    data class Header(val dateLabel: String) : HistoryListItem()
    data class Entry(val history: BrowserHistory) : HistoryListItem()
}
