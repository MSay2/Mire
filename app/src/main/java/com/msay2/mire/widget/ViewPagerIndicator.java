package com.msay2.mire.widget;

import android.app.Activity;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;

import java.util.Arrays;

import com.msay2.mire.R;
import fr.yoann.dev.preferences.utils.AnimUtils;

public class ViewPagerIndicator extends View implements View.OnAttachStateChangeListener
{
    private static final int DEFAULT_DOT_SIZE = 8;                      // dp
    private static final int DEFAULT_GAP = 12;                          // dp
    private static final int DEFAULT_ANIM_DURATION = 400;               // ms
    private static final int DEFAULT_UNSELECTED_COLOUR = 0x80ffffff;    // 50% white
    private static final int DEFAULT_SELECTED_COLOUR = 0xffffffff;      // 100% white

    private static final float INVALID_FRACTION = -1f;
    private static final float MINIMAL_REVEAL = 0.00001f;

    private int dotDiameter;
    private int gap;
    private long animDuration;
    private int unselectedColour;
    private int selectedColour;

    private float dotRadius;
    private float halfDotRadius;
    private long animHalfDuration;
    private float dotTopY;
    private float dotCenterY;
    private float dotBottomY;

    private ViewPager viewPager;

    private int pageCount;
    private int currentPage;
    private int previousPage;
    private float selectedDotX;
    private boolean selectedDotInPosition;
    private float[] dotCenterX;
    private float[] joiningFractions;
    private float retreatingJoinX1;
    private float retreatingJoinX2;
    private float[] dotRevealFractions;
    private boolean isAttachedToWindow;
    private boolean pageChanging;

    private final Paint unselectedPaint;
    private final Paint selectedPaint;
    private final Path combinedUnselectedPath;
    private final Path unselectedDotPath;
    private final Path unselectedDotLeftPath;
    private final Path unselectedDotRightPath;
    private final RectF rectF;

    private ValueAnimator moveAnimation;
    private AnimatorSet joiningAnimationSet;
    private PendingRetreatAnimator retreatAnimation;
    private PendingRevealAnimator[] revealAnimations;
    private final Interpolator interpolator;

    float endX1;
    float endY1;
    float endX2;
    float endY2;
    float controlX1;
    float controlY1;
    float controlX2;
    float controlY2;

    public ViewPagerIndicator(Context context)
	{
        this(context, null, 0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs)
	{
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyle)
	{
        super(context, attrs, defStyle);

        final int density = (int) context.getResources().getDisplayMetrics().density;
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.InkPageIndicator, defStyle, 0);

        dotDiameter = a.getDimensionPixelSize(R.styleable.InkPageIndicator_dotDiameter, DEFAULT_DOT_SIZE * density);
        dotRadius = dotDiameter / 2;
        halfDotRadius = dotRadius / 2;
        
		gap = a.getDimensionPixelSize(R.styleable.InkPageIndicator_dotGap, DEFAULT_GAP * density);
        animDuration = (long)a.getInteger(R.styleable.InkPageIndicator_animationDuration, DEFAULT_ANIM_DURATION);
        animHalfDuration = animDuration / 2;
        
		unselectedColour = a.getColor(R.styleable.InkPageIndicator_pageIndicatorColor, DEFAULT_UNSELECTED_COLOUR);
        selectedColour = a.getColor(R.styleable.InkPageIndicator_currentPageIndicatorColor, DEFAULT_SELECTED_COLOUR);

        a.recycle();

        unselectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        unselectedPaint.setColor(unselectedColour);
        selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedPaint.setColor(selectedColour);
        interpolator = AnimUtils.getFastOutSlowInInterpolator(context);

        combinedUnselectedPath = new Path();
        unselectedDotPath = new Path();
        unselectedDotLeftPath = new Path();
        unselectedDotRightPath = new Path();
        rectF = new RectF();

        addOnAttachStateChangeListener(this);
    }

    public void setFinalStateViewPager(Activity activity, ViewPager viewPager)
	{
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{
				if (isAttachedToWindow)
				{
					float fraction = positionOffset;
					int currentPosition = pageChanging ? previousPage : currentPage;
					int leftDotPosition = position;
					if (currentPosition != position) 
					{
						fraction = 1f - positionOffset;
						if (fraction == 1f)
						{
							leftDotPosition = Math.min(currentPosition, position);
						}
					}
					setJoiningFraction(leftDotPosition, fraction);
				}
			}

			@Override
			public void onPageSelected(int position) 
			{
				if (isAttachedToWindow)
				{
					setSelectedPage(position);
				} 
				else 
				{
					setCurrentPageImmediate();
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) 
			{ }
		});
		setPageCount(viewPager.getAdapter().getCount());
        viewPager.getAdapter().registerDataSetObserver(new DataSetObserver()
		{
			@Override
			public void onChanged()
			{
				setPageCount(ViewPagerIndicator.this.viewPager.getAdapter().getCount());
			}
		});
        setCurrentPageImmediate();
    }

    private void setPageCount(int pages) 
	{
        pageCount = pages;
        resetState();
        requestLayout();
    }

    private void calculateDotPositions(int width, int height)
	{
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = width - getPaddingRight();
        int bottom = height - getPaddingBottom();

        int requiredWidth = getRequiredWidth();
        float startLeft = left + ((right - left - requiredWidth) / 2) + dotRadius;

        dotCenterX = new float[pageCount];
        for (int i = 0; i < pageCount; i++) 
		{
            dotCenterX[i] = startLeft + i * (dotDiameter + gap);
        }

        dotTopY = top;
        dotCenterY = top + dotRadius;
        dotBottomY = top + dotDiameter;

        setCurrentPageImmediate();
    }

    private void setCurrentPageImmediate() 
	{
        if (viewPager != null) 
		{
            currentPage = viewPager.getCurrentItem();
        }
		else
		{
            currentPage = 0;
        }
		
        if (dotCenterX != null) 
		{
            selectedDotX = dotCenterX[currentPage];
        }
    }

    private void resetState()
	{
        joiningFractions = new float[pageCount - 1];
        
		Arrays.fill(joiningFractions, 0f);
        dotRevealFractions = new float[pageCount];
        
		Arrays.fill(dotRevealFractions, 0f);
        retreatingJoinX1 = INVALID_FRACTION;
        retreatingJoinX2 = INVALID_FRACTION;
        selectedDotInPosition = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
        int desiredHeight = getDesiredHeight();
        int height;
        switch (MeasureSpec.getMode(heightMeasureSpec)) 
		{
            case MeasureSpec.EXACTLY:
                height = MeasureSpec.getSize(heightMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
                height = Math.min(desiredHeight, MeasureSpec.getSize(heightMeasureSpec));
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                height = desiredHeight;
                break;
        }

        int desiredWidth = getDesiredWidth();
        int width;
        switch (MeasureSpec.getMode(widthMeasureSpec)) 
		{
            case MeasureSpec.EXACTLY:
                width = MeasureSpec.getSize(widthMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
                width = Math.min(desiredWidth, MeasureSpec.getSize(widthMeasureSpec));
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                width = desiredWidth;
                break;
        }
        setMeasuredDimension(width, height);
        calculateDotPositions(width, height);
    }

    private int getDesiredHeight()
	{
        return getPaddingTop() + dotDiameter + getPaddingBottom();
    }

    private int getRequiredWidth()
	{
        return pageCount * dotDiameter + (pageCount - 1) * gap;
    }

    private int getDesiredWidth()
	{
        return getPaddingLeft() + getRequiredWidth() + getPaddingRight();
    }

    @Override
    public void onViewAttachedToWindow(View view)
	{
        isAttachedToWindow = true;
    }

    @Override
    public void onViewDetachedFromWindow(View view)
	{
        isAttachedToWindow = false;
    }

    @Override
    protected void onDraw(Canvas canvas)
	{
        if (viewPager == null || pageCount == 0)
		{
			return;
		}
        drawUnselected(canvas);
        drawSelected(canvas);
    }

    private void drawUnselected(Canvas canvas) 
	{
        combinedUnselectedPath.rewind();
        for (int page = 0; page < pageCount; page++) 
		{
            int nextXIndex = page == pageCount - 1 ? page : page + 1;
            combinedUnselectedPath.op(getUnselectedPath(page, dotCenterX[page], dotCenterX[nextXIndex], page == pageCount - 1 ? INVALID_FRACTION : joiningFractions[page], dotRevealFractions[page]), Path.Op.UNION);
        }

        if (retreatingJoinX1 != INVALID_FRACTION)
		{
            combinedUnselectedPath.op(getRetreatingJoinPath(), Path.Op.UNION);
        }
        canvas.drawPath(combinedUnselectedPath, unselectedPaint);
    }
	
    private Path getUnselectedPath(int page, float centerX, float nextCenterX, float joiningFraction, float dotRevealFraction)
	{
        unselectedDotPath.rewind();
        if ((joiningFraction == 0f || joiningFraction == INVALID_FRACTION) && dotRevealFraction == 0f && !(page == currentPage && selectedDotInPosition == true)) 
		{
            unselectedDotPath.addCircle(dotCenterX[page], dotCenterY, dotRadius, Path.Direction.CW);
        }

        if (joiningFraction > 0f && joiningFraction <= 0.5f && retreatingJoinX1 == INVALID_FRACTION) 
		{
            unselectedDotLeftPath.rewind();
            unselectedDotLeftPath.moveTo(centerX, dotBottomY);

            rectF.set(centerX - dotRadius, dotTopY, centerX + dotRadius, dotBottomY);
            unselectedDotLeftPath.arcTo(rectF, 90, 180, true);

            endX1 = centerX + dotRadius + (joiningFraction * gap);
            endY1 = dotCenterY;
            controlX1 = centerX + halfDotRadius;
            controlY1 = dotTopY;
            controlX2 = endX1;
            controlY2 = endY1 - halfDotRadius;
            unselectedDotLeftPath.cubicTo(controlX1, controlY1, controlX2, controlY2, endX1, endY1);

            endX2 = centerX;
            endY2 = dotBottomY;
            controlX1 = endX1;
            controlY1 = endY1 + halfDotRadius;
            controlX2 = centerX + halfDotRadius;
            controlY2 = dotBottomY;
            unselectedDotLeftPath.cubicTo(controlX1, controlY1, controlX2, controlY2, endX2, endY2);

            unselectedDotPath.op(unselectedDotLeftPath, Path.Op.UNION);

            unselectedDotRightPath.rewind();
            unselectedDotRightPath.moveTo(nextCenterX, dotBottomY);

            rectF.set(nextCenterX - dotRadius, dotTopY, nextCenterX + dotRadius, dotBottomY);
            unselectedDotRightPath.arcTo(rectF, 90, -180, true);

            endX1 = nextCenterX - dotRadius - (joiningFraction * gap);
            endY1 = dotCenterY;
            controlX1 = nextCenterX - halfDotRadius;
            controlY1 = dotTopY;
            controlX2 = endX1;
            controlY2 = endY1 - halfDotRadius;
            unselectedDotRightPath.cubicTo(controlX1, controlY1, controlX2, controlY2, endX1, endY1);
										   
            endX2 = nextCenterX;
            endY2 = dotBottomY;
            controlX1 = endX1;
            controlY1 = endY1 + halfDotRadius;
            controlX2 = endX2 - halfDotRadius;
            controlY2 = dotBottomY;
            unselectedDotRightPath.cubicTo(controlX1, controlY1, controlX2, controlY2, endX2, endY2);
            unselectedDotPath.op(unselectedDotRightPath, Path.Op.UNION);
        }

        if (joiningFraction > 0.5f && joiningFraction < 1f && retreatingJoinX1 == INVALID_FRACTION) 
		{
            float adjustedFraction = (joiningFraction - 0.2f) * 1.25f;

            unselectedDotPath.moveTo(centerX, dotBottomY);

            rectF.set(centerX - dotRadius, dotTopY, centerX + dotRadius, dotBottomY);
            unselectedDotPath.arcTo(rectF, 90, 180, true);

            endX1 = centerX + dotRadius + (gap / 2);
            endY1 = dotCenterY - (adjustedFraction * dotRadius);
            controlX1 = endX1 - (adjustedFraction * dotRadius);
            controlY1 = dotTopY;
            controlX2 = endX1 - ((1 - adjustedFraction) * dotRadius);
            controlY2 = endY1;
            unselectedDotPath.cubicTo(controlX1, controlY1, controlX2, controlY2, endX1, endY1);

            endX2 = nextCenterX;
            endY2 = dotTopY;
            controlX1 = endX1 + ((1 - adjustedFraction) * dotRadius);
            controlY1 = endY1;
            controlX2 = endX1 + (adjustedFraction * dotRadius);
            controlY2 = dotTopY;
            unselectedDotPath.cubicTo(controlX1, controlY1, controlX2, controlY2, endX2, endY2);

            rectF.set(nextCenterX - dotRadius, dotTopY, nextCenterX + dotRadius, dotBottomY);
            unselectedDotPath.arcTo(rectF, 270, 180, true);
            endY1 = dotCenterY + (adjustedFraction * dotRadius);
            controlX1 = endX1 + (adjustedFraction * dotRadius);
            controlY1 = dotBottomY;
            controlX2 = endX1 + ((1 - adjustedFraction) * dotRadius);
            controlY2 = endY1;
            unselectedDotPath.cubicTo(controlX1, controlY1, controlX2, controlY2, endX1, endY1);
            endX2 = centerX;
            endY2 = dotBottomY;
            controlX1 = endX1 - ((1 - adjustedFraction) * dotRadius);
            controlY1 = endY1;
            controlX2 = endX1 - (adjustedFraction * dotRadius);
            controlY2 = endY2;
            unselectedDotPath.cubicTo(controlX1, controlY1, controlX2, controlY2, endX2, endY2);
        }
        if (joiningFraction == 1 && retreatingJoinX1 == INVALID_FRACTION) 
		{
            rectF.set(centerX - dotRadius, dotTopY, nextCenterX + dotRadius, dotBottomY);
            unselectedDotPath.addRoundRect(rectF, dotRadius, dotRadius, Path.Direction.CW);
        }

        if (dotRevealFraction > MINIMAL_REVEAL)
		{
            unselectedDotPath.addCircle(centerX, dotCenterY, dotRevealFraction * dotRadius, Path.Direction.CW);
        }
        return unselectedDotPath;
    }

    private Path getRetreatingJoinPath()
	{
        unselectedDotPath.rewind();
        rectF.set(retreatingJoinX1, dotTopY, retreatingJoinX2, dotBottomY);
        unselectedDotPath.addRoundRect(rectF, dotRadius, dotRadius, Path.Direction.CW);
        
		return unselectedDotPath;
    }

    private void drawSelected(Canvas canvas)
	{
        canvas.drawCircle(selectedDotX, dotCenterY, dotRadius, selectedPaint);
    }

    private void setSelectedPage(int now)
	{
        if (now == currentPage)
		{
			return;
		}
        pageChanging = true;
        previousPage = currentPage;
        currentPage = now;
		
        final int steps = Math.abs(now - previousPage);
        if (steps > 1)
		{
            if (now > previousPage)
			{
                for (int i = 0; i < steps; i++)
				{
                    setJoiningFraction(previousPage + i, 1f);
                }
            } 
			else
			{
                for (int i = -1; i > -steps; i--) 
				{
                    setJoiningFraction(previousPage + i, 1f);
                }
            }
        }
        moveAnimation = createMoveSelectedAnimator(dotCenterX[now], previousPage, now, steps);
        moveAnimation.start();
    }

    private ValueAnimator createMoveSelectedAnimator(final float moveTo, int was, int now, int steps)
	{
        ValueAnimator moveSelected = ValueAnimator.ofFloat(selectedDotX, moveTo);
        retreatAnimation = new PendingRetreatAnimator(was, now, steps, now > was ? new RightwardStartPredicate(moveTo - ((moveTo - selectedDotX) * 0.25f)) : new LeftwardStartPredicate(moveTo + ((selectedDotX - moveTo) * 0.25f)));
        retreatAnimation.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				resetState();
				pageChanging = false;
			}
		});
        moveSelected.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() 
		{
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) 
			{
				selectedDotX = (Float) valueAnimator.getAnimatedValue();
				retreatAnimation.startIfNecessary(selectedDotX);
				postInvalidateOnAnimation();
			}
		});
        moveSelected.addListener(new AnimatorListenerAdapter() 
		{
			@Override
			public void onAnimationStart(Animator animation) 
			{
				selectedDotInPosition = false;
			}

			@Override
			public void onAnimationEnd(Animator animation)
			{
				selectedDotInPosition = true;
			}
		});
        moveSelected.setStartDelay(selectedDotInPosition ? animDuration / 4l : 0l);
        moveSelected.setDuration(animDuration * 3l / 4l);
        moveSelected.setInterpolator(interpolator);
        
		return moveSelected;
    }

    private void setJoiningFraction(int leftDot, float fraction) 
	{
        if (leftDot < joiningFractions.length)
		{
            if (leftDot == 1) 
			{
                Log.d("PageIndicator", "dot 1 fraction:\t" + fraction);
            }
            joiningFractions[leftDot] = fraction;
            postInvalidateOnAnimation();
        }
    }

    private void clearJoiningFractions()
	{
        Arrays.fill(joiningFractions, 0f);
        postInvalidateOnAnimation();
    }

    private void setDotRevealFraction(int dot, float fraction) 
	{
        dotRevealFractions[dot] = fraction;
        postInvalidateOnAnimation();
    }

    private void cancelJoiningAnimations()
	{
        if (joiningAnimationSet != null && joiningAnimationSet.isRunning()) 
		{
            joiningAnimationSet.cancel();
        }
    }

    public abstract class PendingStartAnimator extends ValueAnimator 
	{
        protected boolean hasStarted;
        protected StartPredicate predicate;

        public PendingStartAnimator(StartPredicate predicate) 
		{
            super();
			
            this.predicate = predicate;
            hasStarted = false;
        }

        public void startIfNecessary(float currentValue) 
		{
            if (!hasStarted && predicate.shouldStart(currentValue))
			{
                start();
                hasStarted = true;
            }
        }
    }

    public class PendingRetreatAnimator extends PendingStartAnimator
	{
        public PendingRetreatAnimator(int was, int now, int steps, StartPredicate predicate)
		{
            super(predicate);
			
            setDuration(animHalfDuration);
            setInterpolator(interpolator);

            final float initialX1 = now > was ? Math.min(dotCenterX[was], selectedDotX) - dotRadius : dotCenterX[now] - dotRadius;
            final float finalX1 = now > was ? dotCenterX[now] - dotRadius : dotCenterX[now] - dotRadius;
            final float initialX2 = now > was ? dotCenterX[now] + dotRadius : Math.max(dotCenterX[was], selectedDotX) + dotRadius;
            final float finalX2 = now > was ? dotCenterX[now] + dotRadius : dotCenterX[now] + dotRadius;

            revealAnimations = new PendingRevealAnimator[steps];
			
            final int[] dotsToHide = new int[steps];
            if (initialX1 != finalX1)
			{
                setFloatValues(initialX1, finalX1);
                for (int i = 0; i < steps; i++) 
				{
                    revealAnimations[i] = new PendingRevealAnimator(was + i, new RightwardStartPredicate(dotCenterX[was + i]));
                    dotsToHide[i] = was + i;
                }
                addUpdateListener(new AnimatorUpdateListener()
				{
					@Override
					public void onAnimationUpdate(ValueAnimator valueAnimator)
					{
						retreatingJoinX1 = (Float) valueAnimator.getAnimatedValue();
						postInvalidateOnAnimation();
						for (PendingRevealAnimator pendingReveal : revealAnimations)
						{
							pendingReveal.startIfNecessary(retreatingJoinX1);
						}
					}
				});
            }
			else
			{
                setFloatValues(initialX2, finalX2);
                for (int i = 0; i < steps; i++) 
				{
                    revealAnimations[i] = new PendingRevealAnimator(was - i, new LeftwardStartPredicate(dotCenterX[was - i]));
                    dotsToHide[i] = was - i;
                }
                addUpdateListener(new AnimatorUpdateListener()
				{
					@Override
					public void onAnimationUpdate(ValueAnimator valueAnimator) 
					{
						retreatingJoinX2 = (Float) valueAnimator.getAnimatedValue();
						postInvalidateOnAnimation();
						for (PendingRevealAnimator pendingReveal : revealAnimations) 
						{
							pendingReveal.startIfNecessary(retreatingJoinX2);
						}
					}
				});
            }

            addListener(new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationStart(Animator animation) 
				{
					cancelJoiningAnimations();
					clearJoiningFractions();
					for (int dot : dotsToHide) 
					{
						setDotRevealFraction(dot, MINIMAL_REVEAL);
					}
					retreatingJoinX1 = initialX1;
					retreatingJoinX2 = initialX2;
					postInvalidateOnAnimation();
				}
				@Override
				public void onAnimationEnd(Animator animation) 
				{
					retreatingJoinX1 = INVALID_FRACTION;
					retreatingJoinX2 = INVALID_FRACTION;
					postInvalidateOnAnimation();
				}
			});
        }
    }

    public class PendingRevealAnimator extends PendingStartAnimator
	{
        private int dot;

        public PendingRevealAnimator(int dot, StartPredicate predicate)
		{
            super(predicate);
           
			setFloatValues(MINIMAL_REVEAL, 1f);
            
			this.dot = dot;
            setDuration(animHalfDuration);
            setInterpolator(interpolator);
            addUpdateListener(new AnimatorUpdateListener() 
			{
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) 
				{
					setDotRevealFraction(PendingRevealAnimator.this.dot, (Float)valueAnimator.getAnimatedValue());
				}
			});
            addListener(new AnimatorListenerAdapter() 
			{
				@Override
				public void onAnimationEnd(Animator animation)
				{
					setDotRevealFraction(PendingRevealAnimator.this.dot, 0f);
					postInvalidateOnAnimation();
				}
			});
        }
    }

    public abstract class StartPredicate 
	{
        protected float thresholdValue;

        public StartPredicate(float thresholdValue) 
		{
            this.thresholdValue = thresholdValue;
        }
        abstract boolean shouldStart(float currentValue);
    }

    public class RightwardStartPredicate extends StartPredicate 
	{
        public RightwardStartPredicate(float thresholdValue)
		{
            super(thresholdValue);
        }

        boolean shouldStart(float currentValue)
		{
            return currentValue > thresholdValue;
        }
    }

    public class LeftwardStartPredicate extends StartPredicate
	{
        public LeftwardStartPredicate(float thresholdValue)
		{
            super(thresholdValue);
        }

        boolean shouldStart(float currentValue)
		{
            return currentValue < thresholdValue;
        }
    }
}
