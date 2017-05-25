package com.msay2.mire.applications;

import android.app.Application;
import android.content.Intent;

import com.msay2.mire.MainActivity;
import com.msay2.mire.utils.ImageConfig;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.res.Configuration;

public class MireApplication extends Application
{
	private static MireApplication getMire;
	
	public static MireApplication getInstance()
	{
		return getMire;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		getMire = this;
		
		if (!ImageLoader.getInstance().isInited())
		{
			ImageLoader.getInstance().init(ImageConfig.getImageLoaderConfiguration(this));
		}
	}
}
