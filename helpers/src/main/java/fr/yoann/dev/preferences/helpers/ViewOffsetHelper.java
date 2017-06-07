package fr.yoann.dev.preferences.helpers;

import fr.yoann.dev.preferences.utils.AnimUtils;

import android.support.v4.view.ViewCompat;

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

    private final View view;

    private int layoutTop;
    private int layoutLeft;
    private int offsetTop;
    private int offsetLeft;

    public ViewOffsetHelper(View view)
	{
        this.view = view;
    }

    public void onViewLayout() 
	{
        this.layoutTop = this.view.getTop();
        this.layoutLeft = this.view.getLeft();

        updateOffsets();
    }

    public boolean setTopAndBottomOffset(int absoluteOffset) 
	{
        if (this.offsetTop != absoluteOffset)
		{
            this.offsetTop = absoluteOffset;
            updateOffsets();

			return true;
        }
        return false;
    }

    public void offsetTopAndBottom(int relativeOffset)
	{
        this.offsetTop += relativeOffset;
        updateOffsets();
    }

    public boolean setLeftAndRightOffset(int absoluteOffset) 
	{
        if (this.offsetLeft != absoluteOffset)
		{
            this.offsetLeft = absoluteOffset;
            updateOffsets();

			return true;
        }
        return false;
    }

    public void offsetLeftAndRight(int relativeOffset) 
	{
        this.offsetLeft += relativeOffset;
        updateOffsets();
    }

    public int getTopAndBottomOffset() 
	{
        return this.offsetTop;
    }

    public int getLeftAndRightOffset()
	{
        return this.offsetLeft;
    }

    public void resyncOffsets()
	{
        this.offsetTop = this.view.getTop() - this.layoutTop;
        this.offsetLeft = this.view.getLeft() - this.layoutLeft;
    }

    private void updateOffsets() 
	{
        ViewCompat.offsetTopAndBottom(this.view, this.offsetTop - (this.view.getTop() - this.layoutTop));
        ViewCompat.offsetLeftAndRight(this.view, this.offsetLeft - (this.view.getLeft() - this.layoutLeft));
    }
}
