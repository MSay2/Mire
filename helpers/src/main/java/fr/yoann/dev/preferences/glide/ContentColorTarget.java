package fr.yoann.dev.preferences.glide;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.request.animation.GlideAnimation;

import fr.yoann.dev.R;
import fr.yoann.dev.preferences.glide.palette.BitmapPaletteTarget;
import fr.yoann.dev.preferences.glide.palette.BitmapPaletteWrapper;
import fr.yoann.dev.preferences.utils.PaletteUtils;

/*
 @Author by Karim Abou Zeid
 
 **Thanks for the {@Palette} color and {@Glide} configuration**
*/

public abstract class ContentColorTarget extends BitmapPaletteTarget 
{
    public ContentColorTarget(ImageView view)
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
       
		onColorReady(PaletteUtils.getColor(resource.getPalette(), getDefaultColor()));
    }

    protected int getDefaultColor() 
	{
        return Color.parseColor("#009688");
    }

    public abstract void onColorReady(int color);
}
