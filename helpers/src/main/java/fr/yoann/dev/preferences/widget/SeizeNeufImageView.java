package fr.yoann.dev.preferences.widget;

import android.os.Build;
import android.content.Context;
import android.widget.ImageView;
import android.util.AttributeSet;

import android.annotation.TargetApi;

/*
 This class is for a {@ImageView} with 16:9
 */

public class SeizeNeufImageView extends ImageView
{
	public SeizeNeufImageView(Context context)
	{
        super(context);
    }

    public SeizeNeufImageView(Context context, AttributeSet attrs) 
	{
        super(context, attrs);
    }

    public SeizeNeufImageView(Context context, AttributeSet attrs, int defStyleAttr)
	{
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SeizeNeufImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) 
	{
        super(context, attrs, defStyleAttr, defStyleRes);
    }

	@Override 
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = Math.round(width * .5625f);
        setMeasuredDimension(width, height);
    }
}
