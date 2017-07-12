package fr.yoann.dev.preferences.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.graphics.Outline;

import android.support.annotation.NonNull;

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
