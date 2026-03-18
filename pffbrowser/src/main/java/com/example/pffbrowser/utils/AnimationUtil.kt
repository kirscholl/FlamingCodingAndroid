package com.example.pffbrowser.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View

object AnimationUtil {

    // 抖动动画（左右摆动） 用于tab编辑
    fun generateTabShakeAnim(view: View): ObjectAnimator {
        val animator = ObjectAnimator.ofFloat(view, "rotation", -2f, 2f)
        animator.duration = 80
        animator.repeatMode = ValueAnimator.REVERSE
        animator.repeatCount = ValueAnimator.INFINITE
        return animator
    }
}