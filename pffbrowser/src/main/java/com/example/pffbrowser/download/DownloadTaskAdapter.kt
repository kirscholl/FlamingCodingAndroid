package com.example.pffbrowser.download

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pffbrowser.R
import com.example.pffbrowser.databinding.PbItemDownloadTaskBinding
import com.example.pffbrowser.download.db.DownloadTaskEntity
import com.example.pffbrowser.download.db.DownloadTaskStatus

/**
 * 下载任务列表 Adapter
 */
class DownloadTaskAdapter(
    private val onActionClick: (DownloadTaskEntity) -> Unit,
    private val onItemClick: (DownloadTaskEntity) -> Unit,
    private val onItemLongClick: (DownloadTaskEntity) -> Unit
) : ListAdapter<DownloadTaskEntity, DownloadTaskAdapter.ViewHolder>(DiffCallback()) {

    var viewModel: DownloadListViewModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PbItemDownloadTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    /**
     * 根据速度映射更新 UI（新方式：响应式更新）
     */
    fun updateSpeeds(speedMap: Map<String, String>) {
        recyclerView?.let { rv ->
            val layoutManager =
                rv.layoutManager as? androidx.recyclerview.widget.LinearLayoutManager
                    ?: return
            val firstVisible = layoutManager.findFirstVisibleItemPosition()
            val lastVisible = layoutManager.findLastVisibleItemPosition()

            for (i in firstVisible..lastVisible) {
                if (i < 0 || i >= currentList.size) continue
                val task = currentList[i]
                // 只更新有速度变化的运行中任务
                if (task.status == DownloadTaskStatus.RUNNING && speedMap.containsKey(task.taskId)) {
                    val holder = rv.findViewHolderForAdapterPosition(i) as? ViewHolder
                    holder?.updateSpeed(speedMap[task.taskId] ?: "0 B/s")
                }
            }
        }
    }

    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    inner class ViewHolder(
        private val binding: PbItemDownloadTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // 操作按钮点击
            binding.btnAction.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onActionClick(getItem(position))
                }
            }

            // 整个 Item 点击（打开已完成文件）
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }

            // 长按菜单
            binding.root.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemLongClick(getItem(position))
                    true
                } else {
                    false
                }
            }
        }

        fun bind(task: DownloadTaskEntity) {
            val displayInfo = viewModel?.getTaskDisplayInfo(task) ?: return

            // 文件名
            binding.tvFileName.text = displayInfo.fileName

            // 文件图标
            binding.ivFileIcon.setImageResource(displayInfo.fileIcon)

            // 文件大小信息
            binding.tvFileInfo.text = "${displayInfo.downloadedSize} / ${displayInfo.totalSize}"

            // 状态文字 - 对于运行中的任务，实时获取速度
            val statusText = if (task.status == DownloadTaskStatus.RUNNING) {
                DownloadSpeedManager.getSpeed(task.taskId)
            } else {
                displayInfo.statusText
            }
            binding.tvStatus.text = statusText
            binding.tvStatus.setTextColor(
                when (task.status) {
                    DownloadTaskStatus.ERROR -> 0xFFE53935.toInt() // 红色
                    DownloadTaskStatus.COMPLETED -> 0xFF43A047.toInt() // 绿色
                    else -> 0xFF999999.toInt() // 灰色
                }
            )

            // 进度条
            if (displayInfo.showProgress) {
                binding.progressBar.visibility = android.view.View.VISIBLE
                binding.progressBar.progress = displayInfo.progressPercent
            } else {
                binding.progressBar.visibility = android.view.View.GONE
            }

            // 操作按钮
            when (displayInfo.actionButtonType) {
                DownloadListViewModel.ActionButtonType.PAUSE -> {
                    binding.btnAction.visibility = android.view.View.VISIBLE
                    binding.btnAction.setImageResource(R.drawable.pb_ic_pause)
                    binding.btnAction.contentDescription = "暂停"
                }

                DownloadListViewModel.ActionButtonType.RESUME -> {
                    binding.btnAction.visibility = android.view.View.VISIBLE
                    binding.btnAction.setImageResource(R.drawable.pb_ic_play)
                    binding.btnAction.contentDescription = "继续"
                }

                DownloadListViewModel.ActionButtonType.RETRY -> {
                    binding.btnAction.visibility = android.view.View.VISIBLE
                    binding.btnAction.setImageResource(R.drawable.pb_ic_refresh)
                    binding.btnAction.contentDescription = "重试"
                }

                DownloadListViewModel.ActionButtonType.NONE -> {
                    binding.btnAction.visibility = android.view.View.GONE
                }
            }
        }

        /**
         * 局部刷新（只更新速度）
         */
        fun updateProgress(task: DownloadTaskEntity) {
            if (task.status == DownloadTaskStatus.RUNNING) {
                val speed = DownloadSpeedManager.getSpeed(task.taskId)
                binding.tvStatus.text = speed
            }
        }

        /**
         * 刷新速度显示（供外部调用）
         */
        fun refreshSpeed(task: DownloadTaskEntity) {
            if (task.status == DownloadTaskStatus.RUNNING) {
                val speed = DownloadSpeedManager.getSpeed(task.taskId)
                android.util.Log.d(
                    "DownloadAdapter",
                    "refreshSpeed: taskId=${task.taskId}, speed=$speed"
                )
                binding.tvStatus.text = speed
            }
        }

        /**
         * 直接更新速度文本
         */
        fun updateSpeed(speed: String) {
            binding.tvStatus.text = speed
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DownloadTaskEntity>() {
        override fun areItemsTheSame(
            oldItem: DownloadTaskEntity,
            newItem: DownloadTaskEntity
        ): Boolean {
            return oldItem.taskId == newItem.taskId
        }

        override fun areContentsTheSame(
            oldItem: DownloadTaskEntity,
            newItem: DownloadTaskEntity
        ): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val PAYLOAD_PROGRESS = "progress"
    }
}
