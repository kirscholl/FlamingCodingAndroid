package com.example.pffbrowser.ext

import android.os.Bundle
import android.os.SystemClock
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.example.pffbrowser.R

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

// NavController不使用回退栈
fun NavController.safeNavigateWithAnim(
    @IdRes currentId: Int,
    @IdRes resId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    if (currentDestination?.id != currentId) return
    // 构建 NavOptions：清除回退栈并添加动画
    val options = navOptions ?: NavOptions.Builder()
        // 弹出到当前页面并移除（关键：不加入回退栈）
//        .setPopUpTo(currentId, true)
        // 进入动画：从右往左移入
        .setEnterAnim(R.anim.pb_anim_fragment_slide_in_right)
        // 退出动画：从左往右移出
        .setExitAnim(R.anim.pb_anim_fragment_slide_out_left)
        // 返回时的进入动画（从上一个页面恢复时的动画）
        .setPopEnterAnim(R.anim.pb_anim_fragment_slide_in_left)
        // 返回时的退出动画
        .setPopExitAnim(R.anim.pb_anim_fragment_slide_out_right)
        .build()
    safeNavigateThrottle(currentId, resId, args, options, navigatorExtras)
}

object NavigationThrottleHelper {
    var lastNavigateTime: Long = 0L
}