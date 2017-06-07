package fr.yoann.dev.preferences.widget;

import fr.yoann.dev.preferences.utils.AnimUtils;
import fr.yoann.dev.preferences.utils.MathUtils;
import fr.yoann.dev.preferences.helpers.ViewOffsetHelper;

import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BottomSheet extends FrameLayout
{
    private static final long DEFAULT_SETTLE_DURATION = 300L;
    private static final long MIN_SETTLE_DURATION = 50L;

    private final int MIN_FLING_VELOCITY;
    private final int MAX_FLING_VELOCITY;

    private View sheet;
    private ViewDragHelper sheetDragHelper;
    private ViewOffsetHelper sheetOffsetHelper;

    private List<Callbacks> callbacks;
    private int sheetExpandedTop;
    private int sheetBottom;
    private int dismissOffset;
    private int nestedScrollInitialTop;
    private boolean settling = false;
    private boolean isNestedScrolling = false;
    private boolean initialHeightChecked = false;
    private boolean hasInteractedWithSheet = false;

    public BottomSheet(Context context)
	{
        this(context, null, 0);
    }

    public BottomSheet(Context context, AttributeSet attrs) 
	{
        this(context, attrs, 0);
    }

    public BottomSheet(Context context, AttributeSet attrs, int defStyle) 
	{
        super(context, attrs, defStyle);

		final ViewConfiguration viewConfiguration = ViewConfiguration.get(context);

		MIN_FLING_VELOCITY = viewConfiguration.getScaledMinimumFlingVelocity();
        MAX_FLING_VELOCITY = viewConfiguration.getScaledMaximumFlingVelocity();
    }

    public static abstract class Callbacks 
	{
        public void onSheetDismissed()
		{ }
        public void onSheetPositionChanged(int sheetTop, boolean userInteracted)
		{ }
    }

    public void registerCallback(Callbacks callback) 
	{
        if (callbacks == null) 
		{
            callbacks = new CopyOnWriteArrayList<>();
        }
        callbacks.add(callback);
    }

    public void unregisterCallback(Callbacks callback) 
	{
        if (callbacks != null && !callbacks.isEmpty()) 
		{
            callbacks.remove(callback);
        }
    }

    public void dismiss() 
	{
        animateSettle(dismissOffset);
    }

    public void expand()
	{
        animateSettle(0);
    }

    public boolean isExpanded() 
	{
        return sheet.getTop() == sheetExpandedTop;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params)
	{
        if (sheet != null)
		{
            throw new UnsupportedOperationException("BottomSheet must only have 1 child view");
        }

		sheet = child;
        sheetOffsetHelper = new ViewOffsetHelper(sheet);
        sheet.addOnLayoutChangeListener(sheetLayout);

        ((LayoutParams)params).gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

		super.addView(child, index, params);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) 
	{
        hasInteractedWithSheet = true;
        if (isNestedScrolling)
		{
			return false;
		}

        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP)
		{
            sheetDragHelper.cancel();
            return false;
        }
        return isDraggableViewUnder((int) ev.getX(), (int) ev.getY()) && (sheetDragHelper.shouldInterceptTouchEvent(ev));
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) 
	{
        sheetDragHelper.processTouchEvent(ev);
        if (sheetDragHelper.getCapturedView() != null) 
		{
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() 
	{
        if (sheetDragHelper.continueSettling(true)) 
		{
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) 
	{
        if ((nestedScrollAxes & View.SCROLL_AXIS_VERTICAL) != 0) 
		{
            isNestedScrolling = true;
            nestedScrollInitialTop = sheet.getTop();

			return true;
        }
        return false;
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed)
	{
        if (dyUnconsumed < 0)
		{
            sheetOffsetHelper.offsetTopAndBottom(-dyUnconsumed);
            dispatchPositionChangedCallback();
        }
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed)
	{
        if (dy > 0) 
		{
            final int upwardDragRange = sheet.getTop() - sheetExpandedTop;
            if (upwardDragRange > 0) 
			{
                final int consume = Math.min(upwardDragRange, dy);

				sheetOffsetHelper.offsetTopAndBottom(-consume);
                dispatchPositionChangedCallback();
                consumed[1] = consume;
            }
        }
    }

    @Override
    public void onStopNestedScroll(View child) 
	{
        isNestedScrolling = false;
        if (!settling && sheet.getTop() != nestedScrollInitialTop)
		{
            expand();
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed)
	{
        if (velocityY <= -MIN_FLING_VELOCITY && !target.canScrollVertically(-1))
		{
            animateSettle(dismissOffset, computeSettleDuration(velocityY, true));
            return true;
        }
		else if (velocityY > 0 && !isExpanded()) 
		{
            animateSettle(0, computeSettleDuration(velocityY, false));
        }
        return false;
    }

    @Override
    protected void onAttachedToWindow()
	{
        super.onAttachedToWindow();

		sheetDragHelper = ViewDragHelper.create(this, dragHelperCallbacks);
    }

    private boolean isDraggableViewUnder(int x, int y)
	{
        return getVisibility() == VISIBLE && sheetDragHelper.isViewUnder(this, x, y);
    }

    private void animateSettle(int targetOffset) 
	{
        animateSettle(targetOffset, DEFAULT_SETTLE_DURATION);
    }

    private void animateSettle(int targetOffset, long duration)
	{
        animateSettle(sheetOffsetHelper.getTopAndBottomOffset(), targetOffset, duration);
    }

    private void animateSettle(int initialOffset, final int targetOffset, long duration) 
	{
        if (settling) 
		{
			return;
		}
        if (sheetOffsetHelper.getTopAndBottomOffset() == targetOffset)
		{
			if (targetOffset >= dismissOffset) 
			{
				dispatchDismissCallback();
			}
			return;
        }

        settling = true;

        final ObjectAnimator settleAnim = ObjectAnimator.ofInt(sheetOffsetHelper, ViewOffsetHelper.OFFSET_Y, initialOffset, targetOffset);
        settleAnim.setDuration(duration);
        settleAnim.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(getContext()));
        settleAnim.addListener(new AnimatorListenerAdapter() 
			{
				@Override
				public void onAnimationEnd(Animator animation)
				{
					dispatchPositionChangedCallback();
					if (targetOffset == dismissOffset) 
					{
						dispatchDismissCallback();
					}
					settling = false;
				}
			});
        if (callbacks != null && !callbacks.isEmpty()) 
		{
            settleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
				{
					@Override
					public void onAnimationUpdate(ValueAnimator animation) 
					{
						if (animation.getAnimatedFraction() > 0f)
						{
							dispatchPositionChangedCallback();
						}
					}
				});
        }
        settleAnim.start();
    }

    private long computeSettleDuration(final float velocity, final boolean dismissing) 
	{
        final float settleDistance = dismissing ? sheetBottom - sheet.getTop() : sheet.getTop() - sheetExpandedTop;
        final float clampedVelocity = MathUtils.constrain(MIN_FLING_VELOCITY, MAX_FLING_VELOCITY, Math.abs(velocity));
        final float distanceFraction = settleDistance / (sheetBottom - sheetExpandedTop);
        final float velocityFraction = clampedVelocity / MAX_FLING_VELOCITY;
        final long duration = MIN_SETTLE_DURATION + (long) (distanceFraction * (1f - velocityFraction) * DEFAULT_SETTLE_DURATION);

		return duration;
    }

    private final ViewDragHelper.Callback dragHelperCallbacks = new ViewDragHelper.Callback()
	{
        @Override
        public boolean tryCaptureView(View child, int pointerId) 
		{
            return child == sheet;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) 
		{
            return Math.min(Math.max(top, sheetExpandedTop), sheetBottom);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx)
		{
            return sheet.getLeft();
        }

        @Override
        public int getViewVerticalDragRange(View child) 
		{
            return sheetBottom - sheetExpandedTop;
        }

        @Override
        public void onViewPositionChanged(View child, int left, int top, int dx, int dy)
		{
            sheetOffsetHelper.resyncOffsets();
            dispatchPositionChangedCallback();
        }

        @Override
        public void onViewReleased(View releasedChild, float velocityX, float velocityY)
		{
            final boolean dismiss = velocityY >= MIN_FLING_VELOCITY;

			animateSettle(dismiss ? dismissOffset : 0, computeSettleDuration(velocityY, dismiss));
        }
    };

    private final OnLayoutChangeListener sheetLayout = new OnLayoutChangeListener()
	{
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) 
		{
            sheetExpandedTop = top;
            sheetBottom = bottom;
            dismissOffset = bottom - top;
            sheetOffsetHelper.onViewLayout();

            if (!initialHeightChecked) 
			{
                applySheetInitialHeightOffset(false, -1);
                initialHeightChecked = true;
            } 
			else if (!hasInteractedWithSheet && (oldBottom - oldTop) != (bottom - top)) 
			{
				applySheetInitialHeightOffset(true, oldTop - sheetExpandedTop);
            }
        }
    };

    private void applySheetInitialHeightOffset(boolean animateChange, int previousOffset)
	{
        final int minimumGap = sheet.getMeasuredWidth() / 16 * 9;
        if (sheet.getTop() < minimumGap) 
		{
            final int offset = minimumGap - sheet.getTop();
            if (animateChange)
			{
                animateSettle(previousOffset, offset, DEFAULT_SETTLE_DURATION);
            } 
			else 
			{
                sheetOffsetHelper.setTopAndBottomOffset(offset);
            }
        }
    }

    private void dispatchDismissCallback()
	{
        if (callbacks != null && !callbacks.isEmpty()) 
		{
            for (Callbacks callback : callbacks)
			{
                callback.onSheetDismissed();
            }
        }
    }

    private void dispatchPositionChangedCallback() 
	{
        if (callbacks != null && !callbacks.isEmpty()) 
		{
            for (Callbacks callback : callbacks)
			{
                callback.onSheetPositionChanged(sheet.getTop(), hasInteractedWithSheet);
            }
        }
    }
}
