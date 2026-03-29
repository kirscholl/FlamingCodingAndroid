package com.example.pffbrowser.download.repository

import android.content.Context
import com.example.pffbrowser.download.DownloadDialogInfo
import com.example.pffbrowser.download.database.DownloadTask
import com.example.pffbrowser.download.database.DownloadTaskBuilder
import com.example.pffbrowser.download.database.DownloadTaskDao
import com.example.pffbrowser.download.manager.DownloadManager
import com.example.pffbrowser.download.manager.FilePathManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 下载Repository
 * 提供统一的数据访问接口，协调DownloadManager和数据库
 */
@Singleton
class DownloadRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadTaskDao: DownloadTaskDao,
    private val downloadManager: DownloadManager
) {

    /**
     * 获取所有下载任务（Flow自动更新）
     */
    fun getAllTasks(): Flow<List<DownloadTask>> {
        return downloadTaskDao.getAllTasksFlow()
    }

    /**
     * 根据ID获取任务
     */
    suspend fun getTaskById(taskId: Long): DownloadTask? {
        return downloadTaskDao.getTaskById(taskId)
    }

    /**
     * 创建下载任务（从下载弹窗）
     *
     * @param downloadInfo 下载弹窗信息
     * @param customFileName 用户自定义的文件名
     * @return 任务ID
     */
    suspend fun createDownloadTask(
        downloadInfo: DownloadDialogInfo,
        customFileName: String
    ): Long {
        // 生成唯一文件路径
        val filePath = FilePathManager.generateUniqueFilePath(context, customFileName)

        // 创建任务
        val task = DownloadTaskBuilder.createPendingTask(
            url = downloadInfo.url,
            fileName = customFileName,
            filePath = filePath,
            totalBytes = downloadInfo.contentLength,
            mimeType = downloadInfo.mimeType
        )

        // 插入数据库
        val taskId = downloadTaskDao.insert(task)

        // 自动开始下载
        downloadManager.startDownload(taskId)

        return taskId
    }

    /**
     * 开始下载
     */
    suspend fun startDownload(taskId: Long) {
        downloadManager.startDownload(taskId)
    }

    /**
     * 暂停下载
     */
    suspend fun pauseDownload(taskId: Long) {
        downloadManager.pauseDownload(taskId)
    }

    /**
     * 恢复下载
     */
    suspend fun resumeDownload(taskId: Long) {
        downloadManager.resumeDownload(taskId)
    }

    /**
     * 删除任务
     *
     * @param taskId 任务ID
     * @param deleteFile 是否删除文件
     */
    suspend fun deleteTask(taskId: Long, deleteFile: Boolean = true) {
        downloadManager.deleteTask(taskId, deleteFile)
    }

    /**
     * 获取正在下载的任务
     */
    suspend fun getDownloadingTasks(): List<DownloadTask> {
        return downloadTaskDao.getDownloadingTasks()
    }

    /**
     * 获取已完成的任务
     */
    suspend fun getCompletedTasks(): List<DownloadTask> {
        return downloadTaskDao.getCompletedTasks()
    }

    /**
     * 获取失败的任务
     */
    suspend fun getFailedTasks(): List<DownloadTask> {
        return downloadTaskDao.getFailedTasks()
    }

    /**
     * 删除所有已完成的任务
     */
    suspend fun deleteAllCompleted() {
        val completedTasks = downloadTaskDao.getCompletedTasks()
        completedTasks.forEach { task ->
            downloadManager.deleteTask(task.id, deleteFile = true)
        }
    }

    /**
     * 获取任务总数
     */
    suspend fun getTaskCount(): Int {
        return downloadTaskDao.getTaskCount()
    }

    /**
     * 获取已完成任务数量
     */
    suspend fun getCompletedCount(): Int {
        return downloadTaskDao.getCompletedCount()
    }
}
