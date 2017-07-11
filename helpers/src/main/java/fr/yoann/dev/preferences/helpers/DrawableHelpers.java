package fr.yoann.dev.preferences.helpers;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.widget.AppCompatDrawableManager;

public class DrawableHelpers
{
	@Nullable
	public static int getDrawableInt(@NonNull Context context, @DrawableRes int res)
	{
		Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, res);
		drawable.mutate();
		
		return res;
	}
	
	@Nullable
	public static Drawable getDrawable(@NonNull Context context, @DrawableRes int res)
	{
		Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, res);
		return drawable.mutate();
	}

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

