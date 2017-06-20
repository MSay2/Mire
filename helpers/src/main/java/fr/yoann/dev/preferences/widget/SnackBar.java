package fr.yoann.dev.preferences.widget;

import fr.yoann.dev.R;
import fr.yoann.dev.preferences.Preferences;
import fr.yoann.dev.preferences.utils.AnimUtils;
import fr.yoann.dev.preferences.listener.AnimListener;
import fr.yoann.dev.preferences.enum.SnackBarTypePosition;
import fr.yoann.dev.preferences.enum.SnackBarTypeSize;
import fr.yoann.dev.preferences.layout.SnackBarLayout;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;

import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.support.annotation.AnimRes;

public class SnackBar extends SnackBarLayout
{
	private SnackBarTypeSize typeOfSnackBar = SnackBarTypeSize.SIZE_LINE;
	private SnackBarTypeSize typeOfElevation = SnackBarTypeSize.SIZE_ELEVATION_NORMAL;
	private TextView snackBarText;
	private TextView snackBarButton;
	private CharSequence mText;
	private CharSequence mButtonText;
	private int mColor;
	private int mTextColorMessage;
	private int mTextColorButton;
	private View.OnClickListener mOnClick;
	
	public static final long LENGTH_SHORT = 1500;
	public static final long LENGTH_LONG = 3000;
	
	private SnackBar(Context context)
	{
		super(context);
	}
	
	public static SnackBar with(Context context)
	{
		return new SnackBar(context);
	}
	
	public SnackBar setColorBackground(int color)
	{
		mColor = color;
		return this;
	}

	public SnackBar setColorBackgroundSrc(@ColorRes int resId)
	{
		return setColorBackground(getContext().getColor(resId));
	}
	
	public SnackBar setMessage(CharSequence text)
	{
		mText = text;
		if (snackBarText != null)
		{
			snackBarText.setText(mText);
		}
		return this;
	}

	public SnackBar setMessage(@StringRes int resId) 
	{
		return setMessage(getContext().getText(resId));
	}
	
	public SnackBar setMessageColor(int color)
	{
		mTextColorMessage = color;
		return this;
	}
	
	public SnackBar setMessageColorSrc(int resId)
	{
		return setMessageColor(getContext().getColor(resId));
	}
	
	public SnackBar setMessageButton(CharSequence buttonText)
	{
		mButtonText = buttonText;
		if (snackBarButton != null)
		{
			snackBarButton.setText(mButtonText);
		}
		return this;
	}

	public SnackBar setMessageButton(@StringRes int resId)
	{
		return setMessageButton(getContext().getString(resId));
	}
	
	public SnackBar setMessageButtonColor(int color)
	{
		mTextColorButton = color;
		return this;
	}
	
	public SnackBar setMessageButtonColorSrc(@ColorRes int resId)
	{
		return setMessageButtonColor(getContext().getColor(resId));
	}

	public SnackBar setButtonListener(View.OnClickListener onClick)
	{
		mOnClick = onClick;
		return this;
	}
	
	private Runnable dismissRunnable = new Runnable() 
	{
		@Override
		public void run()
		{
			exitAnimation();
		}
	};
	
	private static MarginLayoutParams createStateMarginLayout(ViewGroup viewGroup, int width, int height, SnackBarTypePosition position)
	{
		if (viewGroup instanceof FrameLayout) 
		{
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
			params.gravity = position.getGravity();

			return params;
		} 
		else if (viewGroup instanceof RelativeLayout)
		{
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
			if (position == SnackBarTypePosition.TOP)
			{
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			}
			else
			{
				params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			}
			return params;
		}
		else if (viewGroup instanceof LinearLayout) 
		{
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
			params.gravity = position.getGravity();

			return params;
		}
		else
		{
			throw new IllegalStateException("Un FrameLayout ou un RelativeLayout est requis pour le parent de la SnackBar");
		}
	}
	
	private MarginLayoutParams initView(Context context, Activity activity, ViewGroup parent)
	{
		SnackBarLayout layout = (SnackBarLayout)LayoutInflater.from(context).inflate(R.layout.ms__snackbar, this, true);
		layout.setOrientation(LinearLayout.VERTICAL);

		Resources res = getResources();
		float density = res.getDisplayMetrics().density;
		
		View content = layout.findViewById(R.id.ms__dismiss);

		MarginLayoutParams params;

		layout.setMinimumHeight(setDimensionPixels(typeOfSnackBar.getMinHeight(), density));
		layout.setMaxHeight(setDimensionPixels(typeOfSnackBar.getMaxHeight(), density));
		layout.setMaxWidth(getResources().getDimensionPixelSize(R.dimen.max_width_snackbar));
		layout.setBackgroundColor(mColor);
		if (Preferences.isLollipop())
		{
			layout.setElevation(setDimensionPixels(typeOfElevation.getSizeElevation(), density));
		}

		params = createStateMarginLayout(parent, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, SnackBarTypePosition.BOTTOM);

		snackBarText = (TextView)content.findViewById(R.id.ms__text);
		if (!TextUtils.isEmpty(mText))
		{
			snackBarText.setText(mText);
			snackBarText.setTextColor(mTextColorMessage);
		}
		else
		{
			snackBarText.setText("Hello !");
		}

		snackBarButton = (TextView)content.findViewById(R.id.ms__button);
		if (!TextUtils.isEmpty(mButtonText))
		{
			snackBarButton.setText(mButtonText);
			snackBarButton.setOnClickListener(mOnClick);
			snackBarButton.setTextColor(mTextColorButton);
		}
		else
		{
			snackBarButton.setVisibility(GONE);
		}
		return params;
	}
	
	private static int setDimensionPixels(int dp, float densityMectrics)
	{
		return (int)(dp * densityMectrics + 0.5f);
	}
	
	public SnackBar show(Activity activity)
	{
		ViewGroup root = (ViewGroup)activity.findViewById(android.R.id.content);
		
		initView(activity, activity, root);
		MarginLayoutParams params = initView(activity, activity, root);
		
		showAnimation(activity, params, root);

		return this;
	}
	
	public SnackBar show(Activity activity, long duration)
	{
		ViewGroup root = (ViewGroup)activity.findViewById(android.R.id.content);

		initView(activity, activity, root);
		MarginLayoutParams params = initView(activity, activity, root);

		showAnimation(activity, params, root, duration);

		return this;
	}
	
	private void showAnimation(Activity activity, MarginLayoutParams params, ViewGroup parent)
	{
		parent.removeView(this);
		if (Preferences.isLollipop())
		{
			for (int i = 0; i < parent.getChildCount(); i++)
			{
				View otherChild = parent.getChildAt(i);
				float elvation = otherChild.getElevation();
				if (elvation > getElevation())
				{
					setElevation(elvation);
				}
			}
		}
		parent.addView(this, params);
		bringToFront();
		enterAnimation(activity);
	}
	
	private void showAnimation(Activity activity, MarginLayoutParams params, ViewGroup parent, long duration)
	{
		parent.removeView(this);
		if (Preferences.isLollipop())
		{
			for (int i = 0; i < parent.getChildCount(); i++)
			{
				View otherChild = parent.getChildAt(i);
				float elvation = otherChild.getElevation();
				if (elvation > getElevation())
				{
					setElevation(elvation);
				}
			}
		}
		parent.addView(this, params);
		bringToFront();
		enterAnimation(activity, duration);
	}
	
	private void enterAnimation(Context context)
	{
		Animation slideEnter = AnimationUtils.loadAnimation(context, AnimUtils.getAnimationEnter(SnackBarTypePosition.BOTTOM));
		slideEnter.setAnimationListener(new AnimListener()
		{
			@Override
			public void onAnimationEnd(Animation animation)
			{
				post(new Runnable()
				{
					@Override
					public void run()
					{
						start(LENGTH_SHORT);
					}
				});
			}
		});
		startAnimation(slideEnter);
	}
	
	private void enterAnimation(Context context, final long duration)
	{
		Animation slideEnter = AnimationUtils.loadAnimation(context, AnimUtils.getAnimationEnter(SnackBarTypePosition.BOTTOM));
		slideEnter.setAnimationListener(new AnimListener()
		{
			@Override
			public void onAnimationEnd(Animation animation)
			{
				post(new Runnable()
				{
					@Override
					public void run()
					{
						start(duration);
					}
				});
			}
		});
		startAnimation(slideEnter);
	}
	
	private void exitAnimation()
	{
		final Animation slideExit = AnimationUtils.loadAnimation(getContext(), AnimUtils.getAnimationExit(SnackBarTypePosition.BOTTOM));
		slideExit.setAnimationListener(new AnimListener()
		{
			@Override
			public void onAnimationEnd(Animation animation)
			{
				post(new Runnable()
				{
					@Override
					public void run() 
					{
						deleteView();
					}
				});
			}
		});
		startAnimation(slideExit);
	}
	
	private void start(long duration) 
	{
		postDelayed(dismissRunnable, duration);
	}
	
	private void deleteView()
	{
		clearAnimation();
		ViewGroup parent = (ViewGroup)getParent();
		if (parent != null)
		{
			parent.removeView(this);
		}
	}
}
