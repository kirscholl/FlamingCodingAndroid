package com.example.flamingcoding.kotlinCoroutinesTrials;

import com.example.flamingcoding.retrofitOkHttpDev.Repo;
import com.example.flamingcoding.retrofitOkHttpDev.TestServerInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KotlinCoroutineNoteJava {
    public void retroRequestWithCallback(String baseUrl, String user) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TestServerInterface service = retrofit.create(TestServerInterface.class);
        Call<List<Repo>> call = service.listRepos(user);

        call.enqueue(new Callback<List<Repo>>() {
            @Override
            public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {

            }

            @Override
            public void onFailure(Call<List<Repo>> call, Throwable t) {
                // 处理失败情况
            }
        });
    }
}
