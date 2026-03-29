package com.example.pffbrowser.download.database

import androidx.room.*
import com.example.pffbrowser.download.DownloadStatus
import kotlinx.coroutines.flow.Flow

/**
 * 下载任务DAO
 * 提供数据库操作接口
 */
@Dao
interface DownloadTaskDao {

    // ========== 插入操作 ==========

    /**
     * 插入单个任务
     * @return 插入任务的ID
     */
    @Insert
    suspend fun insert(task: DownloadTask): Long

    /**
     * 批量插入任务
     * @return 插入任务的ID列表
     */
    @Insert
    suspend fun insertAll(tasks: List<DownloadTask>): List<Long>

    // ========== 更新操作 ==========

    /**
     * 更新整个任务对象
     */
    @Update
    suspend fun update(task: DownloadTask)

    /**
     * 批量更新任务
     */
    @Update
    suspend fun updateAll(tasks: List<DownloadTask>)

    /**
     * 更新下载进度（性能优化：只更新必要字段）
     * @param id 任务ID
     * @param bytes 已下载字节数
     * @param progress 进度百分比
     * @param time 更新时间
     */
    @Query("""
        UPDATE download_tasks
        SET downloadedBytes = :bytes,
            progress = :progress,
            updateTime = :time
        WHERE id = :id
    """)
    suspend fun updateProgress(id: Long, bytes: Long, progress: Int, time: Long)

    /**
     * 更新任务状态
     * @param id 任务ID
     * @param status 新状态
     * @param time 更新时间
     */
    @Query("""
        UPDATE download_tasks
        SET status = :status,
            updateTime = :time
        WHERE id = :id
    """)
    suspend fun updateStatus(id: Long, status: DownloadStatus, time: Long)

    /**
     * 更新任务状态和错误信息
     */
    @Query("""
        UPDATE download_tasks
        SET status = :status,
            errorMsg = :errorMsg,
            updateTime = :time
        WHERE id = :id
    """)
    suspend fun updateStatusWithError(id: Long, status: DownloadStatus, errorMsg: String?, time: Long)

    /**
     * 标记任务为完成
     */
    @Query("""
        UPDATE download_tasks
        SET status = :status,
            progress = 100,
            completeTime = :completeTime,
            updateTime = :completeTime
        WHERE id = :id
    """)
    suspend fun markAsCompleted(id: Long, status: DownloadStatus = DownloadStatus.COMPLETED, completeTime: Long)

    /**
     * 更新OkDownload任务ID
     */
    @Query("""
        UPDATE download_tasks
        SET okDownloadId = :okDownloadId
        WHERE id = :id
    """)
    suspend fun updateOkDownloadId(id: Long, okDownloadId: Int)

    // ========== 查询操作 ==========

    /**
     * 查询所有任务（Flow自动监听变化）
     * 按创建时间倒序排列
     */
    @Query("SELECT * FROM download_tasks ORDER BY createTime DESC")
    fun getAllTasksFlow(): Flow<List<DownloadTask>>

    /**
     * 查询所有任务（一次性查询）
     */
    @Query("SELECT * FROM download_tasks ORDER BY createTime DESC")
    suspend fun getAllTasks(): List<DownloadTask>

    /**
     * 根据ID查询任务
     */
    @Query("SELECT * FROM download_tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): DownloadTask?

    /**
     * 根据ID查询任务（Flow）
     */
    @Query("SELECT * FROM download_tasks WHERE id = :id")
    fun getTaskByIdFlow(id: Long): Flow<DownloadTask?>

    /**
     * 查询正在下载的任务
     */
    @Query("SELECT * FROM download_tasks WHERE status = 'DOWNLOADING' ORDER BY createTime ASC")
    suspend fun getDownloadingTasks(): List<DownloadTask>

    /**
     * 查询等待中的任务
     * 按创建时间升序（先创建的先下载）
     */
    @Query("SELECT * FROM download_tasks WHERE status = 'PENDING' ORDER BY createTime ASC")
    suspend fun getPendingTasks(): List<DownloadTask>

    /**
     * 查询已完成的任务
     */
    @Query("SELECT * FROM download_tasks WHERE status = 'COMPLETED' ORDER BY completeTime DESC")
    suspend fun getCompletedTasks(): List<DownloadTask>

    /**
     * 查询失败的任务
     */
    @Query("SELECT * FROM download_tasks WHERE status = 'FAILED' ORDER BY updateTime DESC")
    suspend fun getFailedTasks(): List<DownloadTask>

    /**
     * 根据状态查询任务
     */
    @Query("SELECT * FROM download_tasks WHERE status = :status ORDER BY createTime DESC")
    suspend fun getTasksByStatus(status: DownloadStatus): List<DownloadTask>

    /**
     * 查询正在下载的任务数量
     */
    @Query("SELECT COUNT(*) FROM download_tasks WHERE status = 'DOWNLOADING'")
    suspend fun getDownloadingCount(): Int

    /**
     * 查询等待中的任务数量
     */
    @Query("SELECT COUNT(*) FROM download_tasks WHERE status = 'PENDING'")
    suspend fun getPendingCount(): Int

    /**
     * 根据URL查询任务（检查是否已存在）
     */
    @Query("SELECT * FROM download_tasks WHERE url = :url LIMIT 1")
    suspend fun getTaskByUrl(url: String): DownloadTask?

    // ========== 删除操作 ==========

    /**
     * 删除单个任务
     */
    @Delete
    suspend fun delete(task: DownloadTask)

    /**
     * 根据ID删除任务
     */
    @Query("DELETE FROM download_tasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * 批量删除任务
     */
    @Delete
    suspend fun deleteAll(tasks: List<DownloadTask>)

    /**
     * 删除所有已完成的任务
     */
    @Query("DELETE FROM download_tasks WHERE status = 'COMPLETED'")
    suspend fun deleteAllCompleted()

    /**
     * 删除所有失败的任务
     */
    @Query("DELETE FROM download_tasks WHERE status = 'FAILED'")
    suspend fun deleteAllFailed()

    /**
     * 清空所有任务
     */
    @Query("DELETE FROM download_tasks")
    suspend fun deleteAllTasks()

    // ========== 统计操作 ==========

    /**
     * 获取任务总数
     */
    @Query("SELECT COUNT(*) FROM download_tasks")
    suspend fun getTaskCount(): Int

    /**
     * 获取已完成任务数量
     */
    @Query("SELECT COUNT(*) FROM download_tasks WHERE status = 'COMPLETED'")
    suspend fun getCompletedCount(): Int

    /**
     * 获取总下载大小（已完成的任务）
     */
    @Query("SELECT SUM(totalBytes) FROM download_tasks WHERE status = 'COMPLETED'")
    suspend fun getTotalDownloadedSize(): Long?
}
