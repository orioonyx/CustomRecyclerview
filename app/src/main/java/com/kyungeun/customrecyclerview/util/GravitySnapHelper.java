package com.kyungeun.customrecyclerview.util;


import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class GravitySnapHelper extends LinearSnapHelper {

    public static final int FLING_DISTANCE_DISABLE = -1;
    public static final float FLING_SIZE_FRACTION_DISABLE = -1f;
    private int gravity;
    private boolean isRtl;
    private boolean snapLastItem;
    private int nextSnapPosition;
    private boolean isScrolling = false;
    private boolean snapToPadding = false;
    private float scrollMsPerInch = 100f;
    private int maxFlingDistance = FLING_DISTANCE_DISABLE;
    private float maxFlingSizeFraction = FLING_SIZE_FRACTION_DISABLE;
    private OrientationHelper verticalHelper;
    private OrientationHelper horizontalHelper;
    private SnapListener listener;
    private RecyclerView recyclerView;
    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            GravitySnapHelper.this.onScrollStateChanged(newState);
        }
    };


    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        View centerView = findSnapView(layoutManager);
        if (centerView == null)
            return RecyclerView.NO_POSITION;

        int position = layoutManager.getPosition(centerView);
        int targetPosition = -1;
        if (layoutManager.canScrollHorizontally()) {
            if (velocityX < 0) {
                targetPosition = position - 1;
            } else {
                targetPosition = position + 1;
            }
        }

        if (layoutManager.canScrollVertically()) {
            if (velocityY < 0) {
                targetPosition = position - 1;
            } else {
                targetPosition = position + 1;
            }
        }

        final int firstItem = 0;
        final int lastItem = layoutManager.getItemCount() - 1;
        targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
        return targetPosition;
    }

    public GravitySnapHelper(int gravity) {
        this(gravity, false, null);
    }

    public GravitySnapHelper(int gravity, @NonNull SnapListener snapListener) {
        this(gravity, false, snapListener);
    }

    public GravitySnapHelper(int gravity, boolean enableSnapLastItem) {
        this(gravity, enableSnapLastItem, null);
    }

    public GravitySnapHelper(int gravity, boolean enableSnapLastItem,
                             @Nullable SnapListener snapListener) {
        if (gravity != Gravity.START
                && gravity != Gravity.END
                && gravity != Gravity.BOTTOM
                && gravity != Gravity.TOP
                && gravity != Gravity.CENTER) {
            throw new IllegalArgumentException("Invalid gravity value. Use START " +
                    "| END | BOTTOM | TOP | CENTER constants");
        }
        this.snapLastItem = enableSnapLastItem;
        this.gravity = gravity;
        this.listener = snapListener;
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView)
            throws IllegalStateException {
        if (this.recyclerView != null) {
            this.recyclerView.removeOnScrollListener(scrollListener);
        }
        if (recyclerView != null) {
            recyclerView.setOnFlingListener(null);
            if (gravity == Gravity.START || gravity == Gravity.END) {
                isRtl = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault())
                        == ViewCompat.LAYOUT_DIRECTION_RTL;
            }
            recyclerView.addOnScrollListener(scrollListener);
            this.recyclerView = recyclerView;
        } else {
            this.recyclerView = null;
        }
        super.attachToRecyclerView(recyclerView);
    }

    @Override
    @Nullable
    public View findSnapView(@NonNull RecyclerView.LayoutManager lm) {
        return findSnapView(lm, true);
    }

    @Nullable
    public View findSnapView(@NonNull RecyclerView.LayoutManager lm, boolean checkEdgeOfList) {
        View snapView = null;

        switch (gravity) {
            case Gravity.START:
                snapView = findView(lm, getHorizontalHelper(lm), Gravity.START, checkEdgeOfList);
                break;
            case Gravity.END:
                snapView = findView(lm, getHorizontalHelper(lm), Gravity.END, checkEdgeOfList);
                break;
            case Gravity.TOP:
                snapView = findView(lm, getVerticalHelper(lm), Gravity.START, checkEdgeOfList);
                break;
            case Gravity.BOTTOM:
                snapView = findView(lm, getVerticalHelper(lm), Gravity.END, checkEdgeOfList);
                break;
            case Gravity.CENTER:
                if (lm.canScrollHorizontally()) {
                    snapView = findView(lm, getHorizontalHelper(lm), Gravity.CENTER,
                            checkEdgeOfList);
                } else {
                    snapView = findView(lm, getVerticalHelper(lm), Gravity.CENTER,
                            checkEdgeOfList);
                }
                break;
        }
        if (snapView != null) {
            nextSnapPosition = recyclerView.getChildAdapterPosition(snapView);
        } else {
            nextSnapPosition = RecyclerView.NO_POSITION;
        }
        return snapView;
    }

    @Override
    @NonNull
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                              @NonNull View targetView) {
        if (gravity == Gravity.CENTER) {
            //noinspection ConstantConditions
            return super.calculateDistanceToFinalSnap(layoutManager, targetView);
        }

        int[] out = new int[2];

        if (!(layoutManager instanceof LinearLayoutManager)) {
            return out;
        }

        LinearLayoutManager lm = (LinearLayoutManager) layoutManager;

        if (lm.canScrollHorizontally()) {
            if ((isRtl && gravity == Gravity.END) || (!isRtl && gravity == Gravity.START)) {
                out[0] = getDistanceToStart(targetView, getHorizontalHelper(lm));
            } else {
                out[0] = getDistanceToEnd(targetView, getHorizontalHelper(lm));
            }
        } else if (lm.canScrollVertically()) {
            if (gravity == Gravity.TOP) {
                out[1] = getDistanceToStart(targetView, getVerticalHelper(lm));
            } else {
                out[1] = getDistanceToEnd(targetView, getVerticalHelper(lm));
            }
        }
        return out;
    }

    @Override
    @NonNull
    public int[] calculateScrollDistance(int velocityX, int velocityY) {
        if (recyclerView == null
                || (verticalHelper == null && horizontalHelper == null)
                || (maxFlingDistance == FLING_DISTANCE_DISABLE
                && maxFlingSizeFraction == FLING_SIZE_FRACTION_DISABLE)) {
            return super.calculateScrollDistance(velocityX, velocityY);
        }
        final int[] out = new int[2];
        Scroller scroller = new Scroller(recyclerView.getContext(),
                new DecelerateInterpolator());
        int maxDistance = getFlingDistance();
        scroller.fling(0, 0, velocityX, velocityY,
                -maxDistance, maxDistance,
                -maxDistance, maxDistance);
        out[0] = scroller.getFinalX();
        out[1] = scroller.getFinalY();
        return out;
    }

    @Nullable
    @Override
    public RecyclerView.SmoothScroller createScroller(RecyclerView.LayoutManager layoutManager) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)
                || recyclerView == null) {
            return null;
        }
        return new LinearSmoothScroller(recyclerView.getContext()) {
            @Override
            protected void onTargetFound(View targetView,
                                         RecyclerView.State state,
                                         Action action) {
                if (recyclerView == null || recyclerView.getLayoutManager() == null) {
                    // The associated RecyclerView has been removed so there is no action to take.
                    return;
                }
                int[] snapDistances = calculateDistanceToFinalSnap(recyclerView.getLayoutManager(),
                        targetView);
                final int dx = snapDistances[0];
                final int dy = snapDistances[1];
                final int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator);
                }
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return scrollMsPerInch / displayMetrics.densityDpi;
            }
        };
    }


    public void setSnapListener(@Nullable SnapListener listener) {
        this.listener = listener;
    }

    /**
     * Changes the gravity of this {@link GravitySnapHelper}
     * and dispatches a smooth scroll for the new snap position.
     *
     * @param newGravity one of the following: {@link Gravity#START}, {@link Gravity#TOP},
     *                   {@link Gravity#END}, {@link Gravity#BOTTOM}, {@link Gravity#CENTER}
     * @param smooth     true if we should smooth scroll to new edge, false otherwise
     */
    public void setGravity(int newGravity, Boolean smooth) {
        if (this.gravity != newGravity) {
            this.gravity = newGravity;
            updateSnap(smooth, false);
        }
    }

    /**
     * Updates the current view to be snapped
     *
     * @param smooth          true if we should smooth scroll, false otherwise
     * @param checkEdgeOfList true if we should check if we're at an edge of the list
     *                        and snap according to {@link GravitySnapHelper#getSnapLastItem()},
     *                        or false to force snapping to the nearest view
     */
    public void updateSnap(Boolean smooth, Boolean checkEdgeOfList) {
        if (recyclerView == null || recyclerView.getLayoutManager() == null) {
            return;
        }
        final RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        View snapView = findSnapView(lm, checkEdgeOfList);
        if (snapView != null) {
            int[] out = calculateDistanceToFinalSnap(lm, snapView);
            if (smooth) {
                recyclerView.smoothScrollBy(out[0], out[1]);
            } else {
                recyclerView.scrollBy(out[0], out[1]);
            }
        }
    }

    /**
     * This method will only work if there's a ViewHolder for the given position.
     *
     * @return true if scroll was successful, false otherwise
     */
    public boolean scrollToPosition(int position) {
        if (position == RecyclerView.NO_POSITION) {
            return false;
        }
        return scrollTo(position, false);
    }


    public boolean smoothScrollToPosition(int position) {
        if (position == RecyclerView.NO_POSITION) {
            return false;
        }
        return scrollTo(position, true);
    }

    /**
     * Get the current gravity being applied
     *
     * @return one of the following: {@link Gravity#START}, {@link Gravity#TOP}, {@link Gravity#END},
     * {@link Gravity#BOTTOM}, {@link Gravity#CENTER}
     */
    public int getGravity() {
        return this.gravity;
    }

    public void setGravity(int newGravity) {
        setGravity(newGravity, true);
    }

    /**
     * @return true if this SnapHelper should snap to the last item
     */
    public boolean getSnapLastItem() {
        return snapLastItem;
    }

    /**
     * Enable snapping of the last item that's snappable.
     * The default value is false, because you can't see the last item completely
     * if this is enabled.
     *
     * @param snap true if you want to enable snapping of the last snappable item
     */
    public void setSnapLastItem(boolean snap) {
        snapLastItem = snap;
    }


    public int getMaxFlingDistance() {
        return maxFlingDistance;
    }


    public void setMaxFlingDistance(@Px int distance) {
        maxFlingDistance = distance;
        maxFlingSizeFraction = FLING_SIZE_FRACTION_DISABLE;
    }


    public float getMaxFlingSizeFraction() {
        return maxFlingSizeFraction;
    }


    public void setMaxFlingSizeFraction(float fraction) {
        maxFlingDistance = FLING_DISTANCE_DISABLE;
        maxFlingSizeFraction = fraction;
    }

    public float getScrollMsPerInch() {
        return scrollMsPerInch;
    }


    public void setScrollMsPerInch(float ms) {
        scrollMsPerInch = ms;
    }

    /**
     * @return true if this SnapHelper should snap to the padding. Defaults to false.
     */
    public boolean getSnapToPadding() {
        return snapToPadding;
    }

    /**
     * If true, GravitySnapHelper will snap to the gravity edge
     * plus any amount of padding that was set in the RecyclerView.
     * <p>
     * The default value is false.
     *
     * @param snapToPadding true if you want to snap to the padding
     */
    public void setSnapToPadding(boolean snapToPadding) {
        this.snapToPadding = snapToPadding;
    }

    /**
     * @return the position of the current view that's snapped
     * or {@link RecyclerView#NO_POSITION} in case there's none.
     */
    public int getCurrentSnappedPosition() {
        if (recyclerView != null && recyclerView.getLayoutManager() != null) {
            View snappedView = findSnapView(recyclerView.getLayoutManager());
            if (snappedView != null) {
                return recyclerView.getChildAdapterPosition(snappedView);
            }
        }
        return RecyclerView.NO_POSITION;
    }

    private int getFlingDistance() {
        if (maxFlingSizeFraction != FLING_SIZE_FRACTION_DISABLE) {
            if (verticalHelper != null) {
                return (int) (recyclerView.getHeight() * maxFlingSizeFraction);
            } else if (horizontalHelper != null) {
                return (int) (recyclerView.getWidth() * maxFlingSizeFraction);
            } else {
                return Integer.MAX_VALUE;
            }
        } else if (maxFlingDistance != FLING_DISTANCE_DISABLE) {
            return maxFlingDistance;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * @return true if the scroll will snap to a view, false otherwise
     */
    private boolean scrollTo(int position, boolean smooth) {
        if (recyclerView.getLayoutManager() != null) {
            if (smooth) {
                RecyclerView.SmoothScroller smoothScroller
                        = createScroller(recyclerView.getLayoutManager());
                if (smoothScroller != null) {
                    smoothScroller.setTargetPosition(position);
                    recyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
                    return true;
                }
            } else {
                RecyclerView.ViewHolder viewHolder
                        = recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder != null) {
                    int[] distances = calculateDistanceToFinalSnap(recyclerView.getLayoutManager(),
                            viewHolder.itemView);
                    recyclerView.scrollBy(distances[0], distances[1]);
                    return true;
                }
            }
        }
        return false;
    }

    private int getDistanceToStart(View targetView, @NonNull OrientationHelper helper) {
        int distance;
        // If we don't care about padding, just snap to the start of the view
        if (!snapToPadding) {
            int childStart = helper.getDecoratedStart(targetView);
            if (childStart >= helper.getStartAfterPadding() / 2) {
                distance = childStart - helper.getStartAfterPadding();
            } else {
                distance = childStart;
            }
        } else {
            distance = helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
        }
        return distance;
    }

    private int getDistanceToEnd(View targetView, @NonNull OrientationHelper helper) {
        int distance;

        if (!snapToPadding) {
            int childEnd = helper.getDecoratedEnd(targetView);
            if (childEnd >= helper.getEnd() - (helper.getEnd() - helper.getEndAfterPadding()) / 2) {
                distance = helper.getDecoratedEnd(targetView) - helper.getEnd();
            } else {
                distance = childEnd - helper.getEndAfterPadding();
            }
        } else {
            distance = helper.getDecoratedEnd(targetView) - helper.getEndAfterPadding();
        }

        return distance;
    }

    /**
     * Returns the first view that we should snap to.
     *
     * @param layoutManager the RecyclerView's LayoutManager
     * @param helper        orientation helper to calculate view sizes
     * @param gravity       gravity to find the closest view
     * @return the first view in the LayoutManager to snap to, or null if we shouldn't snap to any
     */
    @Nullable
    private View findView(@NonNull RecyclerView.LayoutManager layoutManager,
                          @NonNull OrientationHelper helper,
                          int gravity,
                          boolean checkEdgeOfList) {

        if (layoutManager.getChildCount() == 0 || !(layoutManager instanceof LinearLayoutManager)) {
            return null;
        }

        final LinearLayoutManager lm = (LinearLayoutManager) layoutManager;

        // If we're at an edge of the list, we shouldn't snap
        // to avoid having the last item not completely visible.
        if (checkEdgeOfList && (isAtEdgeOfList(lm) && !snapLastItem)) {
            return null;
        }

        View edgeView = null;
        int distanceToTarget = Integer.MAX_VALUE;
        final int center;
        if (layoutManager.getClipToPadding()) {
            center = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
        } else {
            center = helper.getEnd() / 2;
        }

        final boolean snapToStart = (gravity == Gravity.START && !isRtl)
                || (gravity == Gravity.END && isRtl);

        final boolean snapToEnd = (gravity == Gravity.START && isRtl)
                || (gravity == Gravity.END && !isRtl);

        for (int i = 0; i < lm.getChildCount(); i++) {
            View currentView = lm.getChildAt(i);
            int currentViewDistance;
            if (snapToStart) {
                if (!snapToPadding) {
                    currentViewDistance = Math.abs(helper.getDecoratedStart(currentView));
                } else {
                    currentViewDistance = Math.abs(helper.getStartAfterPadding()
                            - helper.getDecoratedStart(currentView));
                }
            } else if (snapToEnd) {
                if (!snapToPadding) {
                    currentViewDistance = Math.abs(helper.getDecoratedEnd(currentView)
                            - helper.getEnd());
                } else {
                    currentViewDistance = Math.abs(helper.getEndAfterPadding()
                            - helper.getDecoratedEnd(currentView));
                }
            } else {
                currentViewDistance = Math.abs(helper.getDecoratedStart(currentView)
                        + (helper.getDecoratedMeasurement(currentView) / 2) - center);
            }
            if (currentViewDistance < distanceToTarget) {
                distanceToTarget = currentViewDistance;
                edgeView = currentView;
            }
        }
        return edgeView;
    }

    private boolean isAtEdgeOfList(LinearLayoutManager lm) {
        if ((!lm.getReverseLayout() && gravity == Gravity.START)
                || (lm.getReverseLayout() && gravity == Gravity.END)
                || (!lm.getReverseLayout() && gravity == Gravity.TOP)
                || (lm.getReverseLayout() && gravity == Gravity.BOTTOM)) {
            return lm.findLastCompletelyVisibleItemPosition() == lm.getItemCount() - 1;
        } else if (gravity == Gravity.CENTER) {
            return lm.findFirstCompletelyVisibleItemPosition() == 0
                    || lm.findLastCompletelyVisibleItemPosition() == lm.getItemCount() - 1;
        } else {
            return lm.findFirstCompletelyVisibleItemPosition() == 0;
        }
    }


    private void onScrollStateChanged(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE && listener != null) {
            if (isScrolling) {
                if (nextSnapPosition != RecyclerView.NO_POSITION) {
                    listener.onSnap(nextSnapPosition);
                } else {
                    dispatchSnapChangeWhenPositionIsUnknown();
                }
            }
        }
        isScrolling = newState != RecyclerView.SCROLL_STATE_IDLE;
    }


    private void dispatchSnapChangeWhenPositionIsUnknown() {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        View snapView = findSnapView(layoutManager, false);
        if (snapView == null) {
            return;
        }
        int snapPosition = recyclerView.getChildAdapterPosition(snapView);
        if (snapPosition != RecyclerView.NO_POSITION) {
            listener.onSnap(snapPosition);
        }
    }

    private OrientationHelper getVerticalHelper(RecyclerView.LayoutManager layoutManager) {
        if (verticalHelper == null || verticalHelper.getLayoutManager() != layoutManager) {
            verticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return verticalHelper;
    }

    private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
        if (horizontalHelper == null || horizontalHelper.getLayoutManager() != layoutManager) {
            horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return horizontalHelper;
    }

    public interface SnapListener {
        /**
         * @param position last position snapped to
         */
        void onSnap(int position);
    }

}
