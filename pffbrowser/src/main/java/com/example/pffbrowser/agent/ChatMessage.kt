package com.example.pffbrowser.agent

data class ChatMessage(
    val id: String,
    val content: String,
    val isUser: Boolean,
    val isStreaming: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
