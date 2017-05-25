package fr.yoann.dev.preferences.helpers;

import android.support.annotation.ColorInt;

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
}
