package fr.yoann.dev.preferences.utils;

import android.support.annotation.Nullable;
import android.support.annotation.ColorInt;

import android.support.v7.graphics.Palette;

import android.graphics.Bitmap;

import java.util.Comparator;
import java.util.Collections;

public class PaletteUtils
{
	@Nullable
    public static Palette generatePalette(Bitmap bitmap) 
	{
        if (bitmap == null)
		{
			return null;
		}
        return Palette.from(bitmap).generate();
    }

	private static class SwatchComparator implements Comparator<Palette.Swatch> 
	{
        private static SwatchComparator swatchInstance;

        static SwatchComparator getInstance() 
		{
            if (swatchInstance == null) 
			{
                swatchInstance = new SwatchComparator();
            }
            return swatchInstance;
        }

        @Override
        public int compare(Palette.Swatch lhs, Palette.Swatch rhs)
		{
            return lhs.getPopulation() - rhs.getPopulation();
        }
    }

	@ColorInt
    public static int getColor(@Nullable Palette palette, int defaultColor)
	{
        if (palette != null) 
		{
            if (palette.getVibrantSwatch() != null)
			{
                return palette.getVibrantSwatch().getRgb();
            }
			else if (palette.getMutedSwatch() != null) 
			{
                return palette.getMutedSwatch().getRgb();
            } 
			else if (palette.getDarkVibrantSwatch() != null)
			{
                return palette.getDarkVibrantSwatch().getRgb();
            } 
			else if (palette.getDarkMutedSwatch() != null)
			{
                return palette.getDarkMutedSwatch().getRgb();
            } 
			else if (palette.getLightVibrantSwatch() != null) 
			{
                return palette.getLightVibrantSwatch().getRgb();
            } 
			else if (palette.getLightMutedSwatch() != null) 
			{
                return palette.getLightMutedSwatch().getRgb();
            } 
			else if (!palette.getSwatches().isEmpty()) 
			{
                return Collections.max(palette.getSwatches(), SwatchComparator.getInstance()).getRgb();
            }
        }
        return defaultColor;
    }
}
