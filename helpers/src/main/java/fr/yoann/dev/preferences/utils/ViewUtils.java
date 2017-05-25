package fr.yoann.dev.preferences.utils;

import android.view.View;
import android.view.ViewOutlineProvider;
import android.graphics.Outline;

public class ViewUtils
{
	public static final ViewOutlineProvider CIRCULAR_OUTLINE = new ViewOutlineProvider() 
	{
        @Override
        public void getOutline(View view, Outline outline) 
		{
            outline.setOval(view.getPaddingLeft(), view.getPaddingTop(), view.getWidth() - view.getPaddingRight(), view.getHeight() - view.getPaddingBottom());
        }
    };
}
