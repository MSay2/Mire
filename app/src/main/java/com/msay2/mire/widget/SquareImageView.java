package com.msay2.mire.widget;

import android.os.Build;
import android.content.Context;
import android.widget.ImageView;
import android.util.AttributeSet;

import android.annotation.TargetApi;

public class SquareImageView extends ImageView 
{
    public SquareImageView(Context context)
	{
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) 
	{
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr)
	{
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) 
	{
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
