package com.example.pffbrowser.download.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

/**
 * 文件打开工具
 * 使用系统应用打开文件
 */
object FileOpenHelper {

    /**
     * 打开文件
     *
     * @param context 上下文
     * @param file 要打开的文件
     */
    fun openFile(context: Context, file: File) {
        if (!file.exists()) {
            Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val uri = getFileUri(context, file)
            val mimeType = getMimeType(file)
            val intent = createOpenIntent(uri, mimeType)

            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "没有找到可以打开此文件的应用", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "打开文件失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    /**
     * 根据文件路径打开文件
     *
     * @param context 上下文
     * @param filePath 文件路径
     */
    fun openFile(context: Context, filePath: String) {
        val file = File(filePath)
        openFile(context, file)
    }

    /**
     * 获取文件URI（使用FileProvider）
     *
     * @param context 上下文
     * @param file 文件
     * @return 文件URI
     */
    private fun getFileUri(context: Context, file: File): Uri {
        return try {
            // Android 7.0+ 使用FileProvider
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            // 降级方案：使用file://协议（Android 7.0以下）
            Uri.fromFile(file)
        }
    }

    /**
     * 创建打开文件的Intent
     *
     * @param uri 文件URI
     * @param mimeType MIME类型
     * @return Intent
     */
    private fun createOpenIntent(uri: Uri, mimeType: String): Intent {
        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    /**
     * 获取文件的MIME类型
     *
     * @param file 文件
     * @return MIME类型
     */
    fun getMimeType(file: File): String {
        val extension = file.extension.lowercase()
        return getMimeTypeFromExtension(extension)
    }

    /**
     * 根据扩展名获取MIME类型
     *
     * @param extension 文件扩展名（不含点）
     * @return MIME类型
     */
    fun getMimeTypeFromExtension(extension: String): String {
        // 先尝试使用系统的MimeTypeMap
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        if (mimeType != null) {
            return mimeType
        }

        // 如果系统无法识别，使用自定义映射
        return when (extension) {
            // 图片
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "bmp" -> "image/bmp"
            "svg" -> "image/svg+xml"

            // 视频
            "mp4" -> "video/mp4"
            "avi" -> "video/x-msvideo"
            "mkv" -> "video/x-matroska"
            "mov" -> "video/quicktime"
            "wmv" -> "video/x-ms-wmv"
            "flv" -> "video/x-flv"
            "webm" -> "video/webm"

            // 音频
            "mp3" -> "audio/mpeg"
            "wav" -> "audio/wav"
            "flac" -> "audio/flac"
            "aac" -> "audio/aac"
            "ogg" -> "audio/ogg"
            "m4a" -> "audio/mp4"

            // 文档
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "ppt" -> "application/vnd.ms-powerpoint"
            "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"

            // 压缩包
            "zip" -> "application/zip"
            "rar" -> "application/x-rar-compressed"
            "7z" -> "application/x-7z-compressed"
            "tar" -> "application/x-tar"
            "gz" -> "application/gzip"

            // APK
            "apk" -> "application/vnd.android.package-archive"

            // 文本
            "txt" -> "text/plain"
            "log" -> "text/plain"
            "json" -> "application/json"
            "xml" -> "application/xml"
            "html", "htm" -> "text/html"
            "css" -> "text/css"
            "js" -> "application/javascript"

            // 默认
            else -> "application/octet-stream"
        }
    }

    /**
     * 检查是否有应用可以打开该文件
     *
     * @param context 上下文
     * @param file 文件
     * @return true表示有应用可以打开，false表示没有
     */
    fun canOpenFile(context: Context, file: File): Boolean {
        return try {
            val uri = getFileUri(context, file)
            val mimeType = getMimeType(file)
            val intent = createOpenIntent(uri, mimeType)

            val packageManager = context.packageManager
            val activities = packageManager.queryIntentActivities(intent, 0)
            activities.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 分享文件
     *
     * @param context 上下文
     * @param file 要分享的文件
     */
    fun shareFile(context: Context, file: File) {
        if (!file.exists()) {
            Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val uri = getFileUri(context, file)
            val mimeType = getMimeType(file)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(intent, "分享文件")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "分享文件失败: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
