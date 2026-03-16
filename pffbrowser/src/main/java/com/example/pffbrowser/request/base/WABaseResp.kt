package com.example.pffbrowser.request.base

import com.google.gson.annotations.SerializedName

data class WABaseResp<T>(
    // data是kotlin关键字 反引号
    val `data`: T,
    @SerializedName("errorCode") val code: Int,
    @SerializedName("errorMsg") val msg: String
)