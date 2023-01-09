package com.kyungeun.customrecyclerview.util

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * Fixing RecyclerView nested scrolling in opposite direction
 *
 * An OnItemTouchListener to be registered on the RecyclerView,
 * where the updated gesture detection code (based on the original code of RecyclerView)
 * will be implemented by overriding onInterceptTouchEvent().
 *
 * An OnScrollListener to be registered on the RecyclerView as well,
 * whose role is to detect transitions from SCROLL_STATE_IDLE to SCROLL_STATE_DRAGGING
 * and revert back to SCROLL_STATE_IDLE if required
 * (depending on the data provided by the updated gesture detection code).
 * After reverting back, RecyclerView.onInterceptTouchEvent() will now return false
 * and the gesture will not be captured.
 *
 * Examples of use : recyclerview.enforceSingleScrollDirection()
 */
fun RecyclerView.enforceSingleScrollDirection() {
    val enforcer = SingleScrollDirectionEnforcer()
    addOnItemTouchListener(enforcer)
    addOnScrollListener(enforcer)
}

private class SingleScrollDirectionEnforcer : RecyclerView.OnScrollListener(), RecyclerView.OnItemTouchListener {

    private var scrollState = RecyclerView.SCROLL_STATE_IDLE
    private var scrollPointerId = -1
    private var initialTouchX = 0
    private var initialTouchY = 0
    private var dx = 0
    private var dy = 0

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                scrollPointerId = e.getPointerId(0)
                initialTouchX = (e.x + 0.5f).toInt()
                initialTouchY = (e.y + 0.5f).toInt()
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                val actionIndex = e.actionIndex
                scrollPointerId = e.getPointerId(actionIndex)
                initialTouchX = (e.getX(actionIndex) + 0.5f).toInt()
                initialTouchY = (e.getY(actionIndex) + 0.5f).toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val index = e.findPointerIndex(scrollPointerId)
                if (index >= 0 && scrollState != RecyclerView.SCROLL_STATE_DRAGGING) {
                    val x = (e.getX(index) + 0.5f).toInt()
                    val y = (e.getY(index) + 0.5f).toInt()
                    dx = x - initialTouchX
                    dy = y - initialTouchY
                }
            }
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        val oldState = scrollState
        scrollState = newState
        if (oldState == RecyclerView.SCROLL_STATE_IDLE && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            recyclerView.layoutManager?.let { layoutManager ->
                val canScrollHorizontally = layoutManager.canScrollHorizontally()
                val canScrollVertically = layoutManager.canScrollVertically()
                if (canScrollHorizontally != canScrollVertically) {
                    if ((canScrollHorizontally && abs(dy) > abs(dx))
                        || (canScrollVertically && abs(dx) > abs(dy))) {
                        recyclerView.stopScroll()
                    }
                }
            }
        }
    }
}