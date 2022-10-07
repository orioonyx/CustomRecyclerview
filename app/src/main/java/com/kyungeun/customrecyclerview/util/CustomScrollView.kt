package com.kyungeun.customrecyclerview.util

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.OverScroller
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView

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

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {

        if (super.onInterceptTouchEvent(e)) {
            if (e.action == MotionEvent.ACTION_DOWN) {
                if (scrolling) {
                    return false
                }
                return true
            }
        }

        when (e.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                val currentX = e.x
                val currentY = e.y
                val dx: Float = Math.abs(currentX - lastX)
                val dy: Float = Math.abs(currentY - lastY)

                if (dx < 5 || dy < 5) {
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
                return false
            }
            MotionEvent.ACTION_UP -> {
                scrolling = false
                return true
            }
            MotionEvent.ACTION_HOVER_MOVE -> {
                scrolling = true
                return false
            }
            MotionEvent.ACTION_CANCEL -> {
            }
            else -> {
                return true
            }
        }
        return super.onInterceptTouchEvent(e)
    }

}