package com.example.pffbrowser.request.base

import okhttp3.Interceptor
import okhttp3.Response

class IPInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        return chain.proceed(originalRequest)
    }

}