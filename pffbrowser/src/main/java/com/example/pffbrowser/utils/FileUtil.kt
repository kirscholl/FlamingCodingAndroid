package com.example.pffbrowser.utils

object FileUtil {

    val downloadableExtensions = listOf(
        ".apk", ".pdf", ".zip", ".rar", ".7z", ".tar", ".gz",
        ".mp3", ".mp4", ".avi", ".mov", ".wmv", ".flv",
        ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx",
        ".txt", ".csv", ".json", ".xml", ".html", ".htm",
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp",
        ".exe", ".dmg", ".pkg", ".deb", ".rpm"
    )

    fun extractFileName(contentDisposition: String?, url: String): String {
        var filename = if (contentDisposition != null && contentDisposition.contains("filename=")) {
            // 从 Content-Disposition 中解析文件名 (例如: attachment; filename="example.pdf")
            contentDisposition.substringAfter("filename=").replace("\"", "")
        } else {
            // 备选方案：从 URL 路径中截取最后一段
            url.substringAfterLast("/")
        }

        // 清理文件名中的非法字符
        filename = filename.replace(Regex("[\\\\/:*?\"<>|]"), "_")

        // 如果文件名为空或只有扩展名，使用默认名称
        if (filename.isBlank() || filename == "." || filename.startsWith(".")) {
            val ext = if (filename.startsWith(".")) filename else ".bin"
            filename = "download_${System.currentTimeMillis()}$ext"
        }

        return filename
    }
}