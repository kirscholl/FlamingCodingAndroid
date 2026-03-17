package com.example.pffbrowser.utils

object SharedPreferencesUtil {

    const val SEARCH_HISTORY = "SearchHistory"

//    private fun saveSearchKeyword(keyword: String) {
//        // 1. 读取现有历史
//        val currentHistory = prefs.getString("history", "") ?: ""
//        val historySet = currentHistory.split(",").filter { it.isNotEmpty() }.toMutableSet()
//
//        // 2. 去重：如果已存在，先移除旧的
//        if (historySet.contains(keyword)) {
//            historySet.remove(keyword)
//        }
//
//        // 3. 将新关键词加到最前面
//        val newHistory = listOf(keyword) + historySet.toList()
//
//        // 4. 限制条目数量，例如最多保存10条
//        val limitedHistory = newHistory.take(10)
//
//        // 5. 保存回 SharedPreferences
//        prefs.edit().putString("history", limitedHistory.joinToString(",")).apply()
//
//        // 6. 更新 ListView
//        loadSearchHistory()
//    }
//
//    // 从 SharedPreferences 加载历史
//    private fun loadSearchHistory() {
//        val historyString = prefs.getString("history", "")
//        if (!historyString.isNullOrEmpty()) {
//            // 假设用逗号分隔
//            historyList.clear()
//            historyList.addAll(historyString.split(",").filter { it.isNotEmpty() })
//            historyAdapter.notifyDataSetChanged()
//        }
//    }
}