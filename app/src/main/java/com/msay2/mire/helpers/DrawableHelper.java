package com.msay2.mire.helpers;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.widget.AppCompatDrawableManager;

public class DrawableHelper
{
	@Nullable
    public static Drawable getTintedDrawable(@NonNull Context context, @DrawableRes int res, @ColorInt int color)
	{
        try 
		{
            Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, res);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
           
			return drawable.mutate();
        } 
		catch (OutOfMemoryError e)
		{
            return null;
        }
    }
}

