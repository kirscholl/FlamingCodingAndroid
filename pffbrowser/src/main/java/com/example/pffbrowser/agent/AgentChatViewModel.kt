package com.example.pffbrowser.agent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pffbrowser.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AgentChatViewModel @Inject constructor() : BaseViewModel() {

    private val _messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _isStreaming = MutableLiveData(false)
    val isStreaming: LiveData<Boolean> = _isStreaming

    private val mockResponses = listOf(
        "你好！我是AI助手，很高兴为你服务。我可以帮你解答各种问题，提供建议和信息。无论是技术问题、生活建议还是知识查询，我都会尽力帮助你。请随时告诉我你需要什么帮助！",
        "这是一个很好的问题。让我详细为你分析一下。首先，我们需要考虑多个方面的因素。从技术角度来看，这个问题涉及到系统架构、性能优化和用户体验等多个维度。我建议采用渐进式的方法来解决，先从最核心的功能开始实现，然后逐步完善其他特性。",
        "根据我的理解，你提到的这个场景确实比较复杂。在实际应用中，我们通常会采用分层架构来处理这类问题。底层负责数据处理和存储，中间层处理业务逻辑，上层则专注于用户交互。这样的设计可以让系统更加灵活和可维护。同时，我们还需要考虑异常处理、日志记录和性能监控等方面。",
        "非常感谢你的提问！这让我想到了一个有趣的观点。在软件开发中，我们经常面临在简洁性和功能性之间做出权衡的情况。我的建议是，始终以用户需求为核心，优先实现最重要的功能，然后根据实际反馈进行迭代优化。记住，完美是优秀的敌人，先让产品运行起来，再逐步改进。",
        "明白了，让我为你总结一下关键点。第一，确保基础架构稳定可靠；第二，注重代码质量和可维护性；第三，持续关注用户反馈并快速响应。在技术选型上，建议选择成熟稳定的方案，避免过度追求新技术而忽略了实际需求。最后，团队协作和文档管理也非常重要，这能大大提高开发效率。"
    )

    fun sendMessage(userInput: String) {
        if (userInput.isBlank() || _isStreaming.value == true) return

        // 添加用户消息
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = userInput,
            isUser = true
        )
        addMessage(userMessage)

        // 开始Agent流式回复
        startStreamingResponse()
    }

    private fun startStreamingResponse() {
        _isStreaming.value = true

        // 创建空的Agent消息
        val agentMessageId = UUID.randomUUID().toString()
        val agentMessage = ChatMessage(
            id = agentMessageId,
            content = "",
            isUser = false,
            isStreaming = true
        )
        addMessage(agentMessage)

        // 启动流式输出协程
        viewModelScope.launch {
            val response = mockResponses.random()
            var currentContent = ""

            response.forEach { char ->
                delay(50) // 控制输出速度
                currentContent += char
                updateMessage(agentMessageId, currentContent, isStreaming = true)
            }

            // 流式输出完成
            updateMessage(agentMessageId, currentContent, isStreaming = false)
            _isStreaming.value = false
        }
    }

    private fun addMessage(message: ChatMessage) {
        val currentList = _messages.value.orEmpty().toMutableList()
        currentList.add(message)
        _messages.value = currentList
    }

    private fun updateMessage(messageId: String, content: String, isStreaming: Boolean) {
        val currentList = _messages.value.orEmpty().toMutableList()
        val index = currentList.indexOfFirst { it.id == messageId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(
                content = content,
                isStreaming = isStreaming
            )
            _messages.value = currentList
        }
    }
}
