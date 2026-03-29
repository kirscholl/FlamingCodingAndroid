# 下载模块数据库设计文档

## 第一阶段完成 ✅

已完成下载模块的数据库层实现，包括实体类、DAO、数据库配置和依赖注入。

---

## 文件结构

```
download/
├── DownloadStatus.kt                    # 下载状态枚举
├── database/
│   ├── DownloadTask.kt                  # 下载任务实体类
│   ├── DownloadTaskDao.kt               # 数据访问对象
│   ├── DownloadDatabase.kt              # Room数据库
│   ├── Converters.kt                    # 类型转换器
│   └── DownloadTaskBuilder.kt           # 任务构建器（辅助工具）
└── di/
    └── DatabaseModule.kt                # Hilt依赖注入模块
```

---

## 数据库表结构

### download_tasks 表

| 字段名 | 类型 | 说明 | 索引 |
|-------|------|------|------|
| id | Long | 主键，自增 | PRIMARY KEY |
| url | String | 下载URL | - |
| fileName | String | 文件名 | - |
| filePath | String | 文件保存路径 | - |
| mimeType | String? | MIME类型 | - |
| totalBytes | Long | 文件总大小（字节） | - |
| downloadedBytes | Long | 已下载大小（字节） | - |
| progress | Int | 下载进度（0-100） | - |
| status | DownloadStatus | 下载状态 | INDEX |
| errorMsg | String? | 错误信息 | - |
| createTime | Long | 创建时间（毫秒） | INDEX |
| updateTime | Long | 更新时间（毫秒） | - |
| completeTime | Long? | 完成时间（毫秒） | - |
| okDownloadId | Int? | OkDownload任务ID | - |

**索引说明**：
- `status` 索引：用于快速查询特定状态的任务（如正在下载、等待中）
- `createTime` 索引：用于按时间排序查询

---

## 下载状态枚举

```kotlin
enum class DownloadStatus {
    PENDING,        // 等待中
    DOWNLOADING,    // 下载中
    PAUSED,         // 已暂停
    COMPLETED,      // 已完成
    FAILED,         // 失败
    CANCELED        // 已取消
}
```

**状态转换规则**：
```
PENDING → DOWNLOADING → COMPLETED
           ↓
        PAUSED → DOWNLOADING
           ↓
        FAILED → DOWNLOADING (重试)
           ↓
        CANCELED (终止)
```

---

## DAO接口功能

### 插入操作
- `insert(task)`: 插入单个任务
- `insertAll(tasks)`: 批量插入任务

### 更新操作（性能优化）
- `update(task)`: 更新整个任务对象
- `updateProgress(id, bytes, progress, time)`: **只更新进度字段**（性能优化）
- `updateStatus(id, status, time)`: **只更新状态字段**（性能优化）
- `updateStatusWithError(id, status, errorMsg, time)`: 更新状态和错误信息
- `markAsCompleted(id, completeTime)`: 标记为完成
- `updateOkDownloadId(id, okDownloadId)`: 更新OkDownload任务ID

### 查询操作
- `getAllTasksFlow()`: 查询所有任务（**Flow自动监听变化**）
- `getAllTasks()`: 查询所有任务（一次性）
- `getTaskById(id)`: 根据ID查询
- `getDownloadingTasks()`: 查询正在下载的任务
- `getPendingTasks()`: 查询等待中的任务
- `getCompletedTasks()`: 查询已完成的任务
- `getFailedTasks()`: 查询失败的任务
- `getTasksByStatus(status)`: 根据状态查询
- `getDownloadingCount()`: 查询正在下载的任务数量
- `getPendingCount()`: 查询等待中的任务数量
- `getTaskByUrl(url)`: 根据URL查询（检查重复）

### 删除操作
- `delete(task)`: 删除单个任务
- `deleteById(id)`: 根据ID删除
- `deleteAll(tasks)`: 批量删除
- `deleteAllCompleted()`: 删除所有已完成的任务
- `deleteAllFailed()`: 删除所有失败的任务
- `deleteAllTasks()`: 清空所有任务

### 统计操作
- `getTaskCount()`: 获取任务总数
- `getCompletedCount()`: 获取已完成任务数量
- `getTotalDownloadedSize()`: 获取总下载大小

---

## 使用示例

### 1. 注入DAO

```kotlin
@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val downloadTaskDao: DownloadTaskDao
) : BaseViewModel() {
    // ...
}
```

### 2. 创建下载任务

```kotlin
// 方式1：使用构造函数
val task = DownloadTask(
    url = "https://example.com/file.zip",
    fileName = "file.zip",
    filePath = "/storage/emulated/0/Download/file.zip",
    mimeType = "application/zip",
    totalBytes = 1024000,
    status = DownloadStatus.PENDING,
    createTime = System.currentTimeMillis(),
    updateTime = System.currentTimeMillis()
)

// 方式2：使用Builder（推荐）
val task = DownloadTaskBuilder.createPendingTask(
    url = "https://example.com/file.zip",
    fileName = "file.zip",
    filePath = "/storage/emulated/0/Download/file.zip",
    totalBytes = 1024000,
    mimeType = "application/zip"
)

// 插入数据库
val taskId = downloadTaskDao.insert(task)
```

### 3. 监听任务列表变化（推荐）

```kotlin
// 在ViewModel中
val downloadTasks: LiveData<List<DownloadTask>> =
    downloadTaskDao.getAllTasksFlow().asLiveData()

// 在Fragment中观察
viewModel.downloadTasks.observe(viewLifecycleOwner) { tasks ->
    // 自动更新UI
    adapter.submitList(tasks)
}
```

### 4. 更新下载进度（性能优化）

```kotlin
// 不推荐：更新整个对象（会更新所有字段）
val task = downloadTaskDao.getTaskById(taskId)
val updatedTask = task.copy(
    downloadedBytes = 512000,
    progress = 50,
    updateTime = System.currentTimeMillis()
)
downloadTaskDao.update(updatedTask)

// 推荐：只更新必要字段（性能更好）
downloadTaskDao.updateProgress(
    id = taskId,
    bytes = 512000,
    progress = 50,
    time = System.currentTimeMillis()
)
```

### 5. 查询特定状态的任务

```kotlin
// 查询正在下载的任务
val downloadingTasks = downloadTaskDao.getDownloadingTasks()

// 查询等待中的任务
val pendingTasks = downloadTaskDao.getPendingTasks()

// 查询正在下载的任务数量
val count = downloadTaskDao.getDownloadingCount()
```

### 6. 删除任务

```kotlin
// 根据ID删除
downloadTaskDao.deleteById(taskId)

// 删除所有已完成的任务
downloadTaskDao.deleteAllCompleted()
```

---

## 性能优化策略

### 1. 索引优化
- `status` 字段添加索引：快速查询特定状态的任务
- `createTime` 字段添加索引：快速按时间排序

### 2. 更新优化
- 使用 `@Query` 直接更新字段，避免整行更新
- 进度更新只更新3个字段：`downloadedBytes`、`progress`、`updateTime`
- 状态更新只更新2个字段：`status`、`updateTime`

### 3. 查询优化
- 使用 `Flow` 监听数据变化，避免轮询
- 提供专门的计数查询，避免查询整个列表

### 4. 批量操作
- 提供批量插入、批量更新、批量删除接口
- 使用事务保证数据一致性

---

## 数据库版本管理

当前版本：**v1**

### 版本历史
- **v1** (当前): 初始版本，包含 `download_tasks` 表

### 未来版本规划
- **v2**: 可能添加下载分片信息表（支持断点续传详细信息）
- **v3**: 可能添加下载历史统计表

### Migration策略
开发阶段使用 `fallbackToDestructiveMigration()`，生产环境需要提供 `Migration` 对象。

---

## 测试建议

### 单元测试
```kotlin
@RunWith(AndroidJUnit4::class)
class DownloadTaskDaoTest {

    private lateinit var database: DownloadDatabase
    private lateinit var dao: DownloadTaskDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, DownloadDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.downloadTaskDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndQuery() = runBlocking {
        val task = DownloadTaskBuilder.createPendingTask(
            url = "https://example.com/test.zip",
            fileName = "test.zip",
            filePath = "/test/test.zip"
        )

        val id = dao.insert(task)
        val queried = dao.getTaskById(id)

        assertNotNull(queried)
        assertEquals(task.url, queried?.url)
    }

    @Test
    fun updateProgress() = runBlocking {
        val task = DownloadTaskBuilder.createPendingTask(
            url = "https://example.com/test.zip",
            fileName = "test.zip",
            filePath = "/test/test.zip"
        )

        val id = dao.insert(task)
        dao.updateProgress(id, 512000, 50, System.currentTimeMillis())

        val updated = dao.getTaskById(id)
        assertEquals(512000, updated?.downloadedBytes)
        assertEquals(50, updated?.progress)
    }
}
```

---

## 下一步工作

第一阶段（数据库）已完成 ✅

**第二阶段：下载核心模块**
- [ ] 创建 DownloadManager（队列管理、并发控制）
- [ ] 集成 OkDownload（下载监听、进度回调）
- [ ] 创建 ProgressThrottler（数据库更新节流）
- [ ] 创建 NotificationThrottler（通知更新节流）
- [ ] 创建 DownloadNotificationManager（通知管理）
- [ ] 创建 DownloadForegroundService（前台服务）
- [ ] 创建 DownloadRepository（统一数据源）

---

## 注意事项

1. **线程安全**：所有DAO方法都是 `suspend` 函数，必须在协程中调用
2. **Flow监听**：使用 `getAllTasksFlow()` 可以自动监听数据变化，无需手动刷新
3. **性能优化**：频繁更新进度时，使用 `updateProgress()` 而非 `update()`
4. **索引使用**：查询时尽量使用已建立索引的字段（status、createTime）
5. **数据迁移**：生产环境必须提供 Migration，避免数据丢失

---

## 依赖项

确保 `build.gradle` 中已添加以下依赖：

```gradle
dependencies {
    // Room
    implementation "androidx.room:room-runtime:2.6.1"
    implementation "androidx.room:room-ktx:2.6.1"
    ksp "androidx.room:room-compiler:2.6.1"

    // Hilt
    implementation "com.google.dagger:hilt-android:2.48"
    ksp "com.google.dagger:hilt-compiler:2.48"

    // Coroutines
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
}
```
