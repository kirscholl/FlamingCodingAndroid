package com.example.pffbrowser.webview.benchmark

import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.example.pffbrowser.webview.PbWebView
import com.example.pffbrowser.webview.pool.WebViewPool

/**
 * WebView性能测试工具
 * 用于对比优化前后的性能差异
 */
object WebViewBenchmark {

    private const val TAG = "WebViewBenchmark"

    /**
     * 测试WebView创建时间
     */
    fun benchmarkWebViewCreation(context: Context, iterations: Int = 10): BenchmarkResult {
        val times = mutableListOf<Long>()

        repeat(iterations) {
            val startTime = SystemClock.elapsedRealtime()

            // 创建WebView
            val webView = PbWebView(context)

            val endTime = SystemClock.elapsedRealtime()
            val duration = endTime - startTime
            times.add(duration)

            // 销毁WebView
            webView.destroy()

            Log.d(TAG, "WebView创建耗时: ${duration}ms")
        }

        return BenchmarkResult(
            name = "WebView创建",
            times = times,
            averageTime = times.average(),
            minTime = times.minOrNull() ?: 0L,
            maxTime = times.maxOrNull() ?: 0L
        )
    }

    /**
     * 测试WebView池获取时间
     */
    fun benchmarkWebViewPoolObtain(context: Context, iterations: Int = 10): BenchmarkResult {
        val times = mutableListOf<Long>()

        repeat(iterations) {
            val startTime = SystemClock.elapsedRealtime()

            // 从池中获取WebView
            val webView = WebViewPool.obtain(context)

            val endTime = SystemClock.elapsedRealtime()
            val duration = endTime - startTime
            times.add(duration)

            // 回收到池中
            WebViewPool.recycle(webView)

            Log.d(TAG, "WebView池获取耗时: ${duration}ms")
        }

        return BenchmarkResult(
            name = "WebView池获取",
            times = times,
            averageTime = times.average(),
            minTime = times.minOrNull() ?: 0L,
            maxTime = times.maxOrNull() ?: 0L
        )
    }

    /**
     * 对比测试
     */
    fun comparePerformance(context: Context): ComparisonResult {
        Log.d(TAG, "开始性能对比测试...")

        // 测试直接创建
        val directResult = benchmarkWebViewCreation(context)

        // 等待一段时间
        Thread.sleep(1000)

        // 测试池化获取
        val poolResult = benchmarkWebViewPoolObtain(context)

        val improvement = ((directResult.averageTime - poolResult.averageTime) /
                directResult.averageTime * 100).toInt()

        Log.d(TAG, "性能对比完成:")
        Log.d(TAG, "直接创建平均耗时: ${directResult.averageTime}ms")
        Log.d(TAG, "池化获取平均耗时: ${poolResult.averageTime}ms")
        Log.d(TAG, "性能提升: $improvement%")

        return ComparisonResult(
            directCreation = directResult,
            poolObtain = poolResult,
            improvementPercentage = improvement
        )
    }
}

/**
 * 基准测试结果
 */
data class BenchmarkResult(
    val name: String,
    val times: List<Long>,
    val averageTime: Double,
    val minTime: Long,
    val maxTime: Long
) {
    override fun toString(): String {
        return """
            $name:
            - 平均耗时: ${averageTime.toInt()}ms
            - 最小耗时: ${minTime}ms
            - 最大耗时: ${maxTime}ms
            - 测试次数: ${times.size}
        """.trimIndent()
    }
}

/**
 * 对比结果
 */
data class ComparisonResult(
    val directCreation: BenchmarkResult,
    val poolObtain: BenchmarkResult,
    val improvementPercentage: Int
) {
    override fun toString(): String {
        return """
            性能对比结果:

            ${directCreation}

            ${poolObtain}

            性能提升: $improvementPercentage%
        """.trimIndent()
    }
}
