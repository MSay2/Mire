package fr.yoann.dev.preferences.utils;

import fr.yoann.dev.R;
import fr.yoann.dev.preferences.enum.SnackBarTypePosition;

import android.os.*;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.AnimRes;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.util.FloatProperty;
import android.util.Property;
import android.util.ArrayMap;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;

import android.support.design.widget.FloatingActionButton;

import java.util.ArrayList;

public class AnimUtils
{
	private AnimUtils()
	{ }
	
	public static Interpolator fastOutSlowIn;
	public static Interpolator linearOutSlowIn;
	public static Interpolator fastOutLinearIn;
	
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
	
	public static Interpolator getFastOutLinearInInterpolator(Context context) 
	{
        if (fastOutLinearIn == null) 
		{
            fastOutLinearIn = AnimationUtils.loadInterpolator(context, android.R.interpolator.fast_out_linear_in);
        }
        return fastOutLinearIn;
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
	
	@AnimRes
	public static int getAnimationEnter(SnackBarTypePosition snackbarPosition)
	{
		return snackbarPosition == SnackBarTypePosition.TOP ? R.anim.slide_exit : R.anim.slide_enter;
	}

	@AnimRes
	public static int getAnimationExit(SnackBarTypePosition snackbarPosition) 
	{
		return snackbarPosition == SnackBarTypePosition.TOP ? R.anim.slide_enter : R.anim.slide_exit;
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
	
	public static abstract class FloatProp<T> 
	{
        public final String name;

        protected FloatProp(String name) 
		{
            this.name = name;
        }

        public abstract void set(T object, float value);
        public abstract float get(T object);
    }
	
	public static abstract class IntProp<T> 
	{
        public final String name;

        public IntProp(String name) 
		{
            this.name = name;
        }

        public abstract void set(T object, int value);
        public abstract int get(T object);
    }

    public static <T> Property<T, Float> createFloatProperty(final FloatProp<T> impl) 
	{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
		{
            return new FloatProperty<T>(impl.name)
			{
                @Override
                public Float get(T object)
				{
                    return impl.get(object);
                }

                @Override
                public void setValue(T object, float value)
				{
                    impl.set(object, value);
                }
            };
        } 
		else 
		{
            return new Property<T, Float>(Float.class, impl.name) 
			{
                @Override
                public Float get(T object)
				{
                    return impl.get(object);
                }

                @Override
                public void set(T object, Float value)
				{
                    impl.set(object, value);
                }
            };
        }
    }
	
	public static <T> Property<T, Integer> createIntProperty(final IntProp<T> impl)
	{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) 
		{
            return new IntProperty<T>(impl.name)
			{
                @Override
                public Integer get(T object)
				{
                    return impl.get(object);
                }

                @Override
                public void setValue(T object, int value) 
				{
                    impl.set(object, value);
                }
            };
        } 
		else
		{
            return new Property<T, Integer>(Integer.class, impl.name)
			{
                @Override
                public Integer get(T object)
				{
                    return impl.get(object);
                }

                @Override
                public void set(T object, Integer value)
				{
                    impl.set(object, value);
                }
            };
        }
    }
	
	public static class NoPauseAnimator extends Animator 
	{
        private final Animator animator;
        private final ArrayMap<AnimatorListener, AnimatorListener> listeners =
		new ArrayMap<AnimatorListener, AnimatorListener>();

        public NoPauseAnimator(Animator animator)
		{
            this.animator = animator;
        }

        @Override
        public void addListener(AnimatorListener listener)
		{
            Animator.AnimatorListener wrapper = new AnimatorListenerWrapper(this, listener);
            if (!listeners.containsKey(listener))
			{
                listeners.put(listener, wrapper);
                animator.addListener(wrapper);
            }
        }

        @Override
        public void cancel() 
		{
            animator.cancel();
        }

        @Override
        public void end()
		{
           animator.end();
        }

        @Override
        public long getDuration() 
		{
            return animator.getDuration();
        }

        @Override
        public TimeInterpolator getInterpolator() 
		{
            return animator.getInterpolator();
        }

        @Override
        public void setInterpolator(TimeInterpolator timeInterpolator) 
		{
            animator.setInterpolator(timeInterpolator);
        }

        @Override
        public ArrayList<AnimatorListener> getListeners()
		{
            return new ArrayList<AnimatorListener>(listeners.keySet());
        }

        @Override
        public long getStartDelay() 
		{
            return animator.getStartDelay();
        }

        @Override
        public void setStartDelay(long delayMS)
		{
            animator.setStartDelay(delayMS);
        }

        @Override
        public boolean isPaused()
		{
            return animator.isPaused();
        }

        @Override
        public boolean isRunning()
		{
            return animator.isRunning();
        }

        @Override
        public boolean isStarted()
		{
            return animator.isStarted();
        }

        /* We don't want to override pause or resume methods because we don't want them
         * to affect mAnimator.
		 public void pause();

		 public void resume();

		 public void addPauseListener(AnimatorPauseListener listener);

		 public void removePauseListener(AnimatorPauseListener listener);
		 */

        @Override
        public void removeAllListeners()
		{
            listeners.clear();
            animator.removeAllListeners();
        }

        @Override
        public void removeListener(AnimatorListener listener)
		{
            AnimatorListener wrapper = listeners.get(listener);
            if (wrapper != null)
			{
                listeners.remove(listener);
                animator.removeListener(wrapper);
            }
        }

        @Override
        public Animator setDuration(long durationMS) 
		{
            animator.setDuration(durationMS);
            return this;
        }

        @Override
        public void setTarget(Object target)
		{
            animator.setTarget(target);
        }

        @Override
        public void setupEndValues() 
		{
            animator.setupEndValues();
        }

        @Override
        public void setupStartValues() 
		{
            animator.setupStartValues();
        }

        @Override
        public void start()
		{
            animator.start();
        }
    }
	
	static class AnimatorListenerWrapper implements Animator.AnimatorListener 
	{
        private final Animator animator;
        private final Animator.AnimatorListener listener;

        public AnimatorListenerWrapper(Animator animator, Animator.AnimatorListener listener) 
		{
            this.animator = animator;
            this.listener = listener;
        }

        @Override
        public void onAnimationStart(Animator animator) 
		{
            listener.onAnimationStart(animator);
        }

        @Override
        public void onAnimationEnd(Animator animator)
		{
            listener.onAnimationEnd(animator);
        }

        @Override
        public void onAnimationCancel(Animator animator) 
		{
            listener.onAnimationCancel(animator);
        }

        @Override
        public void onAnimationRepeat(Animator animator) 
		{
            listener.onAnimationRepeat(animator);
        }
    }
}
