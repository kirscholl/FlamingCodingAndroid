package com.example.pffbrowser.request.search

import com.example.pffbrowser.request.base.WABaseResp
import retrofit2.http.GET

interface IHotSearchService {
    /**
     * 搜索热词
     */
    @GET("hotkey/json")
    suspend fun getHotSearchWord(): WABaseResp<MutableList<HotSearchData>>
}