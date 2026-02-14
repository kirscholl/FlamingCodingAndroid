package com.example.flamingcoding.androidTrials;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


class TestRecycleViewAdapterJavaViewHolder extends RecyclerView.ViewHolder {

    public TestRecycleViewAdapterJavaViewHolder(@NonNull View itemView) {
        super(itemView);
        // 用于存储ItemView中的变量以至于不用反复调用findViewById
    }
}

public class TestRecycleViewAdapterJava extends RecyclerView.Adapter<TestRecycleViewAdapterJavaViewHolder> {

    @NonNull
    @Override
    public TestRecycleViewAdapterJavaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TestRecycleViewAdapterJavaViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
