package com.example.pffbrowser.download

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.Formatter
import androidx.lifecycle.viewModelScope
import com.example.pffbrowser.base.BaseViewModel
import com.example.pffbrowser.download.db.DownloadTaskEntity
import com.example.pffbrowser.download.db.DownloadTaskStatus
import com.example.pffbrowser.download.db.getProgressPercent
import com.example.pffbrowser.download.db.isCompleted
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * 下载列表页面 ViewModel
 */
@HiltViewModel
class DownloadListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadManager: OkDownloadManager
) : BaseViewModel() {

    /**
     * 下载任务列表
     */
    val downloadTasks: Flow<List<DownloadTaskEntity>> = downloadManager.getAllTasks()

    /**
     * 下载速度流
     * 用于实时更新 UI 速度显示
     */
    val downloadSpeedFlow = downloadManager.downloadSpeedFlow

    /**
     * 操作结果事件
     */
    private val _actionResult = MutableStateFlow<ActionResult?>(null)
    val actionResult: StateFlow<ActionResult?> = _actionResult.asStateFlow()

    /**
     * 获取任务的显示信息
     */
    fun getTaskDisplayInfo(task: DownloadTaskEntity): TaskDisplayInfo {
        return TaskDisplayInfo(
            taskId = task.taskId,
            fileName = task.fileName,
            fileIcon = getFileIconRes(task.mimeType),
            progressPercent = task.getProgressPercent(),
            downloadedSize = Formatter.formatFileSize(context, task.downloadedBytes),
            totalSize = if (task.totalBytes > 0) {
                Formatter.formatFileSize(context, task.totalBytes)
            } else "未知大小",
            statusText = getStatusText(task),
            status = task.status,
            showProgress = task.status == DownloadTaskStatus.RUNNING ||
                    task.status == DownloadTaskStatus.PAUSED,
            actionButtonType = getActionButtonType(task)
        )
    }

    /**
     * 处理任务操作按钮点击
     */
    fun onActionButtonClick(task: DownloadTaskEntity) {
        viewModelScope.launch {
            when (task.status) {
                DownloadTaskStatus.RUNNING -> {
                    // 暂停
                    downloadManager.pauseTask(task.taskId)
                }

                DownloadTaskStatus.PAUSED -> {
                    // 继续
                    downloadManager.resumeTask(task.taskId)
                }

                DownloadTaskStatus.ERROR -> {
                    // 重试
                    downloadManager.retryTask(task.taskId)
                }

                else -> {
                    // 其他状态不处理
                }
            }
        }
    }

    /**
     * 打开已下载的文件
     */
    fun openFile(task: DownloadTaskEntity) {
        if (!task.isCompleted()) {
            return
        }

        val file = File(task.filePath)
        if (!file.exists()) {
            _actionResult.value = ActionResult.Error("文件不存在")
            return
        }

        try {
            val uri = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, task.mimeType ?: "*/*")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // 检查是否有应用可以处理此文件
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                _actionResult.value = ActionResult.Error("没有应用可以打开此文件")
            }
        } catch (e: Exception) {
            _actionResult.value = ActionResult.Error("无法打开文件: ${e.message}")
        }
    }

    /**
     * 删除下载任务
     */
    fun deleteTask(task: DownloadTaskEntity) {
        viewModelScope.launch {
            downloadManager.deleteTask(task.taskId)
            // 清理速度计算器
            DownloadSpeedManager.removeCalculator(task.taskId)
            _actionResult.value = ActionResult.Success("已删除")
        }
    }

    /**
     * 消费操作结果事件
     */
    fun consumeActionResult() {
        _actionResult.value = null
    }

    /**
     * 获取状态文字
     */
    private fun getStatusText(task: DownloadTaskEntity): String {
        return when (task.status) {
            DownloadTaskStatus.PENDING -> "等待中..."
            DownloadTaskStatus.RUNNING -> {
                // 获取实时速度
                val speed = DownloadSpeedManager.getSpeed(task.taskId)
                "$speed"
            }

            DownloadTaskStatus.PAUSED -> "已暂停"
            DownloadTaskStatus.COMPLETED -> "已完成"
            DownloadTaskStatus.ERROR -> "下载失败"
        }
    }

    /**
     * 获取操作按钮类型
     */
    private fun getActionButtonType(task: DownloadTaskEntity): ActionButtonType {
        return when (task.status) {
            DownloadTaskStatus.RUNNING -> ActionButtonType.PAUSE
            DownloadTaskStatus.PAUSED -> ActionButtonType.RESUME
            DownloadTaskStatus.ERROR -> ActionButtonType.RETRY
            else -> ActionButtonType.NONE
        }
    }

    /**
     * 获取文件图标资源
     */
    private fun getFileIconRes(mimeType: String?): Int {
        return when {
            mimeType == null -> com.example.pffbrowser.R.drawable.pb_default_file_img
            mimeType.startsWith("image/") -> com.example.pffbrowser.R.drawable.pb_default_file_img
            mimeType.startsWith("video/") -> com.example.pffbrowser.R.drawable.pb_default_file_img
            mimeType.startsWith("audio/") -> com.example.pffbrowser.R.drawable.pb_default_file_img
            mimeType.contains("pdf") -> com.example.pffbrowser.R.drawable.pb_default_file_img
            mimeType.contains("word") || mimeType.contains("document") -> com.example.pffbrowser.R.drawable.pb_default_file_img
            mimeType.contains("excel") || mimeType.contains("sheet") -> com.example.pffbrowser.R.drawable.pb_default_file_img
            mimeType.contains("powerpoint") || mimeType.contains("presentation") -> com.example.pffbrowser.R.drawable.pb_default_file_img
            mimeType.contains("zip") || mimeType.contains("compressed") -> com.example.pffbrowser.R.drawable.pb_default_file_img
            mimeType.contains("apk") -> com.example.pffbrowser.R.drawable.pb_default_file_img
            else -> com.example.pffbrowser.R.drawable.pb_default_file_img
        }
    }

    /**
     * 任务显示信息数据类
     */
    data class TaskDisplayInfo(
        val taskId: String,
        val fileName: String,
        val fileIcon: Int,
        val progressPercent: Int,
        val downloadedSize: String,
        val totalSize: String,
        val statusText: String,
        val status: DownloadTaskStatus,
        val showProgress: Boolean,
        val actionButtonType: ActionButtonType
    )

    /**
     * 操作按钮类型
     */
    enum class ActionButtonType {
        NONE,   // 不显示
        PAUSE,  // 暂停
        RESUME, // 继续
        RETRY   // 重试
    }

    /**
     * 操作结果
     */
    sealed class ActionResult {
        data class Success(val message: String) : ActionResult()
        data class Error(val message: String) : ActionResult()
    }
}
