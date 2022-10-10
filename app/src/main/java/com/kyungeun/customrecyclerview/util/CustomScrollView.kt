package com.kyungeun.customrecyclerview.util

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView
import kotlin.math.abs

class CustomScrollView(context: Context, attrs: AttributeSet?) : NestedScrollView(context, attrs) {

    private var lastX = 0.0f
    private var lastY = 0.0f

    var scrolling = false

    private fun inChild(x: Int, y: Int): Boolean {
        if (childCount > 0) {
            val scrollY = scrollY
            val child = getChildAt(0)
            return !(y < child.top - scrollY || y >= child.bottom - scrollY || x < child.left || x >= child.right)
        }
        return false
    }

    @SuppressLint("Recycle")
    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {


        when (e.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                val currentX = e.x
                val currentY = e.y
                val dx: Float = abs(currentX - lastX)
                val dy: Float = abs(currentY - lastY)
                if (dx < 3 || dy < 3) {
                    return false
                }
                if(scrolling) {
                    return false
                }
                return dy > dx
            }
            MotionEvent.ACTION_DOWN -> {
                lastX = e.x
                lastY = e.y
                if (scrolling) {
                    scrolling = false
                    val newEvent = MotionEvent.obtain(e)
                    newEvent.action = MotionEvent.ACTION_UP
                    return false
                }
                if (!inChild(lastX.toInt(), lastY.toInt())) {
                    return false
                }
            }
            MotionEvent.ACTION_UP -> {
                if(scrolling) {
                    scrolling = false
                }
                return false
            }
            MotionEvent.ACTION_HOVER_MOVE -> {
            }
            MotionEvent.ACTION_CANCEL -> {
            }
            else -> {
            }
        }
        return super.onInterceptTouchEvent(e)
    }

}