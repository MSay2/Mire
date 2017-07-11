package fr.yoann.dev.preferences.glide.palette;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;

import fr.yoann.dev.preferences.utils.PaletteUtils;
import fr.yoann.dev.preferences.glide.palette.BitmapPaletteWrapper;
import fr.yoann.dev.preferences.glide.palette.BitmapPaletteResource;

public class BitmapPaletteTranscoder implements ResourceTranscoder<Bitmap, BitmapPaletteWrapper> 
{
    private final BitmapPool bitmapPool;

    public BitmapPaletteTranscoder(Context context) 
	{
        this(Glide.get(context).getBitmapPool());
    }

    public BitmapPaletteTranscoder(BitmapPool bitmapPool) 
	{
        this.bitmapPool = bitmapPool;
    }

    @Override
    public Resource<BitmapPaletteWrapper> transcode(Resource<Bitmap> bitmapResource) 
	{
        Bitmap bitmap = bitmapResource.get();
        BitmapPaletteWrapper bitmapPaletteWrapper = new BitmapPaletteWrapper(bitmap, PaletteUtils.generatePalette(bitmap));
       
		return new BitmapPaletteResource(bitmapPaletteWrapper, bitmapPool);
    }

    @Override
    public String getId() 
	{
        return "BitmapPaletteTranscoder.com.msay2.mire.glide.palette";
    }
}
