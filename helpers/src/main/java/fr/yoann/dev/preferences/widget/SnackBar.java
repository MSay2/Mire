package fr.yoann.dev.preferences.widget;

import fr.yoann.dev.R;
import fr.yoann.dev.preferences.Preferences;
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
	private SnackBarTypeSize typeOfSnackBar = SnackBarTypeSize.MULTI_LINE;
	private SnackBarTypeSize typeOfElevation = SnackBarTypeSize.SIZE_ELEVATION_NORMAL;
	private TextView snackBarText;
	private TextView snackBarButton;
	private CharSequence mText;
	private CharSequence mButtonText;
	private int mColor;
	private int mTextColorMessage;
	private int mTextColorButton;
	private View.OnClickListener mOnClick;
	
	public SnackBar(Context context)
	{
		super(context);
	}
	
	public SnackBar with(Context context)
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
			LinearLayout.LayoutParams params = new LayoutParams(width, height);
			params.gravity = position.getGravity();

			return params;
		}
		else
		{
			throw new IllegalStateException("Un FrameLayout ou RelativeLayout est requis pour le parent de la SnackBar");
		}
	}
	
	private MarginLayoutParams initView(Context context, Activity activity, ViewGroup parent)
	{
		SnackBarLayout layout = (SnackBarLayout)LayoutInflater.from(context).inflate(R.layout.ms__snackbar, this, true);
		layout.setOrientation(LinearLayout.VERTICAL);

		Resources res = getResources();
		float scale = res.getDisplayMetrics().density;

		View inner = layout.findViewById(R.id.ms__dismiss);

		MarginLayoutParams params;

		layout.setMinimumHeight(pxToDp(typeOfSnackBar.getMinHeight(), scale));
		layout.setMaxHeight(pxToDp(typeOfSnackBar.getMaxHeight(), scale));
		layout.setMaxWidth(getResources().getDimensionPixelSize(R.dimen.max_width_snackbar));
		layout.setBackgroundColor(mColor);
		if (Preferences.isLollipop())
		{
			layout.setElevation(pxToDp(typeOfElevation.getSizeElevation(), scale));
		}

		params = createStateMarginLayout(parent, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, SnackBarTypePosition.BOTTOM);

		snackBarText = (TextView)inner.findViewById(R.id.ms__text);
		snackBarText.setText(mText);
		snackBarText.setTextColor(mTextColorMessage);

		snackBarButton = (TextView)inner.findViewById(R.id.ms__button);
		if (!TextUtils.isEmpty(mButtonText))
		{
			snackBarButton.setText(mButtonText);
			snackBarButton.setTextColor(mTextColorButton);
			snackBarButton.setOnClickListener(mOnClick);
		}
		else
		{
			snackBarButton.setVisibility(GONE);
		}
		return params;
	}
	
	private static int pxToDp(int dp, float scale)
	{
		return (int)(dp * scale + 0.5f);
	}
	
	public SnackBar show(Activity activity)
	{
		ViewGroup root = (ViewGroup)activity.findViewById(android.R.id.content);
		
		initView(activity, activity, root);
		MarginLayoutParams params = initView(activity, activity, root);
		
		showAnimation(activity, params, root);

		return this;
	}
	
	public void showAnimation(Activity activity, MarginLayoutParams params, ViewGroup parent)
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
	
	void enterAnimation(Context context)
	{
		Animation slideEnter = AnimationUtils.loadAnimation(context, getAnimationEnter(SnackBarTypePosition.BOTTOM));
		slideEnter.setAnimationListener(new Animation.AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation p1)
			{ }

			@Override
			public void onAnimationEnd(Animation p1)
			{
				post(new Runnable()
				{
					@Override
					public void run()
					{
						start(3000);
					}
				});
			}

			@Override
			public void onAnimationRepeat(Animation p1)
			{ }
		});
		startAnimation(slideEnter);
	}
	
	public void exitAnimation()
	{
		final Animation slideExit = AnimationUtils.loadAnimation(getContext(), getAnimationExit(SnackBarTypePosition.BOTTOM));
		slideExit.setAnimationListener(new Animation.AnimationListener() 
		{
			@Override
			public void onAnimationStart(Animation animation) 
			{ }

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

			@Override
			public void onAnimationRepeat(Animation animation)
			{ }
		});
		startAnimation(slideExit);
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
