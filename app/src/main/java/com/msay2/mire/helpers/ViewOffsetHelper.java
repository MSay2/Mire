package com.msay2.mire.helpers;

import android.support.v4.view.ViewCompat;

import fr.yoann.dev.preferences.utils.AnimUtils;

import android.util.Property;
import android.view.View;

public class ViewOffsetHelper 
{
    public static final Property<ViewOffsetHelper, Integer> OFFSET_Y = new AnimUtils.IntProperty<ViewOffsetHelper>("topAndBottomOffset")
	{
        @Override
        public void setValue(ViewOffsetHelper viewOffsetHelper, int offset) 
		{
            viewOffsetHelper.setTopAndBottomOffset(offset);
        }

        @Override
        public Integer get(ViewOffsetHelper viewOffsetHelper) 
		{
            return viewOffsetHelper.getTopAndBottomOffset();
        }

    };

    private final View mView;

    private int mLayoutTop;
    private int mLayoutLeft;
    private int mOffsetTop;
    private int mOffsetLeft;

    public ViewOffsetHelper(View view)
	{
        mView = view;
    }

    public void onViewLayout() 
	{
        mLayoutTop = mView.getTop();
        mLayoutLeft = mView.getLeft();

        updateOffsets();
    }

    public boolean setTopAndBottomOffset(int absoluteOffset) 
	{
        if (mOffsetTop != absoluteOffset)
		{
            mOffsetTop = absoluteOffset;
            updateOffsets();
            
			return true;
        }
        return false;
    }

    public void offsetTopAndBottom(int relativeOffset)
	{
        mOffsetTop += relativeOffset;
        updateOffsets();
    }

    public boolean setLeftAndRightOffset(int absoluteOffset) 
	{
        if (mOffsetLeft != absoluteOffset)
		{
            mOffsetLeft = absoluteOffset;
            updateOffsets();
            
			return true;
        }
        return false;
    }

    public void offsetLeftAndRight(int relativeOffset) 
	{
        mOffsetLeft += relativeOffset;
        updateOffsets();
    }

    public int getTopAndBottomOffset() 
	{
        return mOffsetTop;
    }

    public int getLeftAndRightOffset()
	{
        return mOffsetLeft;
    }

    public void resyncOffsets()
	{
        mOffsetTop = mView.getTop() - mLayoutTop;
        mOffsetLeft = mView.getLeft() - mLayoutLeft;
    }

    private void updateOffsets() 
	{
        ViewCompat.offsetTopAndBottom(mView, mOffsetTop - (mView.getTop() - mLayoutTop));
        ViewCompat.offsetLeftAndRight(mView, mOffsetLeft - (mView.getLeft() - mLayoutLeft));
    }
}
