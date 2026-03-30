# JSBridge 使用文档

## 概述

这是一套完整的Android WebView与H5页面通信的JSBridge解决方案，提供了高效、安全、易用的双向通信机制。

## 架构特点

- **模块化设计**：功能按模块组织，易于扩展
- **安全可靠**：域名白名单、参数校验、超时控制
- **高性能**：异步处理、回调管理
- **易用性**：Promise封装、统一错误处理
- **可调试**：完整的日志追踪

## 核心组件

### Native端

- **JSBridgeCore**：核心调度器，负责消息分发和回调管理
- **IJSBridgeModule**：模块接口，所有功能模块需实现此接口
- **SecurityConfig**：安全配置，控制域名白名单
- **功能模块**：
  - UIModule：UI交互（Toast、Dialog、震动等）
  - StorageModule：本地存储
  - DeviceModule：设备信息
  - NetworkModule：网络请求
  - NavigationModule：页面导航

### H5端

- **jsbridge.js**：JavaScript SDK，提供Promise封装的API

## 快速开始

### 1. Native端集成

JSBridge已经集成到`PbWebView`和`BaseWebViewFragment`中，开箱即用。

#### 自定义安全配置

在你的Fragment中重写`getJSBridgeSecurityConfig`方法：

```kotlin
class MyWebViewFragment : BaseWebViewFragment<FragmentMyBinding, MyViewModel>() {

    override val mWebView: PbWebView
        get() = binding.webView

    override fun getJSBridgeSecurityConfig(): SecurityConfig {
        return SecurityConfig(
            allowedDomains = listOf(
                "yourdomain.com",
                "m.yourdomain.com"
            ),
            allowLocalhost = BuildConfig.DEBUG,
            forceHttps = !BuildConfig.DEBUG
        )
    }
}
```

#### 注册自定义模块

在你的Fragment中重写`initJSBridge`方法：

```kotlin
override fun initJSBridge() {
    super.initJSBridge()  // 调用父类方法注册基础模块

    val jsBridge = mWebView.getJSBridge() ?: return

    // 注册自定义模块
    jsBridge.registerModule(MyCustomModule(requireContext()))
}
```

#### 向H5发送事件

```kotlin
val jsBridge = mWebView.getJSBridge()
jsBridge?.sendEventToJs("onNetworkChanged", mapOf(
    "type" to "wifi",
    "isConnected" to true
))
```

### 2. H5端集成

#### 引入SDK

```html
<script src="jsbridge.js"></script>
```

#### 等待JSBridge就绪

```javascript
JSBridge.ready(() => {
    console.log('JSBridge is ready!');
    // 在这里调用Native方法
});
```

#### 调用Native方法

```javascript
// 方式1：使用便捷方法
JSBridge.ui.showToast('Hello', 2000)
    .then(result => {
        console.log('成功', result);
    })
    .catch(error => {
        console.error('失败', error);
    });

// 方式2：使用通用方法
JSBridge.callNative('ui', 'showToast', {
    message: 'Hello',
    duration: 2000
})
    .then(result => console.log('成功', result))
    .catch(error => console.error('失败', error));
```

#### 监听Native事件

```javascript
JSBridge.on('onNetworkChanged', (data) => {
    console.log('网络状态变化', data);
});

// 取消监听
JSBridge.off('onNetworkChanged', callback);
```

## API文档

### UIModule

#### showToast
显示Toast消息

```javascript
JSBridge.ui.showToast(message, duration)
```

参数：
- `message` (String): 消息内容
- `duration` (Number): 显示时长（毫秒），默认2000

#### showDialog
显示对话框

```javascript
JSBridge.ui.showDialog(title, message, buttons)
```

参数：
- `title` (String): 标题
- `message` (String): 消息内容
- `buttons` (Array): 按钮文本数组，默认['确定']

返回：
- `{ index: Number }`: 用户点击的按钮索引

#### vibrate
震动

```javascript
JSBridge.ui.vibrate(duration)
```

参数：
- `duration` (Number): 震动时长（毫秒），默认100

### StorageModule

#### setItem
存储数据

```javascript
JSBridge.storage.setItem(key, value)
```

#### getItem
读取数据

```javascript
JSBridge.storage.getItem(key)
```

返回：
- `{ value: String }`: 存储的值

#### removeItem
删除数据

```javascript
JSBridge.storage.removeItem(key)
```

#### clear
清空所有数据

```javascript
JSBridge.storage.clear()
```

#### getAllKeys
获取所有key

```javascript
JSBridge.storage.getAllKeys()
```

返回：
- `{ keys: Array }`: 所有key的数组

### DeviceModule

#### getDeviceInfo
获取设备信息

```javascript
JSBridge.device.getDeviceInfo()
```

返回：
```javascript
{
    brand: "Samsung",
    model: "SM-G9900",
    manufacturer: "samsung",
    device: "r8q",
    product: "r8qzc"
}
```

#### getAppInfo
获取应用信息

```javascript
JSBridge.device.getAppInfo()
```

返回：
```javascript
{
    packageName: "com.example.app",
    versionName: "1.0.0",
    versionCode: 1
}
```

#### getSystemInfo
获取系统信息

```javascript
JSBridge.device.getSystemInfo()
```

返回：
```javascript
{
    osVersion: "11",
    sdkVersion: 30,
    platform: "Android"
}
```

### NetworkModule

#### request
发起网络请求

```javascript
JSBridge.network.request(url, method, headers, body)
```

参数：
- `url` (String): 请求URL
- `method` (String): 请求方法，默认'GET'
- `headers` (Object): 请求头
- `body` (String): 请求体

返回：
```javascript
{
    statusCode: 200,
    data: "response data"
}
```

#### getNetworkType
获取网络类型

```javascript
JSBridge.network.getNetworkType()
```

返回：
```javascript
{
    type: "wifi" // wifi | cellular | ethernet | none | unknown
}
```

#### isNetworkAvailable
检查网络是否可用

```javascript
JSBridge.network.isNetworkAvailable()
```

返回：
```javascript
{
    isAvailable: true
}
```

### NavigationModule

#### push
加载新页面

```javascript
JSBridge.navigation.push(url)
```

#### pop
返回上一页

```javascript
JSBridge.navigation.pop()
```

#### reload
刷新当前页面

```javascript
JSBridge.navigation.reload()
```

#### openNewWindow
在新窗口打开URL

```javascript
JSBridge.navigation.openNewWindow(url)
```

#### close
关闭当前页面

```javascript
JSBridge.navigation.close()
```

## 错误码

| 错误码 | 说明 |
|--------|------|
| 0 | 成功 |
| 1000 | 参数错误 |
| 1001 | 模块不存在 |
| 1002 | 方法不存在 |
| 1003 | 权限不足 |
| 1004 | 请求超时 |
| 2000 | 网络错误 |
| 3000 | 系统错误 |
| 9999 | 未知错误 |

## 自定义模块

### 1. 创建模块类

```kotlin
class MyCustomModule(private val context: Context) : IJSBridgeModule {

    override fun getModuleName(): String = "custom"

    override fun invoke(method: String, params: JSONObject, callback: JSCallback) {
        when (method) {
            "myMethod" -> myMethod(params, callback)
            else -> {
                callback.onError(JSBridgeError.METHOD_NOT_FOUND, "方法不存在: $method")
            }
        }
    }

    private fun myMethod(params: JSONObject, callback: JSCallback) {
        // 实现你的逻辑
        callback.onSuccess(mapOf("result" to "success"))
    }
}
```

### 2. 注册模块

```kotlin
val jsBridge = mWebView.getJSBridge()
jsBridge?.registerModule(MyCustomModule(requireContext()))
```

### 3. H5端调用

```javascript
JSBridge.callNative('custom', 'myMethod', { param1: 'value1' })
    .then(result => console.log(result))
    .catch(error => console.error(error));
```

## 调试

### 启用Chrome DevTools

在开发环境中，WebView已自动启用Chrome DevTools调试：

1. 在Chrome浏览器中打开 `chrome://inspect`
2. 找到你的设备和WebView
3. 点击"inspect"开始调试

### 查看日志

JSBridge的所有日志都带有"JSBridge"标签：

```bash
adb logcat -s JSBridge
```

## 示例

项目中包含了一个完整的示例页面：`jsbridge_demo.html`

在WebView中加载此页面即可测试所有功能：

```kotlin
mWebView.loadUrl("file:///android_asset/jsbridge_demo.html")
```

## 最佳实践

1. **安全性**：生产环境必须配置域名白名单和强制HTTPS
2. **错误处理**：始终使用try-catch处理可能的错误
3. **超时控制**：长时间操作应该有超时提示
4. **日志记录**：关键操作记录日志便于排查问题
5. **版本兼容**：H5和Native的API版本要保持同步

## 常见问题

### Q: JSBridge未初始化？
A: 确保在WebView加载完成后再调用JSBridge方法，使用`JSBridge.ready()`等待初始化完成。

### Q: 域名不在白名单中？
A: 检查`SecurityConfig`配置，确保目标域名在`allowedDomains`列表中。

### Q: 方法调用没有响应？
A: 检查Native端是否注册了对应的模块，查看logcat日志排查问题。

### Q: 如何传递复杂数据？
A: 使用JSON格式传递，Native端会自动解析为JSONObject。

## 许可证

MIT License
