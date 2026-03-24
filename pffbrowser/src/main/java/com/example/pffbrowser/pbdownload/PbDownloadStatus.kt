package com.example.pffbrowser.pbdownload

enum class PbDownloadStatus {
    PENDING,     // 等待中
    DOWNLOADING, // 下载中
    PAUSED,      // 已暂停
    COMPLETED,   // 已完成
    ERROR,       // 出错
    CANCELLED    // 已取消
}

enum class PbDownloadAction {
    START,
    PAUSE,
    CONTINUE,
}