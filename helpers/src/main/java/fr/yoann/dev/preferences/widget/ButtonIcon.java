package fr.yoann.dev.preferences.widget;

import fr.yoann.dev.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.ViewOutlineProvider;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.animation.StateListAnimator;
import android.animation.AnimatorInflater;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import android.support.annotation.StringRes;

public class ButtonIcon extends LinearLayout
{
	private LayoutInflater inflater;
	private View content;
	private ImageView buttonIcon;
	private TextView buttonText;
	private int mColor;
	private int mResource;
	private Drawable mDrawable;
	private CharSequence mText;
	private int mTextColor;
	private float mTextSize;
	private Typeface mTypface;
	private Boolean mState = false;
	private Drawable foreground;
	private String mParseColor;
	private String mParceTextColor;

	public ButtonIcon(Context context)
	{
		super(context);
		initInflater(context);
		initView(context);
	}

	public ButtonIcon(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initInflater(context);
		initView(context);

		final TypedArray typed = context.obtainStyledAttributes(attrs, R.styleable.ButtonIcon);
		final DisplayMetrics dm = getResources().getDisplayMetrics();

		mTextSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, dm);

		final int res = typed.getResourceId(R.styleable.ButtonIcon_android_src, R.drawable.ic_github_logo);
		final CharSequence charS = typed.getText(R.styleable.ButtonIcon_android_text);
		final int color = typed.getColor(R.styleable.ButtonIcon_android_textColor, Color.parseColor("#212121"));
		final int mTextSize = typed.getDimensionPixelSize(R.styleable.ButtonIcon_android_textSize, 14);
		final String stringTypeface = typed.getString(R.styleable.ButtonIcon_android_typeface);
		final Boolean typefaceB = typed.getBoolean(R.styleable.ButtonIcon_isTypeface, mState);
		final int colorB = typed.getColor(R.styleable.ButtonIcon_android_background, Color.parseColor("#CCCCCC"));
		final Drawable f = typed.getDrawable(R.styleable.ButtonIcon_android_foreground);

		setImageResource(res);
		setText(charS);
		setTextColor(color);
		setTextSize(mTextSize);
		if (typefaceB)
		{
			mState = typefaceB;
			mState = true;
			isTypeface(true, Typeface.createFromAsset(context.getAssets(), "fonts/" + stringTypeface + ".ttf"));
		}
		else
		{
			mState = typefaceB;
			mState = false;
		}
		setColorBackground(colorB);
		setForeground(f);

		typed.recycle();
		setOutlineProvider(ViewOutlineProvider.BOUNDS);
	}

	public ButtonIcon(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initInflater(context);
		initView(context);
	}

	public ButtonIcon setColorBackground(int color)
	{
		mColor = color;
		if (this != null)
		{
			getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
		}
		return this;
	}

	public ButtonIcon setColorBackground(String parseColor)
	{
		mParseColor = parseColor;
		if (this != null)
		{
			getBackground().setColorFilter(Color.parseColor(parseColor), PorterDuff.Mode.SRC_ATOP);
		}
		return this;
	}

	public ButtonIcon setImageResource(int resource)
	{
		mResource = resource;
		if (buttonIcon != null)
		{
			buttonIcon.setImageResource(mResource);
		}
		return this;
	}

	public ButtonIcon setImageDrawable(Drawable drawable)
	{
		mDrawable = drawable;
		if (buttonIcon != null)
		{
			buttonIcon.setImageDrawable(mDrawable);
		}
		return this;
	}

	public ButtonIcon setText(CharSequence text)
	{
		mText = text;
		if (buttonText != null)
		{
			buttonText.setText(mText);
		}
		return this;
	}

	public ButtonIcon setText(@StringRes int resId)
	{
		return setText(getContext().getString(resId));
	}

	public ButtonIcon setTextColor(int textColor)
	{
		mTextColor = textColor;
		if (buttonText != null)
		{
			buttonText.setTextColor(textColor);
		}
		return this;
	}

	public ButtonIcon setTextColor(String parseTextColor)
	{
		mParceTextColor = parseTextColor;
		if (buttonText != null)
		{
			buttonText.setTextColor(Color.parseColor(parseTextColor));
		}
		return this;
	}

	public ButtonIcon setTextSize(float textSize)
	{
		mTextSize = textSize;
		if (buttonText != null)
		{
			buttonText.setTextSize(mTextSize);
		}
		return this;
	}

	public ButtonIcon isTypeface(Boolean state, Typeface typeface)
	{
		mState = state;
		mTypface = typeface;
		if (mState)
		{
			setTypeface(mTypface);
		}
		else
		{ }

		return this;
	}

	private ButtonIcon setTypeface(Typeface typeface)
	{
		mTypface = typeface;
		if (buttonText != null)
		{
			buttonText.setTypeface(mTypface);
		}
		return this;
	}

	public void setForeground(Drawable drawable)
	{
        if (foreground != drawable)
		{
            if (foreground != null)
			{
                foreground.setCallback(null);
                unscheduleDrawable(foreground);
            }

            foreground = drawable;
            if (foreground != null)
			{
                foreground.setBounds(getLeft(), getTop(), getRight(), getBottom());
                setWillNotDraw(false);
                foreground.setCallback(this);
                if (foreground.isStateful())
				{
                    foreground.setState(getDrawableState());
                }
            }
			else 
			{
                setWillNotDraw(true);
            }
            invalidate();
        }
    }

	private void initInflater(Context context)
	{
		inflater = LayoutInflater.from(context);
	}

	private void initView(Context context)
	{
		inflater.inflate(R.layout.ms__button, this, true);

		Resources res = getResources();
		StateListAnimator sla = AnimatorInflater.loadStateListAnimator(context, R.animator.button_elevation);

		setBackground(res.getDrawable(R.drawable.button_background));
		setStateListAnimator(sla);
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER);
		setOnClickListener(null);

		content = findViewById(R.id.ms__content);

		buttonIcon = (ImageView)content.findViewById(R.id.ms__button_icon);
		buttonIcon.setImageResource(mResource);
		buttonIcon.setImageDrawable(mDrawable);

		buttonText = (TextView)content.findViewById(R.id.ms__button_text);
		buttonText.setText(mText);
		buttonText.setTextColor(mTextColor);
		buttonText.setTextSize(mTextSize);
		if (mState)
		{
			buttonText.setTypeface(mTypface);
		}
		else
		{ }
	}

	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
        super.onSizeChanged(w, h, oldw, oldh);
        if (foreground != null) 
		{
            foreground.setBounds(0, 0, w, h);
        }
    }

    @Override
    public boolean hasOverlappingRendering() 
	{
        return false;
    }

    @Override
    protected boolean verifyDrawable(Drawable who) 
	{
        return super.verifyDrawable(who) || (who == foreground);
    }

    @Override
    public void jumpDrawablesToCurrentState() 
	{
        super.jumpDrawablesToCurrentState();
        if (foreground != null)
		{
			foreground.jumpToCurrentState();
		}
    }

    @Override
    protected void drawableStateChanged() 
	{
        super.drawableStateChanged();
        if (foreground != null && foreground.isStateful())
		{
            foreground.setState(getDrawableState());
        }
    }

	@Override
    public void draw(Canvas canvas)
	{
        super.draw(canvas);
        if (foreground != null) 
		{
            foreground.draw(canvas);
        }
    }

	@Override
    public void drawableHotspotChanged(float x, float y) 
	{
        super.drawableHotspotChanged(x, y);
        if (foreground != null)
		{
            foreground.setHotspot(x, y);
        }
    }
}
