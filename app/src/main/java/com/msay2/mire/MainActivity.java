package com.msay2.mire;

import android.os.*;
import android.app.*;
import android.app.FragmentManager;
import android.Manifest;

import android.support.v7.app.AppCompatActivity;

import com.msay2.mire.interfaces.MenuAnimation;
import com.msay2.mire.helpers.TransitionHelper;
import com.msay2.mire.helpers.SetupSnackBarHelper;

import fr.yoann.dev.preferences.Preferences;

import no.agens.depth.lib.CircularSplashView;
import no.agens.depth.lib.tween.interpolators.ExpoIn;
import no.agens.depth.lib.tween.interpolators.QuintOut;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.graphics.Color;
import android.graphics.BitmapFactory;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Button;

public class MainActivity extends AppCompatActivity 
{
	private Fragment currentFragment;
	private Boolean isConnected = false;
	private ViewGroup menu;
	private FragmentManager fm;
	
	public boolean isMenuVisible = false;
	
	public static AlertDialog dialog;
	
	public static final int REQUEST_WRITE_STORAGE = 128;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		Preferences.makeAppFullscreen(this, Color.TRANSPARENT);
		if (savedInstanceState == null) 
		{
            currentFragment = new FragmentHome();
            getFragmentManager().beginTransaction().add(R.id.fragment_container, currentFragment).commit();
        }
		
		Boolean setup_intro = getSharedPreferences("RE_INTRO", Context.MODE_PRIVATE).getBoolean("re_introduction", true);
		if(setup_intro)
		{
			setupIntroduction();
			getSharedPreferences("RE_INTRO", Context.MODE_PRIVATE).edit().putBoolean("re_introduction", false).commit();
		}
		
		fm = getFragmentManager();
		
		if (Preferences.getPreferences(this).isNewVersion())
		{
			DialogFragmentChangelog.showChangelog(fm);
		}
		
		setupMenu();
		SetupSnackBarHelper.setupSnackBar(this, getStringSrc(R.string.snackbar_content_text_app), getStringSrc(R.string.snackbar_button_text_app), snackbar_clicklistener);
	}
	
	public void setCurretMenuIndex(int curretMenuIndex) 
	{
        this.curretMenuIndex = curretMenuIndex;
    }
	
	int curretMenuIndex = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_MENU) 
		{
			if (!((MainActivity)this).isMenuVisible)
			{
				((MainActivity)this).showMenu();
			}
			else
			{
				((MainActivity)this).onBackPressed();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
    public void onBackPressed()
	{
        if (isMenuVisible)
		{
            hideMenu();
            ((MenuAnimation)currentFragment).revertFromMenu();
        }
		else
		{
			if (SetupSnackBarHelper.isSnackBarVisible)
			{
				SetupSnackBarHelper.hideSnackBarAndFinish(MainActivity.this);
			}
			else
			{
				SetupSnackBarHelper.showSnackBar();
			}
		}
    }
	
	private void setupIntroduction()
	{
		Intent intent = new Intent(MainActivity.this, ActivityIntro.class);
		startActivity(intent);
	}
	
	public void showMenu() 
	{
        isMenuVisible = true;
        ObjectAnimator translationY = ObjectAnimator.ofFloat(menu, View.TRANSLATION_Y, menu.getHeight(), 0);
        translationY.setDuration(1000);
        translationY.setInterpolator(new QuintOut());
        translationY.setStartDelay(150);
        translationY.start();
        selectMenuItem(curretMenuIndex, ((TextView)menu.getChildAt(curretMenuIndex).findViewById(R.id.item_text)).getCurrentTextColor());
        ((MenuAnimation)currentFragment).animateTOMenu();
    }
	
	public void hideMenu()
	{
        isMenuVisible = false;
        ObjectAnimator translationY = ObjectAnimator.ofFloat(menu, View.TRANSLATION_Y, menu.getHeight());
        translationY.setDuration(750);
        translationY.setInterpolator(new ExpoIn());
        translationY.start();
	}
	
	private void setupMenu() 
	{
        menu = (ViewGroup)findViewById(R.id.menu_container);

		int colorAccent = getResources().getColor(R.color.accent);

		addMenuItem(menu, getStringSrc(R.string.fragment_name_home), R.drawable.ic_splash, colorAccent, R.drawable.menu_btn, 0);
        addMenuItem(menu, getStringSrc(R.string.fragment_name_wallpaper), R.drawable.ic_splash, colorAccent, R.drawable.menu_btn, 1);
		addMenuItem(menu, getStringSrc(R.string.fragment_name_settings), R.drawable.ic_splash, colorAccent, R.drawable.menu_btn, 2);
        selectMenuItem(0, colorAccent);
        menu.setTranslationY(20000);
    }
	
	private void addMenuItem(ViewGroup menu, String text, int drawableResource, int splashColor, int menu_btn, int menuIndex) 
	{
        ViewGroup item = (ViewGroup)LayoutInflater.from(this).inflate(R.layout.menu_item, menu, false);
        ((TextView)item.findViewById(R.id.item_text)).setText(text);
        CircularSplashView ic = (CircularSplashView)item.findViewById(R.id.circle);
        ic.setSplash(BitmapFactory.decodeResource(getResources(), drawableResource));
        ic.setSplashColor(splashColor);
        item.setOnClickListener(getMenuItemCLick(menuIndex, splashColor));
        if (menuIndex == 0) 
		{
            menu.addView(item, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

		else if (menuIndex == 3) 
		{
            menu.addView(item, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
		else
            menu.addView(item, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        item.setBackground(getResources().getDrawable(menu_btn, null));

    }
	
	private View.OnClickListener getMenuItemCLick(final int menuIndex, final int color)
	{
        return new View.OnClickListener() 
		{
            @Override
            public void onClick(View v) 
			{
                if (menuIndex == curretMenuIndex)
                    onBackPressed();
					
                else if (menuIndex == 0 && !(currentFragment instanceof FragmentHome))
				{
                    ((MenuAnimation)currentFragment).exitFromMenu();
                    FragmentHome fragmentHome = new FragmentHome();
                    fragmentHome.setIntroAnimate(true);
                    goToFragment(fragmentHome);
                    hideMenu();
                    selectMenuItem(menuIndex, color);
                } 
				else if (menuIndex == 1 && !(currentFragment instanceof FragmentWallpaper))
				{
					if(Preferences.SDK_INT >= Preferences.M)
					{
						if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) 
						{
							if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) 
							{
								requestPermissions();
							} 

							else
							{
								setDialogPermission();
							}
						}
						else
						{
							setStateWiFi(menuIndex, color);
						}
					}
                }
				else if (menuIndex == 2 && !(currentFragment instanceof FragmentSettings))
				{
                    ((MenuAnimation)currentFragment).exitFromMenu();
                    FragmentSettings fragmentSettings = new FragmentSettings();
                    fragmentSettings.setIntroAnimate(true);
                    goToFragment(fragmentSettings);
                    hideMenu();
                    selectMenuItem(menuIndex, color);
                }
            }
        };
    }
	
	private void selectMenuItem(int menuIndex, int color) 
	{
        for (int i = 0; i < menu.getChildCount(); i++) 
		{
            View menuItem = menu.getChildAt(i);
            if (i == menuIndex)
                select(menuItem, color);
            else
                unSelect(menuItem);
        }
        curretMenuIndex = menuIndex;
    }

    private void unSelect(View menuItem) 
	{
        final View circle = menuItem.findViewById(R.id.circle);
        circle.animate().scaleX(0).scaleY(0).setDuration(150).withEndAction(new Runnable()
		{
			@Override
			public void run() 
			{
				circle.setVisibility(View.INVISIBLE);
			}
		}).start();
        fadeColoTo(Color.BLACK, (TextView)menuItem.findViewById(R.id.item_text));
    }

    private void fadeColoTo(int newColor, TextView view)
	{
        ObjectAnimator color = ObjectAnimator.ofObject(view, "TextColor", new ArgbEvaluator(), view.getCurrentTextColor(), newColor);
        color.setDuration(200);
        color.start();
    }

    private void select(View menuItem, int color)
	{
        final CircularSplashView circle = (CircularSplashView) menuItem.findViewById(R.id.circle);
        circle.setScaleX(1f);
        circle.setScaleY(1f);
        circle.setVisibility(View.VISIBLE);
        circle.introAnimate();
        fadeColoTo(color, (TextView)menuItem.findViewById(R.id.item_text));
    }

    public void goToFragment(final Fragment newFragment) 
	{
        getFragmentManager().beginTransaction().add(R.id.fragment_container, newFragment).commit();
        final Fragment removeFragment = currentFragment;
        currentFragment = newFragment;
        getWindow().getDecorView().postDelayed(new Runnable() 
		{
			@Override
			public void run()
			{
				getFragmentManager().beginTransaction().remove(removeFragment).commit();
			}
		}, 2000);
    }
	
	private void setStateWiFi(int menuIndex, int color)
	{
		isConnected = Preferences.getPreferences(MainActivity.this).checkWiFi();
		if(isConnected)
		{
			setFragmentWallpaper(true, menuIndex, color);
		}
		else
		{
			noObtainsWiFi(MainActivity.this, false);
		}
	}
	
	private void setFragmentWallpaper(Boolean status, int menuIndex, int color)
	{
		((MenuAnimation)currentFragment).exitFromMenu();
		FragmentWallpaper fragmentWallpeper = new FragmentWallpaper();
		fragmentWallpeper.setIntroAnimate(true);
		goToFragment(fragmentWallpeper);
		hideMenu();
		selectMenuItem(menuIndex, color);
	}
	
	private void noObtainsWiFi(Context context, Boolean status)
	{
		View view = getLayoutInflater().inflate(R.layout.dialog_update, null);

		TextView title = (TextView)view.findViewById(R.id.id_title);
		TextView content = (TextView)view.findViewById(R.id.id_content_text);

		title.setText(getStringSrc(R.string.no_wifi_access));
		content.setText(getStringSrc(R.string.no_wifi_access_prompt));

		AlertDialog.Builder builder = new AlertDialog.Builder(context)
		    .setView(view)
		    .setPositiveButton(getStringSrc(R.string.updater_btn_ok), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface view, int position)
				{ }
			});

		dialog = builder.create();
		dialog.show();
	}
	
	public View.OnClickListener snackbar_clicklistener = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			SetupSnackBarHelper.hideSnackBar();
		}
	};
	
	private void setDialogPermission()
	{
		View view = getLayoutInflater().inflate(R.layout.dialog_update, null);
		
		TextView title = (TextView)view.findViewById(R.id.id_title);
		TextView content = (TextView)view.findViewById(R.id.id_content_text);

		title.setText(getResources().getString(R.string.request_permissions_title));
		content.setText(getResources().getString(R.string.request_permissions_prompt));
		
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
		    .setView(view)
		    .setPositiveButton(getStringSrc(R.string.button_text_yes), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int position)
				{
					requestPermissions();
				}
			})
		    .setNegativeButton(getStringSrc(R.string.button_text_back), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int position)
				{ }
			});
			
		dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
	}
	
	public void requestPermissions()
	{
		if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) 
		{
			if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) 
			{
				setDialogReshowRequestPermission(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
			} 

			else
			{
				ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
			}
		}
		else
		{ }
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
	{
		switch (requestCode) 
		{
			case REQUEST_WRITE_STORAGE: 
			{
				for (int i = 0; i < permissions.length; i++)
				{
					if (permissions[i].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED)
					{
						Preferences.longToast(MainActivity.this, getResources().getString(R.string.toast_permission_close_app));
					}
				}
			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
	
	public static void setDialogReshowRequestPermission(final Activity activity, final String[] permissons)
	{
		View view = activity.getLayoutInflater().inflate(R.layout.dialog_update, null);

		TextView title = (TextView)view.findViewById(R.id.id_title);
		TextView content = (TextView)view.findViewById(R.id.id_content_text);

		title.setText(activity.getResources().getString(R.string.reshow_request_permissions_title));
		content.setText(activity.getResources().getString(R.string.reshow_request_permissions_prompt));
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity)
		    .setView(view)
		    .setPositiveButton(activity.getResources().getString(R.string.button_text_yes), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int position)
				{
					ActivityCompat.requestPermissions(activity, permissons, REQUEST_WRITE_STORAGE);
				}
			})
		    .setNegativeButton(activity.getResources().getString(R.string.button_text_back), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int position)
				{ }
			});

		dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
	}
	
	//this method is for happy :D
	public String getStringSrc(int id)
	{
		String srcString = getResources().getString(id);
		
		return srcString;
	}
}
