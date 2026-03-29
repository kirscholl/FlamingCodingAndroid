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

    /**
     * 根据文件扩展名获取文件图标资源ID
     * @param extension 文件扩展名（带或不带点都可以）
     * @return drawable资源ID
     */
    fun getFileIconByExtension(extension: String): Int {
        val ext = extension.lowercase().removePrefix(".")
        return when (ext) {
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg" ->
                com.example.pffbrowser.R.drawable.pb_ic_file_image
            "mp4", "avi", "mkv", "mov", "wmv", "flv", "webm" ->
                com.example.pffbrowser.R.drawable.pb_ic_file_video
            "mp3", "wav", "flac", "aac", "ogg", "m4a" ->
                com.example.pffbrowser.R.drawable.pb_ic_file_audio
            "pdf" ->
                com.example.pffbrowser.R.drawable.pb_ic_file_pdf
            "doc", "docx" ->
                com.example.pffbrowser.R.drawable.pb_ic_file_word
            "xls", "xlsx" ->
                com.example.pffbrowser.R.drawable.pb_ic_file_excel
            "ppt", "pptx" ->
                com.example.pffbrowser.R.drawable.pb_ic_file_ppt
            "zip", "rar", "7z", "tar", "gz" ->
                com.example.pffbrowser.R.drawable.pb_ic_file_archive
            "apk" ->
                com.example.pffbrowser.R.drawable.pb_ic_file_apk
            "txt", "log" ->
                com.example.pffbrowser.R.drawable.pb_ic_file_text
            else ->
                com.example.pffbrowser.R.drawable.pb_ic_file_unknown
        }
    }

    /**
     * 格式化文件大小
     * @param bytes 文件大小（字节）
     * @return 格式化后的字符串，如 "1.5 MB"
     */
    fun formatFileSize(bytes: Long): String {
        if (bytes < 0) return "未知大小"
        if (bytes < 1024) return "$bytes B"

        val kb = bytes / 1024.0
        if (kb < 1024) return String.format("%.1f KB", kb)

        val mb = kb / 1024.0
        if (mb < 1024) return String.format("%.1f MB", mb)

        val gb = mb / 1024.0
        return String.format("%.2f GB", gb)
    }
}