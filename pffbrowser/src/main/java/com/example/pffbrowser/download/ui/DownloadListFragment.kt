package com.example.pffbrowser.download.ui

import android.app.AlertDialog
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pffbrowser.base.BaseFragment
import com.example.pffbrowser.databinding.PbFragmentDownloadListBinding
import com.example.pffbrowser.download.DownloadStatus
import com.example.pffbrowser.download.adapter.DownloadTaskAdapter
import com.example.pffbrowser.download.database.DownloadTask
import com.example.pffbrowser.download.utils.FileOpenHelper
import com.example.pffbrowser.download.viewmodel.DownloadViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

/**
 * 下载列表Fragment
 * 显示所有下载任务，支持暂停/恢复/删除操作
 */
@AndroidEntryPoint
class DownloadListFragment : BaseFragment<PbFragmentDownloadListBinding, DownloadViewModel>() {

    companion object {
        const val TAG = "DownloadListFragment"
    }

    private lateinit var adapter: DownloadTaskAdapter

    override fun PbFragmentDownloadListBinding.initView() {
        // 初始化RecyclerView
        adapter = DownloadTaskAdapter(
            onItemClick = ::onItemClick,
            onItemLongClick = ::onItemLongClick,
            onActionClick = ::onActionClick
        )

        rvDownloadList.layoutManager = LinearLayoutManager(requireContext())
        rvDownloadList.adapter = adapter
    }

    override fun initViewObserver() {
        // 观察下载任务列表
        mViewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)

            // 显示/隐藏空状态
            if (tasks.isEmpty()) {
                mViewBinding.llEmptyState.visibility = View.VISIBLE
                mViewBinding.rvDownloadList.visibility = View.GONE
            } else {
                mViewBinding.llEmptyState.visibility = View.GONE
                mViewBinding.rvDownloadList.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 点击任务项
     * 如果已完成，打开文件；否则不做处理
     */
    private fun onItemClick(task: DownloadTask) {
        if (task.status == DownloadStatus.COMPLETED) {
            val file = File(task.filePath)
            if (file.exists()) {
                FileOpenHelper.openFile(requireContext(), file)
            } else {
                showToast("文件不存在")
            }
        }
    }

    /**
     * 长按任务项
     * 弹出删除确认对话框
     */
    private fun onItemLongClick(task: DownloadTask): Boolean {
        showDeleteDialog(task)
        return true
    }

    /**
     * 点击操作按钮
     * 根据任务状态执行相应操作：暂停/恢复/打开文件
     */
    private fun onActionClick(task: DownloadTask) {
        when (task.status) {
            DownloadStatus.DOWNLOADING -> {
                // 暂停下载
                mViewModel.pauseDownload(task.id)
            }

            DownloadStatus.PAUSED, DownloadStatus.FAILED -> {
                // 恢复下载
                mViewModel.resumeDownload(task.id)
            }

            DownloadStatus.COMPLETED -> {
                // 打开文件
                onItemClick(task)
            }

            DownloadStatus.PENDING -> {
                // 等待中，暂停
                mViewModel.pauseDownload(task.id)
            }

            else -> {
                // 其他状态不处理
            }
        }
    }

    /**
     * 显示删除确认对话框
     */
    private fun showDeleteDialog(task: DownloadTask) {
        val items = arrayOf("删除任务（保留文件）", "删除任务和文件")

        AlertDialog.Builder(requireContext())
            .setTitle("删除下载任务")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> {
                        // 删除任务，保留文件
                        mViewModel.deleteTask(task.id, deleteFile = false)
                        showToast("已删除任务")
                    }

                    1 -> {
                        // 删除任务和文件
                        mViewModel.deleteTask(task.id, deleteFile = true)
                        showToast("已删除任务和文件")
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT)
            .show()
    }
}

