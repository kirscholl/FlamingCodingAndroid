package com.example.pffbrowser.download.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pffbrowser.base.BaseViewModel
import com.example.pffbrowser.download.database.DownloadTask
import com.example.pffbrowser.download.repository.DownloadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 下载列表的ViewModel
 * 负责管理下载任务列表的数据和操作
 */
@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val repository: DownloadRepository
) : BaseViewModel() {

    /**
     * 所有下载任务列表
     * 从Flow转换为LiveData供UI观察
     */
    val allTasks: LiveData<List<DownloadTask>> = repository.getAllTasks().asLiveData()

    /**
     * 创建下载任务
     * @param downloadInfo 下载信息
     * @param customFileName 自定义文件名（可选）
     * @return 任务ID
     */
//    fun createDownloadTask(
//        downloadInfo: DownloadDialogInfo,
//        customFileName: String? = null
//    ) = viewModelScope.launch {
//        repository.createDownloadTask(downloadInfo, customFileName)
//    }

    /**
     * 开始下载
     * @param taskId 任务ID
     */
    fun startDownload(taskId: Long) = viewModelScope.launch {
        repository.startDownload(taskId)
    }

    /**
     * 暂停下载
     * @param taskId 任务ID
     */
    fun pauseDownload(taskId: Long) = viewModelScope.launch {
        repository.pauseDownload(taskId)
    }

    /**
     * 恢复下载
     * @param taskId 任务ID
     */
    fun resumeDownload(taskId: Long) = viewModelScope.launch {
        repository.resumeDownload(taskId)
    }

    /**
     * 删除任务
     * @param taskId 任务ID
     * @param deleteFile 是否删除文件，默认为true
     */
    fun deleteTask(taskId: Long, deleteFile: Boolean = true) = viewModelScope.launch {
        repository.deleteTask(taskId, deleteFile)
    }
}
