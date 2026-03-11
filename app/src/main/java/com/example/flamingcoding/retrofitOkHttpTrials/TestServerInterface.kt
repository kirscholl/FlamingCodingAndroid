package com.example.flamingcoding.retrofitOkHttpTrials

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface TestServerInterface {

    companion object {
        fun getRetrofitService(hostName: String, user: String): TestServerInterface {
            val retro = Retrofit.Builder()
                .baseUrl(hostName)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retro.create(TestServerInterface::class.java)
        }

        fun getRetroRequestCallbackCall(baseUrl: String, user: String): Call<List<Repo>> {
            val retro = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create()).build()
            val service = retro.create(TestServerInterface::class.java)
            return service.listRepos(user)
        }

        fun retroRequestWithCallback(
            baseUrl: String,
            user: String,
            onResponse: (response: Response<List<Repo>?>) -> Unit,
            onFailure: (throwable: Throwable) -> Unit
        ) {
            val retro = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create()).build()
            val service = retro.create(TestServerInterface::class.java)
            val call = service.listRepos(user)
            call.enqueue(object : Callback<List<Repo>> {
                override fun onResponse(
                    call: Call<List<Repo>?>,
                    response: Response<List<Repo>?>
                ) {
                    onResponse(response)
                }

                override fun onFailure(
                    call: Call<List<Repo>?>,
                    throwable: Throwable
                ) {
                    onFailure(throwable)
                }
            })
        }

        suspend fun retroRequest(hostName: String, user: String): List<Repo> {
            val retro = Retrofit.Builder()
                .baseUrl(hostName)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val retroService = retro.create(TestServerInterface::class.java)
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