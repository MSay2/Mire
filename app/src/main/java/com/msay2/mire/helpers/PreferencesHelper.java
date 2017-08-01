package com.msay2.mire.helpers;

import com.msay2.mire.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.support.annotation.NonNull;

public class PreferencesHelper
{
	private Context context;
	
	private static final String PREFERENCES_NAME = "mire_preferences";
	private static final String KEY_WALLS_DIRECTORY = "wallpaper_directory";
	private static final String KEY_AUTO_UPDATE = "auto_update";
	private static final String KEY_NO_AUTO_UPDATE = "no_auto_update";
	
	public PreferencesHelper(@NonNull Context context)
	{
        this.context = context;
    }

    private SharedPreferences getSharedPreferences() 
	{
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
	
	private SharedPreferences getDefaultSharedPreferences(@NonNull Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	void setWallsDirectory(String directory) 
	{
        getSharedPreferences().edit().putString(KEY_WALLS_DIRECTORY, directory).apply();
    }

    public String getWallsDirectory() 
	{
        return getSharedPreferences().getString(KEY_WALLS_DIRECTORY, "");
    }
	
	public boolean getAutoUpdate()
	{
		return getDefaultSharedPreferences(context).getString(PREFERENCES_NAME, "auto").equals(KEY_AUTO_UPDATE);
	}

	public boolean getNoAutoUpdate()
	{
		return getDefaultSharedPreferences(context).getString(PREFERENCES_NAME, "no_auto").equals(KEY_NO_AUTO_UPDATE);
	}
	
	public boolean enableAutoUpdate()
	{
		SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
		editor.putString(PREFERENCES_NAME, KEY_AUTO_UPDATE);

		return editor.commit();
	}

	public boolean enableNoAutoUpdate()
	{
		SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
		editor.putString(PREFERENCES_NAME, KEY_NO_AUTO_UPDATE);

		return editor.commit();
	}
}
