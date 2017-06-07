package com.msay2.mire.glide;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.request.animation.GlideAnimation;

import com.msay2.mire.R;
import com.msay2.mire.glide.palette.BitmapPaletteTarget;
import com.msay2.mire.glide.palette.BitmapPaletteWrapper;
import com.msay2.mire.utils.MirePaletteUtils;

public abstract class MireColorTarget extends BitmapPaletteTarget 
{
    public MireColorTarget(ImageView view)
	{
        super(view);
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable)
	{
        super.onLoadFailed(e, errorDrawable);
      
		onColorReady(getDefaultColor());
    }

    @Override
    public void onResourceReady(BitmapPaletteWrapper resource, GlideAnimation<? super BitmapPaletteWrapper> glideAnimation) 
	{
        super.onResourceReady(resource, glideAnimation);
       
		onColorReady(MirePaletteUtils.getColor(resource.getPalette(), getDefaultColor()));
    }

    protected int getDefaultColor() 
	{
        return R.color.primary;
    }

    public abstract void onColorReady(int color);
}
