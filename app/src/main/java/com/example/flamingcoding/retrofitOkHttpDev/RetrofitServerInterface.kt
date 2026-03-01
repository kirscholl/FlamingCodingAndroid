package com.example.flamingcoding.retrofitOkHttpDev

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitServerInterface {

    companion object {
        fun retroRequestWithCallback(baseUrl: String, user: String): Call<List<Repo>> {
            val retro = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create()).build()
            val service = retro.create(RetrofitServerInterface::class.java)
            val repo = service.listRepos(user)
            return repo
        }

        suspend fun retroRequest(hostName: String, user: String): List<Repo> {
            val retro = Retrofit.Builder()
                .baseUrl(hostName)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val retroService = retro.create(RetrofitServerInterface::class.java)
            // 调用了其他挂起函数
            return retroService.listReposCor(user)
        }
    }

    @GET("users/{user}/repos")
    fun listRepos(@Path("user") user: String?): Call<List<Repo>>

    @GET("/repos/{owner}/{repo}/contributors")
    fun contributorsFuture(@Path("owner") owner: String, @Path("repo") repo: String)

    @GET("users/{user}/repos")
    suspend fun listReposCor(@Path("user") user: String?): List<Repo>

}