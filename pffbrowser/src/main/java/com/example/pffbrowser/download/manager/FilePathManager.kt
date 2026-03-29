package com.example.pffbrowser.download.manager

import android.content.Context
import android.os.Environment
import android.os.StatFs
import java.io.File

/**
 * 文件路径管理器
 * 负责管理下载文件的存储路径、文件名生成、磁盘空间检查等
 */
object FilePathManager {

    /**
     * 获取下载目录
     * Android 10+ 使用App私有目录，无需存储权限
     *
     * @param context 上下文
     * @return 下载目录File对象
     */
    fun getDownloadDir(context: Context): File {
        // 优先使用外部存储的Download目录
        val externalDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        if (externalDir != null && externalDir.exists()) {
            return externalDir
        }

        // 备选：使用内部存储
        val internalDir = File(context.filesDir, "downloads")
        if (!internalDir.exists()) {
            internalDir.mkdirs()
        }
        return internalDir
    }

    /**
     * 生成唯一文件路径（避免文件名冲突）
     *
     * @param context 上下文
     * @param fileName 原始文件名
     * @return 唯一的文件完整路径
     */
    fun generateUniqueFilePath(context: Context, fileName: String): String {
        val downloadDir = getDownloadDir(context)
        return generateUniqueFilePath(downloadDir, fileName)
    }

    /**
     * 在指定目录生成唯一文件路径
     *
     * @param dir 目标目录
     * @param fileName 原始文件名
     * @return 唯一的文件完整路径
     */
    fun generateUniqueFilePath(dir: File, fileName: String): String {
        var file = File(dir, fileName)

        // 如果文件不存在，直接返回
        if (!file.exists()) {
            return file.absolutePath
        }

        // 文件已存在，生成新文件名：filename_(1).ext
        val nameWithoutExt = fileName.substringBeforeLast(".", fileName)
        val ext = if (fileName.contains(".")) {
            fileName.substringAfterLast(".")
        } else {
            ""
        }

        var counter = 1
        while (file.exists()) {
            val newName = if (ext.isNotEmpty()) {
                "${nameWithoutExt}_($counter).$ext"
            } else {
                "${nameWithoutExt}_($counter)"
            }
            file = File(dir, newName)
            counter++

            // 防止无限循环
            if (counter > 9999) {
                // 使用时间戳作为后缀
                val timestamp = System.currentTimeMillis()
                val finalName = if (ext.isNotEmpty()) {
                    "${nameWithoutExt}_$timestamp.$ext"
                } else {
                    "${nameWithoutExt}_$timestamp"
                }
                file = File(dir, finalName)
                break
            }
        }

        return file.absolutePath
    }

    /**
     * 检查磁盘可用空间是否足够
     *
     * @param context 上下文
     * @param requiredBytes 需要的字节数
     * @return true表示空间足够，false表示空间不足
     */
    fun hasEnoughSpace(context: Context, requiredBytes: Long): Boolean {
        if (requiredBytes <= 0) {
            // 未知大小，假设空间足够
            return true
        }

        val downloadDir = getDownloadDir(context)
        val availableBytes = getAvailableSpace(downloadDir)

        // 预留100MB的缓冲空间
        val bufferBytes = 100 * 1024 * 1024L
        return availableBytes > (requiredBytes + bufferBytes)
    }

    /**
     * 获取指定目录的可用空间（字节）
     *
     * @param dir 目录
     * @return 可用空间字节数
     */
    fun getAvailableSpace(dir: File): Long {
        return try {
            val stat = StatFs(dir.absolutePath)
            stat.availableBlocksLong * stat.blockSizeLong
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * 获取指定目录的总空间（字节）
     *
     * @param dir 目录
     * @return 总空间字节数
     */
    fun getTotalSpace(dir: File): Long {
        return try {
            val stat = StatFs(dir.absolutePath)
            stat.blockCountLong * stat.blockSizeLong
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * 清理临时文件
     * 删除下载目录中的.tmp文件
     *
     * @param context 上下文
     * @return 清理的文件数量
     */
    fun cleanTempFiles(context: Context): Int {
        val downloadDir = getDownloadDir(context)
        var count = 0

        downloadDir.listFiles()?.forEach { file ->
            if (file.isFile && file.name.endsWith(".tmp")) {
                if (file.delete()) {
                    count++
                }
            }
        }

        return count
    }

    /**
     * 删除指定文件
     *
     * @param filePath 文件路径
     * @return true表示删除成功，false表示删除失败
     */
    fun deleteFile(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            } else {
                true  // 文件不存在，视为删除成功
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param filePath 文件路径
     * @return true表示文件存在，false表示文件不存在
     */
    fun fileExists(filePath: String): Boolean {
        return try {
            File(filePath).exists()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 获取文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小（字节），文件不存在返回0
     */
    fun getFileSize(filePath: String): Long {
        return try {
            val file = File(filePath)
            if (file.exists()) file.length() else 0L
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * 获取下载目录的使用情况
     *
     * @param context 上下文
     * @return Pair<已使用空间, 总空间>（字节）
     */
    fun getStorageInfo(context: Context): Pair<Long, Long> {
        val downloadDir = getDownloadDir(context)
        val totalSpace = getTotalSpace(downloadDir)
        val availableSpace = getAvailableSpace(downloadDir)
        val usedSpace = totalSpace - availableSpace
        return Pair(usedSpace, totalSpace)
    }
}
