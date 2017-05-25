package fr.yoann.dev.preferences.helpers;

import fr.yoann.dev.R;

import android.support.annotation.NonNull;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class PreferencesHelpers
{
	private final Context context;

	private static final String PREFERENCES_NAME = "preferences";
	private static final String APP_VERSION = "app_version";
	
	public PreferencesHelpers(@NonNull Context context)
	{
        this.context = context;
    }

	private SharedPreferences getSharedPreferences() 
	{
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

	// set new Version
	private int getVersion() 
	{
        return getSharedPreferences().getInt(APP_VERSION, 0);
    }

	private void setVersion(int version)
	{
        getSharedPreferences().edit().putInt(APP_VERSION, version).apply();
    }

	public boolean isNewVersion() 
	{
        int version = 0;
        try 
		{
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        }
		catch (PackageManager.NameNotFoundException ignored)
		{ }

        if (version > getVersion())
		{
            setVersion(version);
			return true;
        } 
		else 
		{
            return false;
        }
    }
	// end set new Version
	
	// set Wifi detect
	public boolean checkWiFi()
	{
		ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null)
		{
			NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (info != null)
			{
				if (info.isConnected()) 
				{
					return true;
				}
			}
		}
		return false;
	}
	// end set Wifi detect
}
