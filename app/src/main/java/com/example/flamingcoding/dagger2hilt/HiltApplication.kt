package com.example.flamingcoding.dagger2hilt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * @HiltApplication 触发 Hilt 代码生成，包括一个父级 Hilt 组件。
 * 所有使用 Hilt 的应用都必须有一个带有此注解的 Application 类。
 */
@HiltAndroidApp
open class HiltApplication : Application() {


}