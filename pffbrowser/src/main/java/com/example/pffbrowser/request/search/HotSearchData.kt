package com.example.pffbrowser.request.search

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class HotSearchData(
    val id: Int,
    val link: String,
    val name: String,
    val visible: Int,
    val order: Int
) : Parcelable