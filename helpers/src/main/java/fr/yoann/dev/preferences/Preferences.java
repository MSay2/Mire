package fr.yoann.dev.preferences;

import android.os.Build;
import android.os.Environment;
import android.app.Activity;

import fr.yoann.dev.preferences.helpers.PreferencesHelpers;

import android.support.annotation.NonNull;
import android.support.annotation.ColorInt;
import android.support.annotation.AttrRes;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ActivityOptionsCompat;

import android.net.Uri;
import android.text.Spanned;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.view.WindowManager;
import android.view.Window;
import android.view.View;
import android.widget.Toast;

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
	public static final int SDK_INT = Build.VERSION.SDK_INT;

	public static final int M = Build.VERSION_CODES.M;
	public static final int L = Build.VERSION_CODES.LOLLIPOP;
	// end set SDK method
	
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
	// end set intent basic
	
	// set FragmentTransaction
	public void setFragmentTransaction(int id, android.support.v4.app.Fragment fr)
	{
		FragmentTransaction requestFragment = this.fragmentManager.beginTransaction();
		requestFragment.replace(id, fr);
		requestFragment.commit();
	}
	// end set FragmentTransaction
	
	// set intent simple activityOptions
	public static void start(Activity context, Class<?> contextClass)
	{
		context.startActivity(new Intent(context, contextClass), ActivityOptionsCompat.makeSceneTransitionAnimation(context).toBundle());
	}
	
	public static void start(FragmentActivity context, Class<?> contextClass)
	{
		context.startActivity(new Intent(context, contextClass), ActivityOptionsCompat.makeSceneTransitionAnimation(context).toBundle());
	}
	
	public static void start(Context context, Class<?> contextClass)
	{
		context.startActivity(new Intent(context, contextClass), ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context).toBundle());
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
	
	public static void longToast(Context context, Spanned htmlText)
	{
		Toast.makeText(context, htmlText, Toast.LENGTH_LONG).show();
	}

	public static void rapidToast(Context context, Spanned htmlText)
	{
		Toast.makeText(context, htmlText, Toast.LENGTH_SHORT).show();
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
	// end set Full Screen
	
	public static String getExternalStorage()
	{
		return Environment.getExternalStorageDirectory().toString();
	}
}
