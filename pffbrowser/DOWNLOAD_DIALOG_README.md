# 下载弹窗功能使用说明

## 功能概述

已实现浏览器下载弹窗功能，当WebView检测到下载链接时，会自动弹出下载对话框。

## 已实现的功能

### 1. 数据模型
- `DownloadDialogInfo`: 下载弹窗信息数据类（支持Parcelable）

### 2. UI组件
- `DownloadDialogFragment`: 底部弹出的下载对话框
  - 显示文件图标（根据文件类型自动匹配颜色）
  - 显示文件名（可编辑，但扩展名不可修改）
  - 显示完整URL（单行，超出显示省略号）
  - 显示文件大小（自动格式化为B/KB/MB/GB）
  - 两个按钮：取消、开始下载

### 3. WebView集成
- `PbWebView`: 添加了下载监听接口
- `BaseWebViewFragment`: 自动处理下载事件，显示弹窗

### 4. 工具类
- `FileUtil.getFileIconByExtension()`: 根据扩展名获取图标
- `FileUtil.formatFileSize()`: 格式化文件大小

## 文件图标颜色方案

| 文件类型 | 颜色 | 扩展名示例 |
|---------|------|-----------|
| 图片 | 绿色 (#4CAF50) | jpg, png, gif, webp |
| 视频 | 橙红色 (#FF5722) | mp4, avi, mkv |
| 音频 | 紫色 (#9C27B0) | mp3, wav, flac |
| PDF | 红色 (#F44336) | pdf |
| Word | 蓝色 (#2196F3) | doc, docx |
| Excel | 绿色 (#4CAF50) | xls, xlsx |
| PPT | 橙色 (#FF9800) | ppt, pptx |
| 压缩包 | 黄色 (#FFC107) | zip, rar, 7z |
| APK | 浅绿色 (#8BC34A) | apk |
| 文本 | 灰蓝色 (#607D8B) | txt, log |
| 未知 | 灰色 (#9E9E9E) | 其他 |

## 使用方式

### 自动使用（推荐）

所有继承自 `BaseWebViewFragment` 的Fragment都会自动支持下载弹窗功能，无需额外配置。

### 自定义下载逻辑

如果需要在用户点击"开始下载"后执行自定义逻辑，可以重写 `onDownloadConfirmed` 方法：

```kotlin
class MyWebViewFragment : BaseWebViewFragment<MyBinding, MyViewModel>() {

    override val mWebView: PbWebView
        get() = mViewBinding.webView

    override fun onDownloadConfirmed(
        fileName: String,
        url: String,
        downloadInfo: DownloadDialogInfo
    ) {
        // 在这里实现实际的下载逻辑
        // 例如：启动下载服务、使用OkDownload等
        Toast.makeText(requireContext(), "开始下载: $fileName", Toast.LENGTH_SHORT).show()
    }
}
```

## 测试方法

在 `SearchResultFragment` 中已经有测试代码：

```kotlin
mViewBinding.btnResSearch.setOnClickListener {
    // 测试下载10MB文件
    mWebView.loadUrl("http://speedtest.tele2.net/10MB.zip")
}
```

点击"重新搜索"按钮即可触发下载弹窗。

## 弹窗特性

1. **高度**: 固定为屏幕高度的50%
2. **圆角**: 顶部16dp圆角
3. **拖拽指示器**: 顶部显示小横条
4. **点击外部关闭**: 支持
5. **文件名编辑**: 支持，但扩展名不可修改
6. **文件名验证**: 不允许空文件名

## 后续扩展

当前实现只包含UI弹窗部分，后续需要实现：

1. 实际的下载管理器（使用OkDownload）
2. 下载进度显示
3. 下载列表管理
4. 下载通知
5. 权限处理（Android 10以下需要存储权限）
6. 下载历史记录

## 文件清单

### 新增文件

**Java/Kotlin文件**:
- `download/DownloadDialogInfo.kt`
- `download/DownloadDialogFragment.kt`

**布局文件**:
- `layout/pb_dialog_download.xml`
- `drawable/pb_download_dialog_background.xml`

**图标文件**:
- `drawable/pb_ic_file_image.xml`
- `drawable/pb_ic_file_video.xml`
- `drawable/pb_ic_file_audio.xml`
- `drawable/pb_ic_file_pdf.xml`
- `drawable/pb_ic_file_word.xml`
- `drawable/pb_ic_file_excel.xml`
- `drawable/pb_ic_file_ppt.xml`
- `drawable/pb_ic_file_archive.xml`
- `drawable/pb_ic_file_apk.xml`
- `drawable/pb_ic_file_text.xml`
- `drawable/pb_ic_file_unknown.xml`

### 修改文件

- `utils/FileUtil.kt`: 添加了 `getFileIconByExtension()` 和 `formatFileSize()` 方法
- `webview/PbWebView.kt`: 添加了 `OnDownloadListener` 接口和回调
- `webview/BaseWebViewFragment.kt`: 添加了下载监听和弹窗显示逻辑
