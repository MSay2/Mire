package com.msay2.mire.preferences;

import android.app.*;
import android.os.*;

import com.msay2.mire.R;
import com.msay2.mire.helpers.PreferencesHelper;

import android.support.annotation.NonNull;

import android.content.Context;

public class Preferences
{
	public static int VERSION_CODE = 4;
	public static String VERSION_NAME = "4.0";
	
	public static PreferencesHelper getPreferences(@NonNull Context context) 
	{
        return new PreferencesHelper(context);
    }
}
