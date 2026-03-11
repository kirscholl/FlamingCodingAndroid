package com.example.flamingcoding.customViewTrials

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class TestCustomView(context: Context?, attrs: AttributeSet) : View(context, attrs) {

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    var lastX: Int = 0
    var lastY: Int = 0

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = x
                lastY = y
            }

            MotionEvent.ACTION_MOVE -> {
                val offsetX = x - lastX
                val offsetY = y - lastY
                layout(left + offsetX, top + offsetY, right + offsetX, bottom + offsetY)
            }

            MotionEvent.ACTION_UP -> {

            }
        }
        return true
    }
}