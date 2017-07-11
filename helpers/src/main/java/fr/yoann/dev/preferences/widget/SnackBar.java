package fr.yoann.dev.preferences.widget;

import fr.yoann.dev.R;
import fr.yoann.dev.preferences.Preferences;
import fr.yoann.dev.preferences.utils.AnimUtils;
import fr.yoann.dev.preferences.listener.AnimListener;
import fr.yoann.dev.preferences.enum.SnackBarTypePosition;
import fr.yoann.dev.preferences.enum.SnackBarTypeSize;
import fr.yoann.dev.preferences.layout.SnackBarLayout;
import fr.yoann.dev.preferences.widget.ForegroundTextView;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.content.Context;
import android.text.TextUtils;
import android.text.Layout;
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
	private Activity act;
	private SnackBarTypeSize typeOfSnackBar = SnackBarTypeSize.SIZE_LINE;
	private SnackBarTypeSize typeOfElevation = SnackBarTypeSize.SIZE_ELEVATION_NORMAL;
	
	private static TextView snackBarText;
	private static ForegroundTextView snackBarButton;
	private static CharSequence mText;
	private static CharSequence mButtonText;
	private static int mColor;
	private static int mTextColorMessage;
	private static int mTextColorButton;
	private static View.OnClickListener mOnClick;
	private static SnackBar snackBar;
	
	public static final long LENGTH_SHORT = 1500;
	public static final long LENGTH_LONG = 3000;
	
	private SnackBar(Context context)
	{
		super(context);
		act = (Activity)context;
	}
	
	public static SnackBar setColorBackground(int color)
	{
		mColor = color;
		
		return snackBar;
	}

	public static SnackBar setColorBackground(Context context, @ColorRes int resId)
	{
		return setColorBackground(context.getColor(resId));
	}
	
	public static SnackBar setMessage(CharSequence text, SnackBar sn)
	{
		mText = text;
		snackBar = sn;
		if (snackBarText != null)
		{
			snackBarText.setText(mText);
		}
		return sn;
	}

	public static SnackBar setMessage(Context context, @StringRes int resId, SnackBar sn)
	{
		return setMessage(context.getString(resId), sn);
	}
	
	public static SnackBar setMessageColor(int color)
	{
		mTextColorMessage = color;
		
		return snackBar;
	}
	
	public static SnackBar setMessageColor(Context context, @ColorRes int resId)
	{
		return setMessageColor(context.getColor(resId));
	}
	
	public static SnackBar setMessageButton(CharSequence buttonText, SnackBar sn)
	{
		mButtonText = buttonText;
		snackBar = sn;
		if (snackBarButton != null)
		{
			snackBarButton.setText(mButtonText);
		}
		return sn;
	}

	public static SnackBar setMessageButton(Context context, @StringRes int resId, SnackBar sn)
	{
		return setMessageButton(context.getString(resId), sn);
	}
	
	public static SnackBar setMessageButtonColor(int color)
	{
		mTextColorButton = color;
		
		return snackBar;
	}
	
	public static SnackBar setMessageButtonColor(Context context, @ColorRes int resId)
	{
		return setMessageButtonColor(context.getColor(resId));
	}

	public static SnackBar setButtonListener(View.OnClickListener onClick)
	{
		mOnClick = onClick;
		
		return snackBar;
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
	
	private MarginLayoutParams initView(ViewGroup parent)
	{
		final SnackBarLayout layout = (SnackBarLayout)LayoutInflater.from(getContext()).inflate(R.layout.ms__snackbar, this, true);
		layout.setOrientation(LinearLayout.VERTICAL);

		act = (Activity)getContext();
		Resources res = getResources();
		final float density = res.getDisplayMetrics().density;
		
		View content = layout.findViewById(R.id.ms__dismiss);

		MarginLayoutParams params;

		layout.setMinimumHeight(setDimensionPixels(typeOfSnackBar.getMinHeight(), density));
		layout.setBackgroundColor(mColor);
		if (Preferences.isLollipop())
		{
			layout.setElevation(setDimensionPixels(typeOfElevation.getSizeElevation(), density));
		}

		params = createStateMarginLayout(parent, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, SnackBarTypePosition.BOTTOM);

		snackBarText = (TextView)content.findViewById(R.id.ms__text);
		if (!TextUtils.isEmpty(mText))
		{
			snackBarText.setText(mText);
			snackBarText.setTextColor(mTextColorMessage);
			post(new Runnable()
			{
				@Override
				public void run()
				{
					Layout layouts = snackBarText.getLayout();  
					final boolean isMultiLine = layouts.getLineCount() > 1;

					if (isMultiLine)
					{
						layout.setMaxHeight(setDimensionPixels(64, density));
					}
					else
					{
						layout.setMaxHeight(setDimensionPixels(48, density));
					}
				}
			});
		}
		else
		{
			snackBarText.setText("Hello !");
		}

		snackBarButton = (ForegroundTextView)content.findViewById(R.id.ms__button);
		if (!TextUtils.isEmpty(mButtonText))
		{
			snackBarButton.setText(mButtonText);
			snackBarButton.setOnClickListener(mOnClick);
			snackBarButton.setTextColor(mTextColorButton);
			snackBarButton.setBackground(res.getDrawable(R.drawable.button_action));
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
	
	public SnackBar show()
	{
		act = (Activity)getContext();
		
		ViewGroup root = (ViewGroup)act.findViewById(android.R.id.content);
		
		initView(root);
		MarginLayoutParams params = initView(root);
		
		showAnimation(params, root);

		return this;
	}
	
	public SnackBar show(long duration)
	{
		act = (Activity)getContext();
		
		ViewGroup root = (ViewGroup)act.findViewById(android.R.id.content);

		initView(root);
		MarginLayoutParams params = initView(root);

		showAnimation(params, root, duration);

		return this;
	}
	
	public static SnackBar make(Context context, CharSequence text)
	{
		snackBar = new SnackBar(context);
		
		mText = text;
		setColorBackground(Color.parseColor("#FF323232"));
		setMessage(mText, snackBar);
		setMessageColor(context.getResources().getColor(R.color.white));
		
		return snackBar;
	}
	
	public static SnackBar makeText(Context context, @StringRes int resString)
	{
		snackBar = new SnackBar(context);

		setColorBackground(Color.parseColor("#FF323232"));
		setMessage(context, resString, snackBar);
		setMessageColor(context.getResources().getColor(R.color.white));

		return snackBar;
	}
	
	public static SnackBar makeText(Context context, String text)
	{
		snackBar = new SnackBar(context);

		setColorBackground(Color.parseColor("#FF323232"));
		setMessage(text, snackBar);
		setMessageColor(context.getResources().getColor(R.color.white));

		return snackBar;
	}
	
	public static SnackBar makeTextColor(Context context, @StringRes int resString, @ColorRes int resColor)
	{
		snackBar = new SnackBar(context);
		
		setColorBackground(Color.parseColor("#FF323232"));
		setMessage(context, resString, snackBar);
		setMessageColor(context, resColor);
		
		return snackBar;
	}
	
	public static SnackBar makeTextColor(Context context, String text, int color)
	{
		snackBar = new SnackBar(context);

		setColorBackground(Color.parseColor("#FF323232"));
		setMessage(text, snackBar);
		setMessageColor(color);

		return snackBar;
	}
	
	public SnackBar setAction(CharSequence text, View.OnClickListener onClick)
	{
		snackBar = new SnackBar(getContext());
		
		mButtonText = text;
		mOnClick = onClick;
		setColorBackground(Color.parseColor("#FF323232"));
		setMessageButton(mButtonText, snackBar);
		setMessageButtonColor(Preferences.getAttributeColor(getContext(), android.R.attr.colorAccent));
		setButtonListener(mOnClick);
		
		return snackBar;
	}
	
	public SnackBar setAction(@StringRes int resString, View.OnClickListener onClick)
	{
		snackBar = new SnackBar(getContext());
		
		setColorBackground(Color.parseColor("#FF323232"));
		setMessageButton(getContext(), resString, snackBar);
		setMessageButtonColor(Preferences.getAttributeColor(getContext(), android.R.attr.colorAccent));
		setButtonListener(onClick);
		
		return snackBar;
	}
	
	public SnackBar setAction(String text, View.OnClickListener onClick)
	{
		snackBar = new SnackBar(getContext());

		setColorBackground(Color.parseColor("#FF323232"));
		setMessageButton(text, snackBar);
		setMessageButtonColor(Preferences.getAttributeColor(getContext(), android.R.attr.colorAccent));
		setButtonListener(onClick);

		return snackBar;
	}
	
	public SnackBar setActionColor(@StringRes int resString, @ColorRes int resColor, View.OnClickListener onClick)
	{
		snackBar = new SnackBar(getContext());

		setColorBackground(Color.parseColor("#FF323232"));
		setMessageButton(getContext(), resString, snackBar);
		setMessageButtonColor(getContext(), resColor);
		setButtonListener(onClick);

		return snackBar;
	}

	public SnackBar setActionColor(String text, int color, View.OnClickListener onClick)
	{
		snackBar = new SnackBar(getContext());

		setColorBackground(Color.parseColor("#FF323232"));
		setMessageButton(text, snackBar);
		setMessageButtonColor(color);
		setButtonListener(onClick);

		return snackBar;
	}
	
	private void showAnimation(MarginLayoutParams params, ViewGroup parent)
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
		enterAnimation();
	}
	
	private void showAnimation(MarginLayoutParams params, ViewGroup parent, long duration)
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
		enterAnimation(duration);
	}
	
	private void enterAnimation()
	{
		Animation slideEnter = AnimationUtils.loadAnimation(getContext(), AnimUtils.getAnimationEnter(SnackBarTypePosition.BOTTOM));
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
	
	private void enterAnimation(final long duration)
	{
		Animation slideEnter = AnimationUtils.loadAnimation(getContext(), AnimUtils.getAnimationEnter(SnackBarTypePosition.BOTTOM));
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
