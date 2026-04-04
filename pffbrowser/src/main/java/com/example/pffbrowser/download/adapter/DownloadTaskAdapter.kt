package com.example.pffbrowser.download.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pffbrowser.R
import com.example.pffbrowser.download.DownloadStatus
import com.example.pffbrowser.download.database.DownloadTask
import com.example.pffbrowser.utils.FileUtil


/**
 * 下载任务列表适配器
 * 使用ListAdapter和DiffUtil优化性能
 */
class DownloadTaskAdapter(
    private val onItemClick: (DownloadTask) -> Unit,
    private val onItemLongClick: (DownloadTask) -> Boolean,
    private val onActionClick: (DownloadTask) -> Unit
) : ListAdapter<DownloadTask, DownloadTaskAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pb_item_download_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        }
        
        // 使用Payload进行局部更新
        val task = getItem(position)
        var needFullBind = false
        
        for (payload in payloads) {
            when (payload) {
                is List<*> -> {
                    // 处理多个 payload 的情况
                    payload.filterIsInstance<String>().forEach { p ->
                        when (p) {
                            PAYLOAD_PROGRESS -> holder.updateProgress(task)
                            PAYLOAD_STATUS -> holder.updateStatus(task)
                        }
                    }
                }
                PAYLOAD_PROGRESS -> holder.updateProgress(task)
                PAYLOAD_STATUS -> holder.updateStatus(task)
                else -> needFullBind = true
            }
        }
        
        // 如果有未知 payload，执行完整绑定
        if (needFullBind) {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivFileIcon: ImageView = itemView.findViewById(R.id.ivFileIcon)
        private val tvFileName: TextView = itemView.findViewById(R.id.tvFileName)
        private val tvFileInfo: TextView = itemView.findViewById(R.id.tvFileInfo)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private val tvProgress: TextView = itemView.findViewById(R.id.tvProgress)
        private val btnAction: ImageButton = itemView.findViewById(R.id.btnAction)

        fun bind(task: DownloadTask) {
            // 设置文件图标
            ivFileIcon.setImageResource(FileUtil.getFileIconByExtension(task.fileName))

            // 设置文件名
            tvFileName.text = task.fileName

            // 更新状态和进度
            updateStatus(task)
            updateProgress(task)

            // 设置点击事件
            itemView.setOnClickListener { onItemClick(task) }
            itemView.setOnLongClickListener { onItemLongClick(task) }
            btnAction.setOnClickListener { onActionClick(task) }
        }

        fun updateProgress(task: DownloadTask) {
            progressBar.progress = task.progress

            when (task.status) {
                DownloadStatus.DOWNLOADING -> {
                    val speedText = formatSpeed(task.speed)
                    tvProgress.text = "${task.progress}% · $speedText"
                    tvProgress.visibility = View.VISIBLE
                    progressBar.visibility = View.VISIBLE
                }

                DownloadStatus.PENDING, DownloadStatus.PAUSED -> {
                    tvProgress.text = "${task.progress}%"
                    tvProgress.visibility = View.VISIBLE
                    progressBar.visibility = View.VISIBLE
                }

                else -> {
                    tvProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                }
            }
        }

        fun updateStatus(task: DownloadTask) {
            val statusText = when (task.status) {
                DownloadStatus.PENDING -> "等待中"
                DownloadStatus.DOWNLOADING -> "下载中"
                DownloadStatus.PAUSED -> "已暂停"
                DownloadStatus.COMPLETED -> "已完成"
                DownloadStatus.FAILED -> "下载失败"
                DownloadStatus.CANCELED -> "已取消"
            }

            val fileSizeText = FileUtil.formatFileSize(task.totalBytes)
            tvFileInfo.text = "$fileSizeText · $statusText"

            // 设置操作按钮图标
            val iconRes = when (task.status) {
                DownloadStatus.DOWNLOADING -> android.R.drawable.ic_media_pause
                DownloadStatus.PAUSED, DownloadStatus.FAILED -> android.R.drawable.ic_media_play
                DownloadStatus.COMPLETED -> android.R.drawable.ic_menu_view
                else -> android.R.drawable.ic_menu_close_clear_cancel
            }
            btnAction.setImageResource(iconRes)

            // 已完成或已取消时隐藏操作按钮
            btnAction.visibility = if (task.status == DownloadStatus.CANCELED) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }


        private fun formatSpeed(bytesPerSecond: Long): String {
            return when {
                bytesPerSecond < 1024 -> "${bytesPerSecond} B/s"
                bytesPerSecond < 1024 * 1024 -> {
                    String.format("%.1f KB/s", bytesPerSecond / 1024.0)
                }

                else -> {
                    String.format("%.1f MB/s", bytesPerSecond / (1024.0 * 1024.0))
                }
            }
        }
    }

    /**
     * DiffUtil回调，用于高效计算列表差异
     */
    private class DiffCallback : DiffUtil.ItemCallback<DownloadTask>() {
        override fun areItemsTheSame(oldItem: DownloadTask, newItem: DownloadTask): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DownloadTask, newItem: DownloadTask): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: DownloadTask, newItem: DownloadTask): Any? {
            // 构建变更标记集合
            val payloads = mutableListOf<String>()
            
            // 检查进度或速度是否变化
            if (oldItem.progress != newItem.progress || oldItem.speed != newItem.speed) {
                payloads.add(PAYLOAD_PROGRESS)
            }
            
            // 检查状态是否变化
            if (oldItem.status != newItem.status) {
                payloads.add(PAYLOAD_STATUS)
            }
            
            // 如果只有进度/速度/状态变化（或它们的组合），返回对应的 payload
            // 检查其他可能影响 UI 的字段是否变化
            val otherFieldsChanged = oldItem.fileName != newItem.fileName ||
                    oldItem.filePath != newItem.filePath ||
                    oldItem.totalBytes != newItem.totalBytes ||
                    oldItem.errorMsg != newItem.errorMsg ||
                    oldItem.completeTime != newItem.completeTime
            
            return if (payloads.isNotEmpty() && !otherFieldsChanged) {
                payloads
            } else {
                null
            }
        }
    }

    companion object {
        private const val PAYLOAD_PROGRESS = "progress"
        private const val PAYLOAD_STATUS = "status"
    }
}

