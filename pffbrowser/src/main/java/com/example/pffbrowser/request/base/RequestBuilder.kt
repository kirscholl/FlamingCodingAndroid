package com.example.pffbrowser.request.base

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RequestBuilder {

    @Provides
    @Singleton
    fun buildOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30L * 1000L, TimeUnit.MILLISECONDS)
            .readTimeout(30L * 1000L, TimeUnit.MILLISECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun buildRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // 创建自定义 Gson 实例 为了兼容解析kotlinx.serialization
//        val gson: Gson = GsonBuilder()
//            .registerTypeAdapter(JsonObject::class.java, JsonObjectAdapter())
//            .create()
        return Retrofit.Builder()
            .baseUrl(RequestConst.WAN_ANDROID_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}