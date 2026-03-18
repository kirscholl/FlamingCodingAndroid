package com.example.pffbrowser.utils

object UrlUtil {

    fun isDownloadableUrl(url: String): Boolean {
        val lowerUrl = url.lowercase()

        // 检查扩展名
        if (FileUtil.downloadableExtensions.any { lowerUrl.endsWith(it) }) {
            return true
        }

        // 检查常见的下载路径关键词
        val downloadIndicators = listOf(
            "/download/", "/downloads/", "/file/", "/files/",
            "download.php", "file.php", "attachment", "blob:"
        )

        if (downloadIndicators.any { lowerUrl.contains(it) }) {
            return true
        }

        return false
    }
}