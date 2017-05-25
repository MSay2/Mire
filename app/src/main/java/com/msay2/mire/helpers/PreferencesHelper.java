package com.msay2.mire.helpers;

import com.msay2.mire.R;

import android.content.Context;
import android.content.SharedPreferences;

import android.support.annotation.NonNull;

public class PreferencesHelper
{
	private Context context;
	
	private static final String PREFERENCES_NAME = "mire_preferences";
	private static final String KEY_WALLS_DIRECTORY = "wallpaper_directory";
	
	public PreferencesHelper(@NonNull Context context)
	{
        this.context = context;
    }

    private SharedPreferences getSharedPreferences() 
	{
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
	
	void setWallsDirectory(String directory) 
	{
        getSharedPreferences().edit().putString(KEY_WALLS_DIRECTORY, directory).apply();
    }

    public String getWallsDirectory() 
	{
        return getSharedPreferences().getString(KEY_WALLS_DIRECTORY, "");
    }
}
