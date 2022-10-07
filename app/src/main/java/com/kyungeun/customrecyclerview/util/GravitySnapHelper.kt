package com.kyungeun.customrecyclerview.util

import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.annotation.Px
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import java.util.*

class GravitySnapHelper @JvmOverloads constructor(
    gravity: Int, enableSnapLastItem: Boolean = false,
    snapListener: SnapListener? = null
) :
    LinearSnapHelper() {
    val FLING_DISTANCE_DISABLE = -1
    val FLING_SIZE_FRACTION_DISABLE = -1f
    private var gravity = 0
    private var isRtl = false
    private var snapLastItem = false
    private var nextSnapPosition = 0
    private var isScrolling = false
    private var snapToPadding = false
    private var scrollMsPerInch = 100f
    private var maxFlingDistance = FLING_DISTANCE_DISABLE
    private var maxFlingSizeFraction = FLING_SIZE_FRACTION_DISABLE
    private var verticalHelper: OrientationHelper? = null
    private var horizontalHelper: OrientationHelper? = null
    private var listener: SnapListener? = null
    private var recyclerView: RecyclerView? = null
    private val scrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                this@GravitySnapHelper.onScrollStateChanged(newState)
            }
        }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {
        val centerView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        val position = layoutManager.getPosition(centerView)
        var targetPosition = -1
        if (layoutManager.canScrollHorizontally()) {
            if (velocityX < 0) {
                targetPosition = position - 1
            } else {
                targetPosition = position + 1
            }
        }
        if (layoutManager.canScrollVertically()) {
            if (velocityY < 0) {
                targetPosition = position - 1
            } else {
                targetPosition = position + 1
            }
        }
        val firstItem = 0
        val lastItem = layoutManager.itemCount - 1
        targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem))
        return targetPosition
    }

    constructor(gravity: Int, snapListener: SnapListener) : this(gravity, false, snapListener) {}

    @Throws(IllegalStateException::class)
    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        if (this.recyclerView != null) {
            this.recyclerView!!.removeOnScrollListener(scrollListener)
        }
        if (recyclerView != null) {
            recyclerView.onFlingListener = null
            if (gravity == Gravity.START || gravity == Gravity.END) {
                isRtl = (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault())
                        == ViewCompat.LAYOUT_DIRECTION_RTL)
            }
            recyclerView.addOnScrollListener(scrollListener)
            this.recyclerView = recyclerView
        } else {
            this.recyclerView = null
        }
        super.attachToRecyclerView(recyclerView)
    }

    override fun findSnapView(lm: RecyclerView.LayoutManager): View? {
        return findSnapView(lm, true)
    }

    fun findSnapView(lm: RecyclerView.LayoutManager, checkEdgeOfList: Boolean): View? {
        var snapView: View? = null
        when (gravity) {
            Gravity.START -> snapView = findView(
                lm,
                getHorizontalHelper(lm)!!, Gravity.START, checkEdgeOfList
            )
            Gravity.END -> snapView = findView(
                lm,
                getHorizontalHelper(lm)!!, Gravity.END, checkEdgeOfList
            )
            Gravity.TOP -> snapView = findView(
                lm,
                getVerticalHelper(lm)!!, Gravity.START, checkEdgeOfList
            )
            Gravity.BOTTOM -> snapView = findView(
                lm,
                getVerticalHelper(lm)!!, Gravity.END, checkEdgeOfList
            )
            Gravity.CENTER -> if (lm.canScrollHorizontally()) {
                snapView = findView(
                    lm, getHorizontalHelper(lm)!!, Gravity.CENTER,
                    checkEdgeOfList
                )
            } else {
                snapView = findView(
                    lm, getVerticalHelper(lm)!!, Gravity.CENTER,
                    checkEdgeOfList
                )
            }
        }
        if (snapView != null) {
            nextSnapPosition = recyclerView!!.getChildAdapterPosition(snapView)
        } else {
            nextSnapPosition = RecyclerView.NO_POSITION
        }
        return snapView
    }

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray {
        if (gravity == Gravity.CENTER) {
            return super.calculateDistanceToFinalSnap(layoutManager, targetView)!!
        }
        val out = IntArray(2)
        if (layoutManager !is LinearLayoutManager) {
            return out
        }
        val lm = layoutManager
        if (lm.canScrollHorizontally()) {
            if (isRtl && gravity == Gravity.END || !isRtl && gravity == Gravity.START) {
                out[0] = getDistanceToStart(targetView, getHorizontalHelper(lm)!!)
            } else {
                out[0] = getDistanceToEnd(targetView, getHorizontalHelper(lm)!!)
            }
        } else if (lm.canScrollVertically()) {
            if (gravity == Gravity.TOP) {
                out[1] = getDistanceToStart(targetView, getVerticalHelper(lm)!!)
            } else {
                out[1] = getDistanceToEnd(targetView, getVerticalHelper(lm)!!)
            }
        }
        return out
    }

    override fun calculateScrollDistance(velocityX: Int, velocityY: Int): IntArray {
        if (recyclerView == null || verticalHelper == null && horizontalHelper == null
            || (maxFlingDistance == FLING_DISTANCE_DISABLE
                    && maxFlingSizeFraction == FLING_SIZE_FRACTION_DISABLE)
        ) {
            return super.calculateScrollDistance(velocityX, velocityY)
        }
        val out = IntArray(2)
        val scroller = Scroller(
            recyclerView!!.context,
            DecelerateInterpolator()
        )
        val maxDistance = getFlingDistance()
        scroller.fling(
            0, 0, velocityX, velocityY,
            -maxDistance, maxDistance,
            -maxDistance, maxDistance
        )
        out[0] = scroller.finalX
        out[1] = scroller.finalY
        return out
    }

    public override fun createScroller(layoutManager: RecyclerView.LayoutManager): SmoothScroller? {
        return if ((!(layoutManager is ScrollVectorProvider)
                    || recyclerView == null)
        ) {
            null
        } else object : LinearSmoothScroller(recyclerView!!.context) {
            override fun onTargetFound(
                targetView: View,
                state: RecyclerView.State,
                action: Action
            ) {
                if (recyclerView == null || recyclerView!!.layoutManager == null) {
                    // The associated RecyclerView has been removed so there is no action to take.
                    return
                }
                val snapDistances = calculateDistanceToFinalSnap(
                    recyclerView!!.layoutManager!!,
                    targetView
                )
                val dx = snapDistances.get(0)
                val dy = snapDistances.get(1)
                val time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)))
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator)
                }
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return scrollMsPerInch / displayMetrics.densityDpi
            }
        }
    }

    fun setSnapListener(listener: SnapListener?) {
        this.listener = listener
    }

    /**
     * Changes the gravity of this [GravitySnapHelper]
     * and dispatches a smooth scroll for the new snap position.
     *
     * @param newGravity one of the following: [Gravity.START], [Gravity.TOP],
     * [Gravity.END], [Gravity.BOTTOM], [Gravity.CENTER]
     * @param smooth     true if we should smooth scroll to new edge, false otherwise
     */
    fun setGravity(newGravity: Int, smooth: Boolean) {
        if (gravity != newGravity) {
            gravity = newGravity
            updateSnap(smooth, false)
        }
    }

    /**
     * Updates the current view to be snapped
     *
     * @param smooth          true if we should smooth scroll, false otherwise
     * @param checkEdgeOfList true if we should check if we're at an edge of the list
     * and snap according to [GravitySnapHelper.getSnapLastItem],
     * or false to force snapping to the nearest view
     */
    fun updateSnap(smooth: Boolean, checkEdgeOfList: Boolean) {
        if (recyclerView == null || recyclerView!!.layoutManager == null) {
            return
        }
        val lm = recyclerView!!.layoutManager
        val snapView = findSnapView(lm!!, checkEdgeOfList)
        if (snapView != null) {
            val out = calculateDistanceToFinalSnap(lm, snapView)
            if (smooth) {
                recyclerView!!.smoothScrollBy(out[0], out[1])
            } else {
                recyclerView!!.scrollBy(out[0], out[1])
            }
        }
    }

    /**
     * This method will only work if there's a ViewHolder for the given position.
     *
     * @return true if scroll was successful, false otherwise
     */
    fun scrollToPosition(position: Int): Boolean {
        return if (position == RecyclerView.NO_POSITION) {
            false
        } else scrollTo(position, false)
    }

    fun smoothScrollToPosition(position: Int): Boolean {
        return if (position == RecyclerView.NO_POSITION) {
            false
        } else scrollTo(position, true)
    }

    /**
     * Get the current gravity being applied
     *
     * @return one of the following: [Gravity.START], [Gravity.TOP], [Gravity.END],
     * [Gravity.BOTTOM], [Gravity.CENTER]
     */
    fun getGravity(): Int {
        return gravity
    }

    fun setGravity(newGravity: Int) {
        setGravity(newGravity, true)
    }

    /**
     * @return true if this SnapHelper should snap to the last item
     */
    fun getSnapLastItem(): Boolean {
        return snapLastItem
    }

    /**
     * Enable snapping of the last item that's snappable.
     * The default value is false, because you can't see the last item completely
     * if this is enabled.
     *
     * @param snap true if you want to enable snapping of the last snappable item
     */
    fun setSnapLastItem(snap: Boolean) {
        snapLastItem = snap
    }

    fun getMaxFlingDistance(): Int {
        return maxFlingDistance
    }

    fun setMaxFlingDistance(@Px distance: Int) {
        maxFlingDistance = distance
        maxFlingSizeFraction = FLING_SIZE_FRACTION_DISABLE
    }

    fun getMaxFlingSizeFraction(): Float {
        return maxFlingSizeFraction
    }

    fun setMaxFlingSizeFraction(fraction: Float) {
        maxFlingDistance = FLING_DISTANCE_DISABLE
        maxFlingSizeFraction = fraction
    }

    fun getScrollMsPerInch(): Float {
        return scrollMsPerInch
    }

    fun setScrollMsPerInch(ms: Float) {
        scrollMsPerInch = ms
    }

    /**
     * @return true if this SnapHelper should snap to the padding. Defaults to false.
     */
    fun getSnapToPadding(): Boolean {
        return snapToPadding
    }

    /**
     * If true, GravitySnapHelper will snap to the gravity edge
     * plus any amount of padding that was set in the RecyclerView.
     *
     *
     * The default value is false.
     *
     * @param snapToPadding true if you want to snap to the padding
     */
    fun setSnapToPadding(snapToPadding: Boolean) {
        this.snapToPadding = snapToPadding
    }

    /**
     * @return the position of the current view that's snapped
     * or [RecyclerView.NO_POSITION] in case there's none.
     */
    fun getCurrentSnappedPosition(): Int {
        if (recyclerView != null && recyclerView!!.layoutManager != null) {
            val snappedView = findSnapView(recyclerView!!.layoutManager!!)
            if (snappedView != null) {
                return recyclerView!!.getChildAdapterPosition(snappedView)
            }
        }
        return RecyclerView.NO_POSITION
    }

    private fun getFlingDistance(): Int {
        if (maxFlingSizeFraction != FLING_SIZE_FRACTION_DISABLE) {
            if (verticalHelper != null) {
                return (recyclerView!!.height * maxFlingSizeFraction).toInt()
            } else return if (horizontalHelper != null) {
                (recyclerView!!.width * maxFlingSizeFraction).toInt()
            } else {
                Int.MAX_VALUE
            }
        } else return if (maxFlingDistance != FLING_DISTANCE_DISABLE) {
            maxFlingDistance
        } else {
            Int.MAX_VALUE
        }
    }

    /**
     * @return true if the scroll will snap to a view, false otherwise
     */
    private fun scrollTo(position: Int, smooth: Boolean): Boolean {
        if (recyclerView!!.layoutManager != null) {
            if (smooth) {
                val smoothScroller = createScroller(recyclerView!!.layoutManager!!)
                if (smoothScroller != null) {
                    smoothScroller.targetPosition = position
                    recyclerView!!.layoutManager!!.startSmoothScroll(smoothScroller)
                    return true
                }
            } else {
                val viewHolder = recyclerView!!.findViewHolderForAdapterPosition(position)
                if (viewHolder != null) {
                    val distances = calculateDistanceToFinalSnap(
                        recyclerView!!.layoutManager!!,
                        viewHolder.itemView
                    )
                    recyclerView!!.scrollBy(distances[0], distances[1])
                    return true
                }
            }
        }
        return false
    }

    private fun getDistanceToStart(targetView: View, helper: OrientationHelper): Int {
        val distance: Int
        // If we don't care about padding, just snap to the start of the view
        if (!snapToPadding) {
            val childStart = helper.getDecoratedStart(targetView)
            if (childStart >= helper.startAfterPadding / 2) {
                distance = childStart - helper.startAfterPadding
            } else {
                distance = childStart
            }
        } else {
            distance = helper.getDecoratedStart(targetView) - helper.startAfterPadding
        }
        return distance
    }

    private fun getDistanceToEnd(targetView: View, helper: OrientationHelper): Int {
        val distance: Int
        if (!snapToPadding) {
            val childEnd = helper.getDecoratedEnd(targetView)
            if (childEnd >= helper.end - (helper.end - helper.endAfterPadding) / 2) {
                distance = helper.getDecoratedEnd(targetView) - helper.end
            } else {
                distance = childEnd - helper.endAfterPadding
            }
        } else {
            distance = helper.getDecoratedEnd(targetView) - helper.endAfterPadding
        }
        return distance
    }

    /**
     * Returns the first view that we should snap to.
     *
     * @param layoutManager the RecyclerView's LayoutManager
     * @param helper        orientation helper to calculate view sizes
     * @param gravity       gravity to find the closest view
     * @return the first view in the LayoutManager to snap to, or null if we shouldn't snap to any
     */
    private fun findView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper,
        gravity: Int,
        checkEdgeOfList: Boolean
    ): View? {
        if (layoutManager.childCount == 0 || layoutManager !is LinearLayoutManager) {
            return null
        }
        val lm = layoutManager

        // If we're at an edge of the list, we shouldn't snap
        // to avoid having the last item not completely visible.
        if (checkEdgeOfList && isAtEdgeOfList(lm) && !snapLastItem) {
            return null
        }
        var edgeView: View? = null
        var distanceToTarget = Int.MAX_VALUE
        val center: Int
        if (layoutManager.getClipToPadding()) {
            center = helper.startAfterPadding + helper.totalSpace / 2
        } else {
            center = helper.end / 2
        }
        val snapToStart = (gravity == Gravity.START && !isRtl
                || gravity == Gravity.END && isRtl)
        val snapToEnd = (gravity == Gravity.START && isRtl
                || gravity == Gravity.END && !isRtl)
        for (i in 0 until lm.childCount) {
            val currentView = lm.getChildAt(i)
            var currentViewDistance: Int
            if (snapToStart) {
                if (!snapToPadding) {
                    currentViewDistance = Math.abs(helper.getDecoratedStart(currentView))
                } else {
                    currentViewDistance = Math.abs(
                        helper.startAfterPadding
                                - helper.getDecoratedStart(currentView)
                    )
                }
            } else if (snapToEnd) {
                if (!snapToPadding) {
                    currentViewDistance = Math.abs(
                        (helper.getDecoratedEnd(currentView)
                                - helper.end)
                    )
                } else {
                    currentViewDistance = Math.abs(
                        (helper.endAfterPadding
                                - helper.getDecoratedEnd(currentView))
                    )
                }
            } else {
                currentViewDistance = Math.abs(
                    helper.getDecoratedStart(currentView)
                            + (helper.getDecoratedMeasurement(currentView) / 2) - center
                )
            }
            if (currentViewDistance < distanceToTarget) {
                distanceToTarget = currentViewDistance
                edgeView = currentView
            }
        }
        return edgeView
    }

    private fun isAtEdgeOfList(lm: LinearLayoutManager): Boolean {
        if (((!lm.reverseLayout && gravity == Gravity.START)
                    || (lm.reverseLayout && gravity == Gravity.END)
                    || (!lm.reverseLayout && gravity == Gravity.TOP)
                    || (lm.reverseLayout && gravity == Gravity.BOTTOM))
        ) {
            return lm.findLastCompletelyVisibleItemPosition() == lm.itemCount - 1
        } else return if (gravity == Gravity.CENTER) {
            (lm.findFirstCompletelyVisibleItemPosition() == 0
                    || lm.findLastCompletelyVisibleItemPosition() == lm.itemCount - 1)
        } else {
            lm.findFirstCompletelyVisibleItemPosition() == 0
        }
    }

    private fun onScrollStateChanged(newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE && listener != null) {
            if (isScrolling) {
                if (nextSnapPosition != RecyclerView.NO_POSITION) {
                    listener!!.onSnap(nextSnapPosition)
                } else {
                    dispatchSnapChangeWhenPositionIsUnknown()
                }
            }
        }
        isScrolling = newState != RecyclerView.SCROLL_STATE_IDLE
    }

    private fun dispatchSnapChangeWhenPositionIsUnknown() {
        val layoutManager = recyclerView!!.layoutManager ?: return
        val snapView = findSnapView(layoutManager, false) ?: return
        val snapPosition = recyclerView!!.getChildAdapterPosition(snapView)
        if (snapPosition != RecyclerView.NO_POSITION) {
            listener!!.onSnap(snapPosition)
        }
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper? {
        if (verticalHelper == null || verticalHelper!!.layoutManager !== layoutManager) {
            verticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return verticalHelper
    }

    private fun getHorizontalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper? {
        if (horizontalHelper == null || horizontalHelper!!.layoutManager !== layoutManager) {
            horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return horizontalHelper
    }

    interface SnapListener {
        /**
         * @param position last position snapped to
         */
        fun onSnap(position: Int)
    }

    companion object {
        val FLING_DISTANCE_DISABLE = -1
        val FLING_SIZE_FRACTION_DISABLE = -1f
    }

    init {
        if ((gravity != Gravity.START
                    ) && (gravity != Gravity.END
                    ) && (gravity != Gravity.BOTTOM
                    ) && (gravity != Gravity.TOP
                    ) && (gravity != Gravity.CENTER)
        ) {
            throw IllegalArgumentException(
                "Invalid gravity value. Use START " +
                        "| END | BOTTOM | TOP | CENTER constants"
            )
        }
        snapLastItem = enableSnapLastItem
        this.gravity = gravity
        listener = snapListener
    }
}
