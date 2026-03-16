package com.example.pffbrowser.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.pffbrowser.base.BaseNetWorkViewModel
import com.example.pffbrowser.request.base.WABaseResp
import com.example.pffbrowser.request.search.HotSearchData
import com.example.pffbrowser.request.search.HotSearchRequester
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val requester: HotSearchRequester) :
    BaseNetWorkViewModel() {

    lateinit var hotSearchLiveData: LiveData<WABaseResp<MutableList<HotSearchData>>>

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
}