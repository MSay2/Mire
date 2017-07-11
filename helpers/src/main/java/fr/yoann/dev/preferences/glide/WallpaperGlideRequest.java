package fr.yoann.dev.preferences.glide;

import android.content.Context;
import android.graphics.Bitmap;

import android.support.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;

import fr.yoann.dev.R;
import fr.yoann.dev.preferences.glide.palette.BitmapPaletteWrapper;
import fr.yoann.dev.preferences.glide.palette.BitmapPaletteTranscoder;


public class WallpaperGlideRequest
{
	public static final int DEFAULT_ERROR_IMAGE = R.drawable.ic_github_logo;
    public static final int DEFAULT_ANIMATION = android.R.anim.fade_in;
	
	public static class Builder
	{
		final RequestManager requestManager;
		private String wallpaper;
		private int resId;
		
		public static Builder from(@NonNull RequestManager requestManager, String wallpaper) 
		{
            return new Builder(requestManager, wallpaper);
        }
		
		public static Builder from(@NonNull RequestManager requestManager, int resId)
		{
			return new Builder(requestManager, resId);
		}
		
		private Builder(@NonNull RequestManager requestManager, String wallpaper) 
		{
            this.requestManager = requestManager;
            this.wallpaper = wallpaper;
        }
		
		private Builder(@NonNull RequestManager requestManager, int resId)
		{
			this.requestManager = requestManager;
			this.resId = resId;
		}
		
		public BitmapBuilder asBitmap()
		{
            return new BitmapBuilder(this);
        }
		
		public PaletteBuilder generatePalette(Context context)
		{
            return new PaletteBuilder(this, context);
        }
		
		public static class BitmapBuilder
		{
			private final Builder builder;

			public BitmapBuilder(Builder builder) 
			{
				this.builder = builder;
			}

			public BitmapRequestBuilder<?, Bitmap> build()
			{
				return createBaseRequest(builder.requestManager, builder.wallpaper)
                    .asBitmap()
                    .error(DEFAULT_ERROR_IMAGE)
                    .animate(DEFAULT_ANIMATION);
			}
			
			public BitmapRequestBuilder<?, Bitmap> buildInt()
			{
				return createBaseRequest(builder.requestManager, builder.resId)
                    .asBitmap()
                    .error(DEFAULT_ERROR_IMAGE)
                    .animate(DEFAULT_ANIMATION);
			}
		}
		
		public static class PaletteBuilder 
		{
			final Context context;
			private final Builder builder;

			public PaletteBuilder(Builder builder, Context context) 
			{
				this.builder = builder;
				this.context = context;
			}

			public BitmapRequestBuilder<?, BitmapPaletteWrapper> build()
			{
				return createBaseRequest(builder.requestManager, builder.wallpaper)
                    .asBitmap()
                    .transcode(new BitmapPaletteTranscoder(context), BitmapPaletteWrapper.class)
                    .error(DEFAULT_ERROR_IMAGE)
                    .animate(DEFAULT_ANIMATION);
			}
			
			public BitmapRequestBuilder<?, BitmapPaletteWrapper> buildInt()
			{
				return createBaseRequest(builder.requestManager, builder.resId)
                    .asBitmap()
                    .transcode(new BitmapPaletteTranscoder(context), BitmapPaletteWrapper.class)
                    .error(DEFAULT_ERROR_IMAGE)
                    .animate(DEFAULT_ANIMATION);
			}
		}
	}
	
	public static DrawableTypeRequest createBaseRequest(RequestManager requestManager, String wallpaper)
	{
        return requestManager.load(wallpaper);
    }
	
	public static DrawableTypeRequest createBaseRequest(RequestManager requestManager, int resId)
	{
		return requestManager.load(resId);
	}
}
