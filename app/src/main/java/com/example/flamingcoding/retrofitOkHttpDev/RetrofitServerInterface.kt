package com.example.flamingcoding.retrofitOkHttpDev

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitServerInterface {

    @GET("users/{user}/repos")
    fun listRepos(@Path("user") user: String?): Call<List<Repo>>

    @GET("/repos/{owner}/{repo}/contributors")
    fun contributorsFuture(@Path("owner") owner: String, @Path("repo") repo: String)

    @GET("users/{user}/repos")
    suspend fun listReposCor(@Path("user") user: String?): List<Repo>
}