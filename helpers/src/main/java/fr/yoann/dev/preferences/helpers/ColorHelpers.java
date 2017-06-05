package fr.yoann.dev.preferences.helpers;

import fr.yoann.dev.R;

import android.support.annotation.ColorInt;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;

public class ColorHelpers
{
	public static ColorStateList getColorStateList(int attr, @ColorInt int color, @ColorInt int color2)
	{
        int[][] states = new int[][]
		{
			new int[] {attr},
			new int[] { }
        };
		
        int[] colors = new int[]
		{
			color,
			color2
        };
        return new ColorStateList(states, colors);
    }
	
	public static int getDarkerColor(@ColorInt int color, float transparency)
	{
        float[] hsv = new float[3];
		
        Color.colorToHSV(color, hsv);
       
		hsv[2] *= transparency;
       
		return Color.HSVToColor(hsv);
    }
	
	public static int getBodyTextColor(Context context, @ColorInt int color) 
	{
        double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;

		return (darkness < 0.35) ? context.getResources().getColor(R.color.semi_black) : context.getResources().getColor(R.color.semi_white);
	}
	
	public static int getTitleTextColor(Context context, @ColorInt int color)
	{
		double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;

		return (darkness < 0.35) ? context.getResources().getColor(R.color.black) : context.getResources().getColor(R.color.white);
	}
}
