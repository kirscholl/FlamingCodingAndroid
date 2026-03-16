package com.example.pffbrowser.request.search

import com.example.pffbrowser.request.base.WanAndroidBaseResponse
import retrofit2.http.GET

interface HotSearchWordService {
    /**
     * 热词
     */
    @GET("hotkey/json")
    suspend fun getHotSearchWord(): WanAndroidBaseResponse<MutableList<HotSearchWordData>>
}