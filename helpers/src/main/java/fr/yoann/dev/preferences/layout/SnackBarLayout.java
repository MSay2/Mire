package fr.yoann.dev.preferences.layout;

import android.content.Context;
import android.widget.LinearLayout;
import android.util.AttributeSet;

public class SnackBarLayout extends LinearLayout
{
	private int maxWidth = Integer.MAX_VALUE;
    private int maxHeight = Integer.MAX_VALUE;

    public SnackBarLayout(Context context)
	{
        super(context);
    }

    public SnackBarLayout(Context context, AttributeSet attrs)
	{
        this(context, attrs, 0);
    }

    public SnackBarLayout(Context context, AttributeSet attrs, int defStyle) 
	{
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

		if (maxWidth < width)
		{
            int mode = MeasureSpec.getMode(widthMeasureSpec);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth, mode);
        }

        if (maxHeight < height)
		{
            int mode = MeasureSpec.getMode(heightMeasureSpec);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, mode);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setMaxWidth(int maxWidth)
	{
        this.maxWidth = maxWidth;
        requestLayout();
    }

    public void setMaxHeight(int maxHeight) 
	{
        this.maxHeight = maxHeight;
        requestLayout();
    }
}
