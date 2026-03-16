package com.example.pffbrowser.request.search

import com.example.pffbrowser.request.base.BaseRequester
import com.example.pffbrowser.request.base.WABaseResp
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class HotSearchRequester @Inject constructor() {

    @Inject
    lateinit var hotSearchService: IHotSearchService

    fun requestHotSearch(): Flow<WABaseResp<MutableList<HotSearchData>>> {
        return BaseRequester.request {
            emit(hotSearchService.getHotSearchWord())
        }
    }
}