package com.example.flamingcoding.androidTrials

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TestItemRecycleViewAdapter :
    RecyclerView.Adapter<TestItemRecycleViewAdapter.TestItemRecycleViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TestItemRecycleViewHolder {
        // 填充itemView
        // 绑定itemView中的点击事件
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(
        holder: TestItemRecycleViewHolder,
        position: Int
    ) {
        // 初始化itemView中的具体内容
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class TestItemRecycleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 用于存储ItemView中的变量以至于不用反复调用findViewById
    }

}