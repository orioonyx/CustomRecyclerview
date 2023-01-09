package com.kyungeun.customrecyclerview.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.view.NestedScrollingParent3
import androidx.core.widget.NestedScrollView

/**
 * Custom NestedScrollView for RecyclerView
 */
class CustomNestedScrollView : NestedScrollView, NestedScrollingParent3 {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // The maximum scroll y position
    private var maxScrollY = 0

    // The current y position of the NestedScrollView
    private var currentY = 0

    // The total scroll distance of the NestedScrollView
    private var totalScrollDistance = 0

    // Flag to indicate whether the NestedScrollView is being flinged
    private var isFlinging = false

    // Flag to indicate whether the NestedScrollView is scrolling
    private var isScrolling = false

    // Flag to indicate whether the NestedScrollView should intercept the touch event
    private var shouldIntercept = false

    // The touch down y position
    private var touchDownY = 0

    // The touch down x position
    private var touchDownX = 0

    // The touch slop value, used to determine the minimum distance the user must move the finger to trigger scrolling
    private val touchSlop by lazy { ViewConfiguration.get(context).scaledTouchSlop }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDownY = e.y.toInt()
                touchDownX = e.x.toInt()
                shouldIntercept = false
            }
            MotionEvent.ACTION_MOVE -> {
                // Check if the user has moved the finger past the touch slop
                val hasMoved = Math.abs(touchDownY - e.y) > touchSlop
                if (hasMoved) {
                    // Check if the NestedScrollView is at the top or bottom and the user is scrolling up or down
                    val isScrollingUp = touchDownY > e.y
                    val isScrollingDown = touchDownY < e.y
                    if ((currentY == 0 && isScrollingUp) || (currentY == maxScrollY && isScrollingDown)) {
                        // Set the flag to intercept the touch event
                        shouldIntercept = true
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                shouldIntercept = false
            }
        }

        // If the NestedScrollView should intercept the touch event, return true. Otherwise, return the super value.
        return shouldIntercept || super.onInterceptTouchEvent(e)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isFlinging = false
                isScrolling = false
            }
        }

        // Return the super value
        return super.onTouchEvent(e)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
// Check if the NestedScrollView is being flinged
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        // Check if the NestedScrollView is scrolling
        if (dyConsumed != 0) {
            isScrolling = true
        }
    }
}