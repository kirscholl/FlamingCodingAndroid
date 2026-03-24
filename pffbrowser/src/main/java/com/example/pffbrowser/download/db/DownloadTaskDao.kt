package com.example.pffbrowser.download.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * 下载任务数据访问对象
 */
@Dao
interface DownloadTaskDao {

    /**
     * 获取所有下载任务，按创建时间倒序（最新的在前）
     */
    @Query("SELECT * FROM download_tasks ORDER BY createTime DESC")
    fun getAllTasks(): Flow<List<DownloadTaskEntity>>

    /**
     * 获取所有下载任务（同步）
     */
    @Query("SELECT * FROM download_tasks ORDER BY createTime DESC")
    suspend fun getAllTasksSync(): List<DownloadTaskEntity>

    /**
     * 根据 ID 获取任务
     */
    @Query("SELECT * FROM download_tasks WHERE taskId = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: String): DownloadTaskEntity?

    /**
     * 根据 ID 获取任务（Flow）
     */
    @Query("SELECT * FROM download_tasks WHERE taskId = :taskId LIMIT 1")
    fun getTaskByIdFlow(taskId: String): Flow<DownloadTaskEntity?>

    /**
     * 获取正在运行的任务
     */
    @Query("SELECT * FROM download_tasks WHERE status = 'RUNNING' ORDER BY createTime DESC")
    suspend fun getRunningTasks(): List<DownloadTaskEntity>

    /**
     * 获取正在运行的任务数量
     */
    @Query("SELECT COUNT(*) FROM download_tasks WHERE status = 'RUNNING'")
    suspend fun getRunningTaskCount(): Int

    /**
     * 获取活跃任务数量（正在运行或等待中）
     */
    @Query("SELECT COUNT(*) FROM download_tasks WHERE status = 'RUNNING' OR status = 'PENDING'")
    suspend fun getActiveTaskCount(): Int

    /**
     * 获取活跃任务（正在运行或等待中）
     */
    @Query("SELECT * FROM download_tasks WHERE status = 'RUNNING' OR status = 'PENDING' ORDER BY createTime DESC")
    fun getActiveTasks(): Flow<List<DownloadTaskEntity>>

    /**
     * 获取等待中的任务，按创建时间正序（先等的先开始）
     */
    @Query("SELECT * FROM download_tasks WHERE status = 'PENDING' ORDER BY createTime ASC")
    suspend fun getPendingTasks(): List<DownloadTaskEntity>

    /**
     * 获取第一个等待中的任务
     */
    @Query("SELECT * FROM download_tasks WHERE status = 'PENDING' ORDER BY createTime ASC LIMIT 1")
    suspend fun getFirstPendingTask(): DownloadTaskEntity?

    /**
     * 插入任务
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: DownloadTaskEntity)

    /**
     * 更新任务
     */
    @Update
    suspend fun updateTask(task: DownloadTaskEntity)

    /**
     * 更新任务状态
     */
    @Query("UPDATE download_tasks SET status = :status, lastUpdateTime = :updateTime WHERE taskId = :taskId")
    suspend fun updateTaskStatus(taskId: String, status: DownloadTaskStatus, updateTime: Long = System.currentTimeMillis())

    /**
     * 更新下载进度
     */
    @Query("UPDATE download_tasks SET downloadedBytes = :downloadedBytes, lastUpdateTime = :updateTime WHERE taskId = :taskId")
    suspend fun updateProgress(taskId: String, downloadedBytes: Long, updateTime: Long = System.currentTimeMillis())

    /**
     * 更新任务为完成状态
     */
    @Query("UPDATE download_tasks SET status = 'COMPLETED', downloadedBytes = :totalBytes, completeTime = :completeTime, lastUpdateTime = :completeTime WHERE taskId = :taskId")
    suspend fun markAsCompleted(taskId: String, totalBytes: Long, completeTime: Long = System.currentTimeMillis())

    /**
     * 删除任务
     */
    @Query("DELETE FROM download_tasks WHERE taskId = :taskId")
    suspend fun deleteTask(taskId: String)

    /**
     * 删除所有已完成任务
     */
    @Query("DELETE FROM download_tasks WHERE status = 'COMPLETED'")
    suspend fun deleteAllCompleted()

    /**
     * 检查是否存在相同任务
     */
    @Query("SELECT COUNT(*) FROM download_tasks WHERE url = :url AND fileName = :fileName")
    suspend fun exists(url: String, fileName: String): Int
}
