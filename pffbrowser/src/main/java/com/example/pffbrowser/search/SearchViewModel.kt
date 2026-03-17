package com.example.pffbrowser.search

import android.animation.ObjectAnimator
import android.content.SharedPreferences
import android.view.View
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.pffbrowser.base.BaseNetWorkViewModel
import com.example.pffbrowser.request.base.WABaseResp
import com.example.pffbrowser.request.search.HotSearchData
import com.example.pffbrowser.request.search.HotSearchRequester
import com.example.pffbrowser.utils.SharedPreferencesUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val requester: HotSearchRequester) :
    BaseNetWorkViewModel() {

    lateinit var hotSearchLiveData: LiveData<WABaseResp<MutableList<HotSearchData>>>

    @Inject
    lateinit var prefs: SharedPreferences
    var hotSearchDataList: MutableList<HotSearchData> = mutableListOf()
    var hotSearchAnimMap = mutableMapOf<View, ObjectAnimator>()
    val tagViews = mutableListOf<View>()
    var isEditMode = false

    fun setHotSearchLiveData() {
        hotSearchLiveData = requester.requestHotSearch().asLiveData()
    }

    override fun requestInitData() {
        super.requestInitData()
        setHotSearchLiveData()
    }

    override fun requestRefreshData() {
        super.requestRefreshData()
    }

    fun getSearchHistory(): String? {
        return prefs.getString(SharedPreferencesUtil.SEARCH_HISTORY, "")
    }

    fun saveSearchHistory(keyword: String) {
        // 读取现有历史
        val currentHistory = prefs.getString("history", "") ?: ""
        val historySet = currentHistory.split(",")
            .filter { it.isNotEmpty() }.toMutableSet()
        // 去重：如果已存在，先移除旧的
        if (historySet.contains(keyword)) {
            historySet.remove(keyword)
        }
        // 将新关键词加到最前面
        val newHistory = listOf(keyword) + historySet.toList()
        // 限制条目数量，例如最多保存10条
        val limitedHistory = newHistory.take(10)
        // 保存回 SharedPreferences
        prefs.edit { putString("history", limitedHistory.joinToString(",")) }
    }
}