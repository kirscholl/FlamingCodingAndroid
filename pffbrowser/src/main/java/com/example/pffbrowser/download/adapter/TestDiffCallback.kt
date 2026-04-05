package com.example.pffbrowser.download.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.pffbrowser.download.database.DownloadTask

class TestDiffCallback : DiffUtil.ItemCallback<DownloadTask>() {

    // 判断两个对象是否代表同一个“实体”，比较它们的 唯一标识符（ID）
    override fun areItemsTheSame(
        oldItem: DownloadTask,
        newItem: DownloadTask
    ): Boolean {
        TODO("Not yet implemented")
    }

    // 仅当 areItemsTheSame 返回 true 时才会被调用
    // 判断同一个 ID 的项，其内部数据（UI 展示的内容）是否完全一致
    override fun areContentsTheSame(
        oldItem: DownloadTask,
        newItem: DownloadTask
    ): Boolean {
        TODO("Not yet implemented")
    }


    // 计算具体哪个字段变了，并返回一个“增量包”（Payload）。
    override fun getChangePayload(
        oldItem: DownloadTask,
        newItem: DownloadTask
    ): Any? {
        return super.getChangePayload(oldItem, newItem)
    }
}