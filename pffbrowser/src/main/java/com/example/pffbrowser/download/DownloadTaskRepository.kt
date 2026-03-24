package com.example.pffbrowser.download

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.pffbrowser.download.db.DownloadTaskDao
import com.example.pffbrowser.download.db.DownloadTaskEntity
import com.example.pffbrowser.download.db.DownloadTaskStatus
import com.example.pffbrowser.download.db.canResume
import com.example.pffbrowser.room.PbBrowserDatabase
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 下载任务仓库
 * 管理下载任务的增删改查、状态流转、队列调度
 */
@Singleton
class DownloadTaskRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val MAX_PARALLEL_DOWNLOADS = 3
        const val DOWNLOAD_DIR = "PbDownloads"
    }

    private val downloadTaskDao: DownloadTaskDao by lazy {
        PbBrowserDatabase.getDatabase(context).downloadTaskDao()
    }

    // 用于执行数据库操作和队列调度的协程作用域
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // 活跃的 OkDownload 任务映射 (taskId -> DownloadTask)
    private val activeDownloadTasks = ConcurrentHashMap<String, DownloadTask>()

    // 下载回调监听器
    var downloadCallback: DownloadTaskCallback? = null

    // ========== 批量进度更新机制 ==========
    // 进度缓冲区：taskId -> downloadedBytes
    private val progressBuffer = ConcurrentHashMap<String, Long>()
    
    // 上次刷新时间
    private var lastFlushTime = System.currentTimeMillis()
    
    // 批量刷新间隔（毫秒）
    private val FLUSH_INTERVAL_MS = 500L
    
    // 最小进度变化才写入（字节）
    private val MIN_BYTES_DIFF = 64 * 1024L  // 64KB
    
    // 速度更新 Flow，供 UI 订阅
    private val _speedFlow = MutableStateFlow<Map<String, String>>(emptyMap())
    val speedFlow = _speedFlow.asStateFlow()
    
    // 上次写入数据库的进度值
    private val lastWrittenProgress = ConcurrentHashMap<String, Long>()

    init {
        // 启动定时刷新任务
        startPeriodicFlush()
    }

    /**
     * 启动定时刷新任务
     */
    private fun startPeriodicFlush() {
        repositoryScope.launch {
            while (true) {
                delay(FLUSH_INTERVAL_MS)
                flushProgressBuffer()
            }
        }
    }

    /**
     * 刷新进度缓冲区到数据库
     */
    private suspend fun flushProgressBuffer() {
        if (progressBuffer.isEmpty()) return
        
        val now = System.currentTimeMillis()
        lastFlushTime = now
        
        // 复制并清空缓冲区
        val batch = progressBuffer.toMap()
        progressBuffer.clear()
        
        // 批量更新数据库
        batch.forEach { (taskId, bytes) ->
            val lastWritten = lastWrittenProgress[taskId] ?: 0
            // 只有进度变化超过阈值才写入
            if (kotlin.math.abs(bytes - lastWritten) >= MIN_BYTES_DIFF) {
                downloadTaskDao.updateProgress(taskId, bytes)
                lastWrittenProgress[taskId] = bytes
            }
        }
    }

    /**
     * 添加进度到缓冲区
     */
    private fun bufferProgress(taskId: String, downloadedBytes: Long) {
        progressBuffer[taskId] = downloadedBytes
        
        // 更新速度并通知 UI
        val newSpeed = DownloadSpeedManager.updateProgress(taskId, downloadedBytes)
        if (newSpeed != null) {
            // 速度实际更新了，通知 UI
            val currentSpeeds = _speedFlow.value.toMutableMap()
            currentSpeeds[taskId] = newSpeed
            _speedFlow.value = currentSpeeds
        }
        
        // 检查是否需要立即刷新（超过500ms）
        val now = System.currentTimeMillis()
        if (now - lastFlushTime > FLUSH_INTERVAL_MS) {
            repositoryScope.launch {
                flushProgressBuffer()
            }
        }
    }

    /**
     * 清理任务的进度缓存
     */
    private fun clearTaskProgress(taskId: String) {
        progressBuffer.remove(taskId)
        lastWrittenProgress.remove(taskId)
        DownloadSpeedManager.removeCalculator(taskId)
        
        // 从速度流中移除
        val currentSpeeds = _speedFlow.value.toMutableMap()
        currentSpeeds.remove(taskId)
        _speedFlow.value = currentSpeeds
    }

    /**
     * 获取所有下载任务（按创建时间倒序）
     */
    fun getAllTasks(): Flow<List<DownloadTaskEntity>> {
        return downloadTaskDao.getAllTasks()
    }

    /**
     * 添加新的下载任务
     * 如果任务已存在（URL和文件名都相同），则返回已存在的任务
     * 如果文件名冲突（同名但不同URL，或文件已存在），则自动重命名
     */
    suspend fun addDownloadTask(
        url: String,
        fileName: String,
        mimeType: String?,
        totalBytes: Long
    ): DownloadTaskEntity {
        val taskId = generateTaskId(url, fileName)

        // 检查是否已存在完全相同的任务（URL和文件名都相同）
        val existingTask = downloadTaskDao.getTaskById(taskId)
        if (existingTask != null) {
            Log.d("DownloadRepository", "addDownloadTask: task already exists, id=$taskId")
            return existingTask
        }

        // 获取公共下载目录
        val downloadDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            DOWNLOAD_DIR
        )
        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }

        // 检查文件名冲突，生成唯一的文件名
        val uniqueFileName = generateUniqueFileName(downloadDir, fileName)
        val finalTaskId = if (uniqueFileName != fileName) {
            // 文件名变化了，重新生成 taskId
            generateTaskId(url, uniqueFileName)
        } else {
            taskId
        }

        // 再次检查（以防生成的文件名也冲突）
        val finalExistingTask = downloadTaskDao.getTaskById(finalTaskId)
        if (finalExistingTask != null) {
            return finalExistingTask
        }

        val filePath = File(downloadDir, uniqueFileName).absolutePath

        // 清理可能存在的旧文件和断点信息（防止之前下载失败的残留）
        cleanupDownloadFiles(filePath)

        val now = System.currentTimeMillis()

        val task = DownloadTaskEntity(
            taskId = finalTaskId,
            url = url,
            fileName = uniqueFileName,
            mimeType = mimeType,
            filePath = filePath,
            totalBytes = totalBytes,
            downloadedBytes = 0,
            status = DownloadTaskStatus.PENDING,
            createTime = now,
            completeTime = null,
            lastUpdateTime = now
        )

        downloadTaskDao.insertTask(task)
        Log.d(
            "DownloadRepository",
            "addDownloadTask: new task created, id=$finalTaskId, fileName=$uniqueFileName"
        )

        // 尝试开始下载
        checkAndStartPendingTask()

        return task
    }

    /**
     * 生成唯一的文件名
     * 检查规则：
     * 1. 下载目录中是否已有同名文件
     * 2. 数据库中是否已有同名任务
     * 如果有冲突，自动添加序号如 xxx(1).pdf
     */
    private suspend fun generateUniqueFileName(
        downloadDir: File,
        originalFileName: String
    ): String {
        // 分离文件名和扩展名
        val lastDotIndex = originalFileName.lastIndexOf('.')
        val (nameWithoutExt, extension) = if (lastDotIndex > 0) {
            originalFileName.substring(0, lastDotIndex) to originalFileName.substring(lastDotIndex)
        } else {
            originalFileName to ""
        }

        var candidateName = originalFileName
        var counter = 1

        while (isFileNameConflict(downloadDir, candidateName)) {
            candidateName = "${nameWithoutExt}($counter)$extension"
            counter++
            // 防止无限循环
            if (counter > 1000) {
                // 使用时间戳确保唯一性
                candidateName = "${nameWithoutExt}_${System.currentTimeMillis()}$extension"
                break
            }
        }

        if (candidateName != originalFileName) {
            Log.d(
                "DownloadRepository",
                "generateUniqueFileName: $originalFileName -> $candidateName"
            )
        }

        return candidateName
    }

    /**
     * 检查文件名是否冲突
     */
    private suspend fun isFileNameConflict(downloadDir: File, fileName: String): Boolean {
        // 检查文件是否已存在于下载目录
        val file = File(downloadDir, fileName)
        if (file.exists()) {
            return true
        }

        // 检查数据库中是否已有同名任务（任何状态）
        val allTasks = downloadTaskDao.getAllTasksSync()
        return allTasks.any { it.fileName == fileName }
    }

    /**
     * 暂停下载任务
     */
    suspend fun pauseTask(taskId: String) {
        val task = downloadTaskDao.getTaskById(taskId) ?: return

        if (task.status != DownloadTaskStatus.RUNNING) {
            return
        }

        // 取消 OkDownload 任务
        activeDownloadTasks[taskId]?.cancel()
        activeDownloadTasks.remove(taskId)

        // 更新数据库状态
        downloadTaskDao.updateTaskStatus(taskId, DownloadTaskStatus.PAUSED)

        // 检查是否有等待中的任务可以开始
        checkAndStartPendingTask()
    }

    /**
     * 继续（恢复）下载任务
     */
    suspend fun resumeTask(taskId: String) {
        val task = downloadTaskDao.getTaskById(taskId) ?: return

        if (!task.canResume()) {
            return
        }

        // 检查是否达到最大并发数
        val runningCount = downloadTaskDao.getRunningTaskCount()
        if (runningCount >= MAX_PARALLEL_DOWNLOADS) {
            // 将任务设为等待状态
            downloadTaskDao.updateTaskStatus(taskId, DownloadTaskStatus.PENDING)
            return
        }

        // 开始下载
        startDownloadInternal(task)
    }

    /**
     * 重试下载任务（用于失败的任务）
     */
    suspend fun retryTask(taskId: String) {
        val task = downloadTaskDao.getTaskById(taskId) ?: return

        if (task.status != DownloadTaskStatus.ERROR) {
            return
        }

        Log.d("DownloadRepository", "retryTask: taskId=$taskId, resetting and restarting")

        // 删除不完整的文件和临时文件
        try {
            val file = File(task.filePath)
            if (file.exists()) {
                file.delete()
                Log.d("DownloadRepository", "retryTask: deleted incomplete file")
            }
            // 删除 OkDownload 的断点信息文件
            val tempFile = File(task.filePath + ".temp")
            if (tempFile.exists()) {
                tempFile.delete()
            }
            // 删除 OkDownload 的 sqlite 断点数据库
            val breakpointDir = File(file.parentFile, ".okdownload")
            if (breakpointDir.exists()) {
                breakpointDir.listFiles()?.forEach {
                    if (it.name.contains(task.fileName)) {
                        it.delete()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("DownloadRepository", "retryTask: error cleaning files", e)
        }

        // 重置下载进度和状态
        val now = System.currentTimeMillis()
        val resetTask = task.copy(
            status = DownloadTaskStatus.PENDING,
            downloadedBytes = 0,
            lastUpdateTime = now
        )
        downloadTaskDao.updateTask(resetTask)

        // 清除速度计算器
        DownloadSpeedManager.removeCalculator(taskId)

        // 尝试开始下载
        val runningCount = downloadTaskDao.getRunningTaskCount()
        if (runningCount >= MAX_PARALLEL_DOWNLOADS) {
            downloadTaskDao.updateTaskStatus(taskId, DownloadTaskStatus.PENDING)
        } else {
            startDownloadInternal(resetTask)
        }
    }

    /**
     * 删除下载任务
     * 同时删除已下载的文件
     */
    suspend fun deleteTask(taskId: String) {
        val task = downloadTaskDao.getTaskById(taskId) ?: return

        // 如果是正在下载的任务，先取消
        if (task.status == DownloadTaskStatus.RUNNING) {
            activeDownloadTasks[taskId]?.cancel()
            activeDownloadTasks.remove(taskId)
        }

        // 删除文件
        try {
            val file = File(task.filePath)
            if (file.exists()) {
                file.delete()
            }
            // 同时删除 .temp 临时文件
            val tempFile = File(task.filePath + ".temp")
            if (tempFile.exists()) {
                tempFile.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 删除数据库记录
        downloadTaskDao.deleteTask(taskId)
        
        // 清理任务的进度缓存
        clearTaskProgress(taskId)

        // 检查是否有等待中的任务可以开始
        checkAndStartPendingTask()
    }

    /**
     * 更新任务总大小（仅在当前值为 0 或无效时更新）
     * 避免断点续传时服务器返回的剩余大小覆盖正确的总大小
     */
    suspend fun updateTaskTotalBytes(taskId: String, totalBytes: Long) {
        val task = downloadTaskDao.getTaskById(taskId) ?: return
        // 只在当前总大小为 0 或无效时才更新
        // 如果已经有值，不要覆盖（避免断点续传时获取到错误的大小）
        if (task.totalBytes <= 0 && totalBytes > 0) {
            Log.d(
                "DownloadRepository",
                "updateTaskTotalBytes: taskId=$taskId, totalBytes=$totalBytes"
            )
            val updated =
                task.copy(totalBytes = totalBytes, lastUpdateTime = System.currentTimeMillis())
            downloadTaskDao.updateTask(updated)
        }
    }

    /**
     * 获取活跃任务数量（正在运行或等待中）
     */
    suspend fun getActiveTaskCount(): Int {
        return downloadTaskDao.getActiveTaskCount()
    }

    /**
     * 获取活跃任务流（正在运行或等待中）
     */
    fun getActiveTasks(): Flow<List<DownloadTaskEntity>> {
        return downloadTaskDao.getActiveTasks()
    }

    /**
     * 检查并启动等待中的任务
     */
    private suspend fun checkAndStartPendingTask() {
        val runningCount = downloadTaskDao.getRunningTaskCount()
        if (runningCount >= MAX_PARALLEL_DOWNLOADS) {
            return
        }

        val pendingTask = downloadTaskDao.getFirstPendingTask() ?: return
        startDownloadInternal(pendingTask)
    }

    /**
     * 内部开始下载的方法
     */
    private suspend fun startDownloadInternal(task: DownloadTaskEntity) {
        downloadTaskDao.updateTaskStatus(task.taskId, DownloadTaskStatus.RUNNING)
        val parentDir = File(task.filePath).parentFile
        val downloadTask = DownloadTask.Builder(task.url, parentDir)
            .setFilename(task.fileName)
            .setPassIfAlreadyCompleted(false)
            .setConnectionCount(1)
            .setPreAllocateLength(false)
            .build()
        activeDownloadTasks[task.taskId] = downloadTask

        downloadTask.enqueue(object : DownloadListener {
            override fun taskStart(downloadTask: DownloadTask) {
                repositoryScope.launch {
                    downloadTaskDao.updateTaskStatus(task.taskId, DownloadTaskStatus.RUNNING)
                }
                downloadCallback?.onTaskStarted(task.taskId)
            }

            override fun connectTrialStart(
                downloadTask: DownloadTask,
                requestHeaderFields: MutableMap<String, MutableList<String>>
            ) {
            }

            override fun connectTrialEnd(
                downloadTask: DownloadTask,
                responseCode: Int,
                responseHeaderFields: MutableMap<String, MutableList<String>>
            ) {
            }

            override fun downloadFromBeginning(
                downloadTask: DownloadTask,
                info: BreakpointInfo,
                cause: com.liulishuo.okdownload.core.cause.ResumeFailedCause
            ) {
            }

            override fun downloadFromBreakpoint(downloadTask: DownloadTask, info: BreakpointInfo) {
            }

            override fun connectStart(
                downloadTask: DownloadTask,
                blockIndex: Int,
                requestHeaderFields: MutableMap<String, MutableList<String>>
            ) {
            }

            override fun connectEnd(
                downloadTask: DownloadTask,
                blockIndex: Int,
                responseCode: Int,
                responseHeaderFields: MutableMap<String, MutableList<String>>
            ) {
            }

            override fun fetchStart(
                downloadTask: DownloadTask,
                blockIndex: Int,
                contentLength: Long
            ) {
                // 获取整个文件的总长度（不是单个 block 的长度）
                val totalLength = downloadTask.info?.totalLength ?: 0
                if (totalLength > 0) {
                    repositoryScope.launch {
                        updateTaskTotalBytes(task.taskId, totalLength)
                    }
                }
            }

            override fun fetchProgress(
                downloadTask: DownloadTask,
                blockIndex: Int,
                increaseBytes: Long
            ) {
                // 从 info 对象获取正确的总长度和已下载长度
                val info = downloadTask.info
                val totalBytes = if (info != null && info.totalLength > 0) {
                    info.totalLength
                } else {
                    task.totalBytes
                }
                // 使用数据库中保存的已下载字节数 + 本次增量
                val downloadedBytes = info?.totalOffset ?: task.downloadedBytes

                Log.d(
                    "DownloadRepository",
                    "fetchProgress: taskId=${task.taskId}, downloaded=$downloadedBytes, total=$totalBytes, increase=$increaseBytes"
                )

                // 使用批量缓冲机制更新进度和速度（减少数据库写入频率）
                bufferProgress(task.taskId, downloadedBytes)

                // 只在当前总大小为 0 或无效时才更新
                if (task.totalBytes <= 0 && totalBytes > 0) {
                    repositoryScope.launch {
                        updateTaskTotalBytes(task.taskId, totalBytes)
                    }
                }

                downloadCallback?.onProgress(task.taskId, downloadedBytes, totalBytes)
            }

            override fun fetchEnd(
                downloadTask: DownloadTask,
                blockIndex: Int,
                contentLength: Long
            ) {
            }

            override fun taskEnd(
                downloadTask: DownloadTask,
                cause: com.liulishuo.okdownload.core.cause.EndCause,
                realCause: Exception?
            ) {
                activeDownloadTasks.remove(task.taskId)
                
                // 清理任务的进度缓存和速度计算器
                clearTaskProgress(task.taskId)

                repositoryScope.launch {
                    // 先检查任务是否还存在（可能已被删除）
                    val currentTask = downloadTaskDao.getTaskById(task.taskId)
                    if (currentTask == null) {
                        Log.d(
                            "DownloadRepository",
                            "taskEnd: task ${task.taskId} no longer exists, skipping status update"
                        )
                        return@launch
                    }

                    when (cause) {
                        com.liulishuo.okdownload.core.cause.EndCause.COMPLETED -> {
                            val totalBytes = downloadTask.info?.totalLength ?: task.totalBytes

                            // 验证文件是否真的下载完成
                            val file = File(task.filePath)
                            val actualFileSize = if (file.exists()) file.length() else 0

                            Log.d(
                                "DownloadRepository",
                                "taskEnd COMPLETED: taskId=${task.taskId}, expected=$totalBytes, actual=$actualFileSize"
                            )

                            // 检查文件是否存在且大小正确（允许一定误差）
                            if (file.exists() && actualFileSize > 0 &&
                                (totalBytes <= 0 || actualFileSize >= totalBytes - 1024)
                            ) {
                                // 文件确实下载完成
                                downloadTaskDao.markAsCompleted(task.taskId, actualFileSize)
                                downloadCallback?.onTaskCompleted(task.taskId)
                            } else {
                                // 文件不完整，标记为错误
                                Log.e(
                                    "DownloadRepository",
                                    "taskEnd: file incomplete or missing, marking as error"
                                )
                                downloadTaskDao.updateTaskStatus(
                                    task.taskId,
                                    DownloadTaskStatus.ERROR
                                )
                                downloadCallback?.onTaskError(task.taskId, "文件下载不完整")
                            }
                        }

                        com.liulishuo.okdownload.core.cause.EndCause.CANCELED -> {
                            // 用户暂停或删除，检查当前状态
                            if (currentTask.status == DownloadTaskStatus.PAUSED) {
                                downloadCallback?.onTaskPaused(task.taskId)
                            } else if (currentTask.status == DownloadTaskStatus.PENDING) {
                                // 等待中状态不需要处理
                            }
                            // 如果任务已被删除（currentTask为null的情况已处理），不更新状态
                        }

                        else -> {
                            // 其他错误，只在任务不是手动暂停/删除的情况下更新为错误状态
                            if (currentTask.status == DownloadTaskStatus.RUNNING) {
                                downloadTaskDao.updateTaskStatus(
                                    task.taskId,
                                    DownloadTaskStatus.ERROR
                                )
                                downloadCallback?.onTaskError(
                                    task.taskId,
                                    realCause?.message ?: "下载失败"
                                )
                            }
                        }
                    }

                    // 检查是否有等待中的任务可以开始
                    checkAndStartPendingTask()
                }
            }
        })
    }

    /**
     * 清理下载相关的文件和断点信息
     */
    private fun cleanupDownloadFiles(filePath: String) {
        try {
            val file = File(filePath)
            val parentDir = file.parentFile

            // 删除不完整的主文件
            if (file.exists()) {
                file.delete()
                Log.d("DownloadRepository", "cleanupDownloadFiles: deleted $filePath")
            }

            // 删除 OkDownload 的临时文件
            val tempFile = File(filePath + ".temp")
            if (tempFile.exists()) {
                tempFile.delete()
            }

            // 删除 OkDownload 的断点信息文件
            if (parentDir != null && parentDir.exists()) {
                val breakpointDir = File(parentDir, ".okdownload")
                if (breakpointDir.exists()) {
                    val fileName = file.name
                    breakpointDir.listFiles()?.forEach { breakpointFile ->
                        // 断点文件命名通常是基于文件名的哈希或包含文件名
                        if (breakpointFile.name.contains(fileName) ||
                            breakpointFile.name.contains(file.nameWithoutExtension)
                        ) {
                            breakpointFile.delete()
                            Log.d(
                                "DownloadRepository",
                                "cleanupDownloadFiles: deleted breakpoint ${breakpointFile.name}"
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("DownloadRepository", "cleanupDownloadFiles: error", e)
        }
    }

    /**
     * 生成任务 ID
     */
    private fun generateTaskId(url: String, fileName: String): String {
        return "${url.hashCode()}_$fileName"
    }

    /**
     * 下载任务回调接口
     */
    interface DownloadTaskCallback {
        fun onTaskStarted(taskId: String)
        fun onTaskPaused(taskId: String)
        fun onTaskCompleted(taskId: String)
        fun onTaskError(taskId: String, errorMessage: String)
        fun onProgress(taskId: String, downloadedBytes: Long, totalBytes: Long)
    }
}
