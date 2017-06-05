package com.msay2.mire.transition;

/*
 this class for the {@CircularReveal} and {@SharedElements}
*/

import fr.yoann.dev.preferences.utils.AnimUtils;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.ArrayMap;
import android.view.ViewAnimationUtils;
import android.view.ViewOutlineProvider;
import android.view.animation.Interpolator;
import android.view.ViewGroup;
import android.view.View;

import static android.view.View.MeasureSpec.makeMeasureSpec;

import java.util.ArrayList;
import java.util.List;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.DrawableRes;

import android.support.v4.content.ContextCompat;

public class CircularRevealTransformEnter extends Transition
{
	private static final String REVEAL_NAME = "com.msay2.mire.transition:CircularRevealTransformEnter:reveal";
	private static final String EXTRA_ICON_FAB_RES_ID = "EXTRA_FAB_ICON_RES_ID";
	private static final long DEFAULT_DURATION = 240L;
	private static final String[] TRANSITION_PROPERTIES = 
	{
		REVEAL_NAME
    };
	
	private int icon;
	
	public CircularRevealTransformEnter(@DrawableRes int iconFabResId)
	{
		icon = iconFabResId;
		setPathMotion(new ArcMotion());
		setDuration(DEFAULT_DURATION);
	}
	
	@Override
	public void captureStartValues(TransitionValues transitionValues)
	{
		captureValues(transitionValues);
	}

	@Override
	public void captureEndValues(TransitionValues transitionValues)
	{
		captureValues(transitionValues);
	}

	@Override
	public String[] getTransitionProperties()
	{
		return TRANSITION_PROPERTIES;
	}
	
	private void captureValues(TransitionValues transitionValues) 
	{
        final View view = transitionValues.view;
        if (view == null || view.getWidth() <= 0 || view.getHeight() <= 0) 
		{
			return;
		}
        transitionValues.values.put(REVEAL_NAME, new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
    }

	@Override
	public Animator createAnimator(final ViewGroup sceneRoot, final TransitionValues startValues, final TransitionValues endValues)
	{
		if (startValues == null || endValues == null)
		{
			return null;
		}
		
		final Rect startBounds = (Rect)startValues.values.get(REVEAL_NAME);
        final Rect endBounds = (Rect)endValues.values.get(REVEAL_NAME);
		
		final boolean fromFab = endBounds.width() > startBounds.width();
		final View view = endValues.view;
		final Rect dialogBounds = fromFab ? endBounds : startBounds;
        final Rect fabBounds = fromFab ? startBounds : endBounds;
		final Interpolator fastOutSlowInInterpolator = AnimUtils.getFastOutSlowInInterpolator(sceneRoot.getContext());
		final long duration = getDuration();
        final long halfDuration = duration / 2;
        final long twoThirdsDuration = duration * 2 / 3;
		
		if (!fromFab) 
		{
            view.measure(makeMeasureSpec(startBounds.width(), View.MeasureSpec.EXACTLY), makeMeasureSpec(startBounds.height(), View.MeasureSpec.EXACTLY));
            view.layout(startBounds.left, startBounds.top, startBounds.right, startBounds.bottom);
        }
		
		final int translationX = startBounds.centerX() - endBounds.centerX();
        final int translationY = startBounds.centerY() - endBounds.centerY();
        if (fromFab) 
		{
            view.setTranslationX(translationX);
            view.setTranslationY(translationY);
        }
		
		final Drawable fabIcon = ContextCompat.getDrawable(sceneRoot.getContext(), icon).mutate();
        final int iconLeft = (dialogBounds.width() - fabIcon.getIntrinsicWidth()) / 2;
        final int iconTop = (dialogBounds.height() - fabIcon.getIntrinsicHeight()) / 2;
       
		fabIcon.setBounds(iconLeft, iconTop, iconLeft + fabIcon.getIntrinsicWidth(), iconTop + fabIcon.getIntrinsicHeight());
        if (!fromFab) 
		{
			fabIcon.setAlpha(0);
		}
        view.getOverlay().add(fabIcon);
		
		final Animator circularReveal;
        if (fromFab)
		{
            circularReveal = ViewAnimationUtils.createCircularReveal(view, view.getWidth() / 2, view.getHeight() / 2, startBounds.width() / 2, (float)Math.hypot(endBounds.width() / 2, endBounds.height() / 2));
            circularReveal.setInterpolator(AnimUtils.getFastOutLinearInInterpolator(sceneRoot.getContext()));
        } 
		else 
		{
            circularReveal = ViewAnimationUtils.createCircularReveal(view, view.getWidth() / 2, view.getHeight() / 2, (float)Math.hypot(startBounds.width() / 2, startBounds.height() / 2), endBounds.width() / 2);
            circularReveal.setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(sceneRoot.getContext()));
            circularReveal.addListener(new AnimatorListenerAdapter() 
			{
				@Override
				public void onAnimationEnd(Animator animation)
				{
					view.setOutlineProvider(new ViewOutlineProvider() 
					{
						@Override
						public void getOutline(View view, Outline outline)
						{
							final int left = (view.getWidth() - fabBounds.width()) / 2;
							final int top = (view.getHeight() - fabBounds.height()) / 2;
							
							outline.setOval(left, top, left + fabBounds.width(), top + fabBounds.height());
							view.setClipToOutline(true);
						}
					});
				}
			});
        }
        circularReveal.setDuration(duration);
		
		final Animator translate = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, View.TRANSLATION_Y, fromFab ? getPathMotion().getPath(translationX, translationY, 0, 0) : getPathMotion().getPath(0, 0, -translationX, -translationY));
        translate.setDuration(duration);
        translate.setInterpolator(fastOutSlowInInterpolator);

        List<Animator> fadeContents = null;
        if (view instanceof ViewGroup)
		{
            final ViewGroup vg = ((ViewGroup)view);
            fadeContents = new ArrayList<>(vg.getChildCount());
            for (int i = vg.getChildCount() - 1; i >= 0; i--) 
			{
                final View child = vg.getChildAt(i);
                final Animator fade = ObjectAnimator.ofFloat(child, View.ALPHA, fromFab ? 1f : 0f);
                if (fromFab) 
				{
                    child.setAlpha(0f);
                }
                fade.setDuration(twoThirdsDuration);
                fade.setInterpolator(fastOutSlowInInterpolator);
                fadeContents.add(fade);
            }
        }

        final Animator iconFade = ObjectAnimator.ofInt(fabIcon, "alpha", fromFab ? 0 : 255);
        if (!fromFab)
		{
            iconFade.setStartDelay(halfDuration);
        }
        iconFade.setDuration(halfDuration);
        iconFade.setInterpolator(fastOutSlowInInterpolator);
		
		final AnimatorSet transition = new AnimatorSet();
        transition.playTogether(circularReveal, translate, iconFade);
        transition.playTogether(fadeContents);
        if (fromFab) 
		{
            transition.addListener(new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationEnd(Animator animation)
				{
					view.getOverlay().clear();
				}
			});
        }
        return new AnimUtils.NoPauseAnimator(transition);
	}
	
	public static void addExtras(@NonNull Intent intent, @DrawableRes int iconFabResId)
	{
        intent.putExtra(EXTRA_ICON_FAB_RES_ID, iconFabResId);
    }
	
	public static boolean setup(@NonNull Activity activity, @Nullable View targetView)
	{
        final Intent intent = activity.getIntent();
        if (!intent.hasExtra(EXTRA_ICON_FAB_RES_ID)) 
		{
            return false;
        }

        final int icon = intent.getIntExtra(EXTRA_ICON_FAB_RES_ID, -1);
        final CircularRevealTransformEnter sharedEnter = new CircularRevealTransformEnter(icon);
        if (targetView != null)
		{
            sharedEnter.addTarget(targetView);
        }
        activity.getWindow().setSharedElementEnterTransition(sharedEnter);
		
        return true;
    }
}
