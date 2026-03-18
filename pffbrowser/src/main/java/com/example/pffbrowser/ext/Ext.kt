package com.example.pffbrowser.ext

import android.os.Bundle
import android.os.SystemClock
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

// 扩展函数：安全导航
fun NavController.safeNavigate(
    @IdRes currentId: Int,
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    // 只有当前 destination 仍然是预期的页面时才导航
    if (currentDestination?.id == currentId) {
        navigate(resId, args, navOptions, navigatorExtras)
    }
}

// NavController 扩展：全局防抖 + destination 校验
fun NavController.safeNavigateThrottle(
    @IdRes currentId: Int,
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    val currentTime = SystemClock.elapsedRealtime()
    // 使用 NavController 的 tag 存储上次导航时间
    val lastNavTime = (this as? Any)?.let {
        NavigationThrottleHelper.lastNavigateTime
    } ?: 0L

    if (currentTime - lastNavTime > 300L
        && currentDestination?.id == currentId
    ) {
        NavigationThrottleHelper.lastNavigateTime = currentTime
        navigate(resId, args, navOptions, navigatorExtras)
    }
}

object NavigationThrottleHelper {
    var lastNavigateTime: Long = 0L
}