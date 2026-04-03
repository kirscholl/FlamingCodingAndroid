package com.example.pffbrowser.common

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterIsInstance

// AppEvent.kt
sealed class AppEvent {
    data class ShowToast(val message: String) : AppEvent()
    data class UserLoggedIn(val userId: String) : AppEvent()
    object UserLoggedOut : AppEvent()
}

object FlowEventBus {
    // 配置 SharedFlow
    // replay = 0: 新订阅者不会收到之前的旧事件（符合普通EventBus的特性）
    // extraBufferCapacity = 1: 给定一个额外的缓冲，允许 tryEmit 在没有挂起的情况下发送成功
    // onBufferOverflow = BufferOverflow.DROP_OLDEST: 缓冲区满时丢弃最旧的事件
    private val _events = MutableSharedFlow<AppEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    // 对外暴露只读的 SharedFlow
    val events = _events.asSharedFlow()

    /**
     * 挂起函数发送事件 (推荐在协程中使用，最安全)
     */
    suspend fun post(event: AppEvent) {
        _events.emit(event)
    }

    /**
     * 非挂起函数发送事件 (适用于非协程环境，如普通的 Click Listener)
     * 注意：依赖于 extraBufferCapacity 的配置
     */
    fun tryPost(event: AppEvent) {
        _events.tryEmit(event)
    }

    /**
     * 监听特定类型的事件 (使用内联函数和 reified 提取特定类型)
     */
    inline fun <reified T : AppEvent> observeEvent(): kotlinx.coroutines.flow.Flow<T> {
        return events.filterIsInstance<T>()
    }
}