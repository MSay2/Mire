package com.msay2.mire.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.msay2.mire.R;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;

public class ImageConfig 
{
    public static ImageLoaderConfiguration getImageLoaderConfiguration(@NonNull Context context) 
	{
        ImageLoaderConfiguration imageLoaderConfig = new ImageLoaderConfiguration.Builder(context)
		    .diskCacheSize(200 * 1024 * 1024)
		    .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
		    .memoryCacheSize(2 * 2014 * 1024)
		    .threadPriority(Thread.NORM_PRIORITY - 2)
		    .threadPoolSize(4)
		    .tasksProcessingOrder(QueueProcessingType.FIFO)
		    .imageDownloader(new ImageDownloader(context))
		    .diskCache(new UnlimitedDiskCache(new File(context.getCacheDir().toString() + "/uil-images")))
		    .build();

		return imageLoaderConfig;
    }

    public static DisplayImageOptions.Builder getRawDefaultImageOptions()
	{
        DisplayImageOptions.Builder options = new DisplayImageOptions.Builder();
        options.delayBeforeLoading(10).bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY);

		return options;
    }

	public static DisplayImageOptions getWallpaperOptions() {
        DisplayImageOptions.Builder options = new DisplayImageOptions.Builder();
        options.delayBeforeLoading(10)
			.bitmapConfig(Bitmap.Config.ARGB_8888)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
			.cacheOnDisk(false)
			.cacheInMemory(false);
        return options.build();
    }

	public static DisplayImageOptions getDefaultImageOptions(boolean cacheOnDisk)
	{
        DisplayImageOptions.Builder options = new DisplayImageOptions.Builder();
        options.delayBeforeLoading(10)
		    .resetViewBeforeLoading(true)
		    .bitmapConfig(Bitmap.Config.RGB_565)
		    .imageScaleType(ImageScaleType.EXACTLY)
		    .displayer(new FadeInBitmapDisplayer(700))
		    .cacheOnDisk(cacheOnDisk)
		    .cacheInMemory(false);

        return options.build();
    }

	public static ImageSize getTargetSize(@NonNull Context context) {
        int quality = 4;
        if (quality <= 0) quality = 1;
        return new ImageSize((50 * quality), (50 * quality));
    }
}

