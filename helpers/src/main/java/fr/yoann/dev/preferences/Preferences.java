package fr.yoann.dev.preferences;

import android.os.Build;
import android.os.Environment;
import android.app.Activity;

import fr.yoann.dev.R;
import fr.yoann.dev.preferences.helpers.PreferencesHelpers;
import fr.yoann.dev.preferences.widget.SnackBar;

import android.support.annotation.NonNull;
import android.support.annotation.ColorInt;
import android.support.annotation.AttrRes;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ActivityOptionsCompat;

import java.io.File;

import android.net.Uri;
import android.text.Spanned;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.view.WindowManager;
import android.view.Window;
import android.view.View;
import android.widget.Toast;
import android.graphics.Typeface;

public class Preferences
{
	public FragmentManager fragmentManager;
	
	// set Preferences helppers
	public static PreferencesHelpers getPreferences(@NonNull Context context) 
	{
        return new PreferencesHelpers(context);
    }
	// end set Preferences helperd
	
	//set SDK method
	public static final Boolean SDK_INT_N = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
	public static final Boolean SDK_INT_M = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
	public static final Boolean SDK_INT_L = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
	public static final Boolean SDK_INT_K = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	// end set SDK method
	
	// get SDK method
	public static boolean isNougat()
	{
		return SDK_INT_N;
	}
	
	public static boolean isMarshmallow()
	{
		return SDK_INT_M;
	}
	
	public static boolean isLollipop()
	{
		return SDK_INT_L;
	}
	
	public static boolean isKitKat()
	{
		return SDK_INT_K;
	}
	// end get SDK method
	
	// set Attrs Color
	@ColorInt
    public static int getAttributeColor(Context context, @AttrRes int attr)
	{
        return getAttributeColor(context, attr, 0);
    }

	@ColorInt
    public static int getAttributeColor(Context context, @AttrRes int attr, int fallback) 
	{
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[] {attr});
        try 
		{
            return a.getColor(0, fallback);
        }
		finally 
		{
            a.recycle();
        }
    }
	// end set Attrs color
	
	// set intent basic
	public static void intent(Activity activity, Class<?> activityClass)
	{
		Intent intent = new Intent(activity, activityClass);
		activity.startActivity(intent);
	}
	
	public static void intent(FragmentActivity fragment, Class<?> activityClass)
	{
		Intent intent = new Intent(fragment, activityClass);
		fragment.startActivity(intent);
	}
	
	public static void intent(Context context, Class<?> activityClass)
	{
		Intent intent = new Intent(context, activityClass);
		context.startActivity(intent);
	}
	
	public static void intentUri(Activity activity, String url)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		activity.startActivity(intent);
	}
	
	public static void intentUri(Context context, String url)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(intent);
	}
	// end set intent basic
	
	// set FragmentTransaction
	public void setFragmentTransaction(int id, android.support.v4.app.Fragment fr)
	{
		FragmentTransaction requestFragment = fragmentManager.beginTransaction();
		requestFragment.replace(id, fr);
		requestFragment.commit();
	}
	// end set FragmentTransaction
	
	// set intent simple activityOptions
	public static void start(Activity activity, Class<?> activityClass)
	{
		activity.startActivity(new Intent(activity, activityClass), ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle());
	}
	
	public static void start(FragmentActivity fragmentActivity, Class<?> activityClass)
	{
		fragmentActivity.startActivity(new Intent(fragmentActivity, activityClass), ActivityOptionsCompat.makeSceneTransitionAnimation(fragmentActivity).toBundle());
	}
	
	public static void start(Context context, Class<?> activityClass)
	{
		context.startActivity(new Intent(context, activityClass), ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context).toBundle());
	}
	// end set intent simple activityOptions
	
	//set Hide Status Bar
	public static void hideStatusBar(Activity activity)
	{
		WindowManager.LayoutParams winParams;
		Window win;
		int bits;

		win = activity.getWindow();
		winParams = win.getAttributes();
        bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;

		winParams.flags |= bits;
		win.setAttributes(winParams);
	}
	
	public static void showStatusBar(Activity activity)
	{
		WindowManager.LayoutParams winParams;
		Window win;
		int bits;

		win = activity.getWindow();
		winParams = win.getAttributes();
        bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;

		winParams.flags &= ~bits;
		win.setAttributes(winParams);
	}
	// end set Hide Status Bar
	
	// set Toast
	public static void longToast(Context context, String text)
	{
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	public static void rapidToast(Context context, String text)
	{
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	
	public static void timeTost(Context context, String text, int duration)
	{
		Toast.makeText(context, text, duration).show();
	}
	// end setToast
	
	// set Full Screnn
	public static void makeAppFullscreen(Activity activity, int color)
	{
		activity.getWindow().setStatusBarColor(color);
		activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}

	public static void makeAppFullscreenSize(Activity activity, int color)
	{
		activity.getWindow().setStatusBarColor(color);
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}
	
	/*
	 Implented this line in {@Styles} for use this method
	 and completely the view
	 
	 ** <item name="android:windowDrawSystemBarBackground">true</item> **
	*/
	public static void makeViewDrawSystemBarBackground(View target)
	{
		target.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
	}
	// end set Full Screen
	
	// set External directory
	public static String getExternalStorage()
	{
		return Environment.getExternalStorageDirectory().toString();
	}
	// end set External directory
	
	// set New file in your storage
	public static void newFile(String nameNewFile)
	{
		File file = new File(getExternalStorage() + nameNewFile);
		file.mkdir();
	}
	// end set New file in your storage
	
	// set Typeface - create typface from assets
	public static void createTypefaceFromAssets(Context context, String ttfAssets)
	{
		Typeface.createFromAsset(context.getAssets(), ttfAssets);
	}
	
	public static void createTypefaceFromAssets(Activity activity, String ttfAssets)
	{
		Typeface.createFromAsset(activity.getAssets(), ttfAssets);
	}
	
	public static void createTypefaceFromAssets(FragmentActivity fragmentActivity, String ttfAssets)
	{
		Typeface.createFromAsset(fragmentActivity.getAssets(), ttfAssets);
	}
	// end set Typeface - create typface from assets
	
	// set SnackBar theme
	// LIGHT
	public void snackBarLight(Activity activity, int message, int messageButton, View.OnClickListener onClick)
	{
		new SnackBar(activity)
		    .setColorBackground(R.color.background_light)
		    .setMessage(message)
		    .setMessageButton(messageButton)
		    .setMessageColorSrc(R.color.text_color_dark)
		    .setMessageButtonColorSrc(R.color.button_text_light)
		    .setButtonListener(onClick);
	}
	
	// DARK
	public void snackBarDark(Activity activity, int message, int messageButton, View.OnClickListener onClick)
	{
		new SnackBar(activity)
		    .setColorBackground(R.color.background_dark)
		    .setMessage(message)
		    .setMessageButton(messageButton)
		    .setMessageColorSrc(R.color.text_color_light)
		    .setMessageButtonColorSrc(R.color.button_text_dark)
		    .setButtonListener(onClick);
	}
	// end set SnackBar theme
}
