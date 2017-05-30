package fr.yoann.dev.preferences.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.ViewOutlineProvider;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.view.LayoutInflater;

import fr.yoann.dev.R;

public class MSay2Button extends RelativeLayout 
{
	
    private Drawable foreground;
	
    public MSay2Button(Context context, AttributeSet attrs) 
	{
        super(context, attrs);
		
        TypedArray typed = context.obtainStyledAttributes(attrs, R.styleable.ForegroundView);

        final Drawable d = typed.getDrawable(R.styleable.ForegroundView_android_foreground);
        if (d != null) 
		{
            setForeground(d);
        }
		
        typed.recycle();
        setOutlineProvider(ViewOutlineProvider.BOUNDS);
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

    public Drawable getForeground()
	{
        return foreground;
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
