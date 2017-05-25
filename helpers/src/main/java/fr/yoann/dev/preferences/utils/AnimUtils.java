package fr.yoann.dev.preferences.utils;

import fr.yoann.dev.R;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Property;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;

import android.support.design.widget.FloatingActionButton;

public class AnimUtils
{
	public static Interpolator fastOutSlowIn;
	public static Interpolator linearOutSlowIn;
	
	public static void _alphaAnimationEnter(@NonNull Context context, @NonNull View view)
	{
		Animation alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_anim_visible);
		
		view.startAnimation(alpha);
		view.setVisibility(View.VISIBLE);
	}
	
	public static void _alphaAnimationEnd(@NonNull Context context, @NonNull View view)
	{
		Animation alpha = AnimationUtils.loadAnimation(context, R.anim.alpha_anim_gone);

		view.startAnimation(alpha);
		view.setVisibility(View.GONE);
	}
	
	public static Interpolator getFastOutSlowInInterpolator(Context context) 
	{
        if (fastOutSlowIn == null) 
		{
            fastOutSlowIn = AnimationUtils.loadInterpolator(context, android.R.interpolator.fast_out_slow_in);
        }
        return fastOutSlowIn;
    }
	
	public static Interpolator getLinearOutSlowInInterpolator(Context context) 
	{
        if (linearOutSlowIn == null)
		{
            linearOutSlowIn = AnimationUtils.loadInterpolator(context, android.R.interpolator.linear_out_slow_in);
        }
        return linearOutSlowIn;
    }
	
	public static void showFab(@Nullable FloatingActionButton fab) 
	{
        if (fab == null)
			return;

        if (ViewCompat.isLaidOut(fab)) 
		{
            fab.show();
            return;
        }

        fab.animate().cancel();
        fab.setScaleX(0f);
        fab.setScaleY(0f);
        fab.setAlpha(0f);
        fab.setVisibility(View.VISIBLE);
        fab.animate().setDuration(200).scaleX(1).scaleY(1).alpha(1).setInterpolator(new LinearOutSlowInInterpolator());
    }

    public static void hideFab(@Nullable FloatingActionButton fab)
	{
        if (fab == null)
			return;

        if (ViewCompat.isLaidOut(fab))
		{
            fab.hide();
            return;
        }

        fab.animate().cancel();
        fab.setScaleX(1f);
        fab.setScaleY(1f);
        fab.setAlpha(1f);
        fab.setVisibility(View.GONE);
        fab.animate().setDuration(200).scaleX(0).scaleY(0).alpha(0).setInterpolator(new LinearOutSlowInInterpolator());
    }
	
	public static void _hideScale(@Nullable Context context, @Nullable View view, @Nullable Animation.AnimationListener listener)
	{
		Animation scale = AnimationUtils.loadAnimation(context, R.anim.scale_out);
		scale.setAnimationListener(listener);
		
		view.startAnimation(scale);
		view.setVisibility(View.GONE);
	}
	
	public static abstract class IntProperty<T> extends Property<T, Integer> 
	{
        public IntProperty(String name) 
		{
            super(Integer.class, name);
        }
		
        public abstract void setValue(T object, int value);

        @Override
        final public void set(T object, Integer value)
		{
            setValue(object, value.intValue());
        }
    }
}
