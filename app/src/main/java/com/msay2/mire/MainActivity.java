package com.msay2.mire;

import android.os.*;
import android.app.*;
import android.app.FragmentManager;
import android.Manifest;

import android.support.v7.app.AppCompatActivity;

import com.msay2.mire.interfaces.MenuAnimation;
import com.msay2.mire.helpers.TransitionHelper;
import com.msay2.mire.helpers.SetupSnackBarHelper;
import com.msay2.mire.item_data.ItemDataUpdate;

import fr.yoann.dev.preferences.Preferences;

import no.agens.depth.lib.CircularSplashView;
import no.agens.depth.lib.tween.interpolators.ExpoIn;
import no.agens.depth.lib.tween.interpolators.QuintOut;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;

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
import android.util.Log;
import android.net.Uri;

public class MainActivity extends AppCompatActivity 
{
	private Fragment currentFragment;
	private Boolean isConnected = false;
	private ViewGroup menu;
	private FragmentManager fm;
	private AsyncTask<String, Void, Integer> getUpdate;
	private List<ItemDataUpdate> item_update;
	private ItemDataUpdate item;
	private ProgressDialog progressDialog;
	private String storage = Preferences.getExternalStorage();
	
	public boolean isMenuVisible = false;
	
	public static AlertDialog dialog;
	
	public static final int REQUEST_WRITE_STORAGE = 128;
	public static final String SHORTCUT_WALLPAPER = "com.msay2.mire.FRAGMENT_WALLPAPER";
	public static final String SHORTCUT_SETTINGS = "com.msay2.mire.FRAGMENT_SETTINGS";
	public static final String TAG = MainActivity.class.getSimpleName();
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		Preferences.makeAppFullscreen(this, Color.TRANSPARENT);
		if (savedInstanceState == null) 
		{
			if (equalsAction(SHORTCUT_WALLPAPER))
			{
				currentFragment = new FragmentWallpaper();
				getFragmentManager().beginTransaction().add(R.id.fragment_container, currentFragment).commit();
				setupMenu(1);
			}
			else if (equalsAction(SHORTCUT_SETTINGS))
			{
				currentFragment = new FragmentSettings();
				getFragmentManager().beginTransaction().add(R.id.fragment_container, currentFragment).commit();
				setupMenu(3);
			}
			else
			{
				currentFragment = new FragmentHome();
				getFragmentManager().beginTransaction().add(R.id.fragment_container, currentFragment).commit();
				setupMenu(0);
			}
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
		setAutoUpdate();
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
	
	private void setupMenu(int position) 
	{
        menu = (ViewGroup)findViewById(R.id.menu_container);

		int splash = R.drawable.ic_splash;
		int colorAccent = Preferences.getAttributeColor(this, R.attr.colorAccent);
		int button_menu = R.drawable.menu_btn;
		
		addMenuItem(menu, getStringSrc(R.string.fragment_name_home), splash, colorAccent, button_menu, 0);
        addMenuItem(menu, getStringSrc(R.string.fragment_name_wallpaper), splash, colorAccent, button_menu, 1);
		addMenuItem(menu, getStringSrc(R.string.fragment_name_about), splash, colorAccent, button_menu, 2);
		addMenuItem(menu, getStringSrc(R.string.fragment_name_settings), splash, colorAccent, button_menu, 3);
		
		selectMenuItem(position, colorAccent);
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
				else if (menuIndex == 2 && !(currentFragment instanceof FragmentAbout))
				{
                    ((MenuAnimation)currentFragment).exitFromMenu();
                    FragmentAbout fragmentAbout = new FragmentAbout();
                    fragmentAbout.setIntroAnimate(true);
                    goToFragment(fragmentAbout);
                    hideMenu();
                    selectMenuItem(menuIndex, color);
                }
				else if (menuIndex == 3 && !(currentFragment instanceof FragmentSettings))
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
			{
				select(menuItem, color);
			}
            else
			{
				unSelect(menuItem);
			}
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
	
	//this method is for the fun :D
	public String getStringSrc(int id)
	{
		String srcString = getResources().getString(id);
		
		return srcString;
	}
	
	private boolean equalsAction(String string)
	{
		return string.equals(getIntent().getAction());
	}
	
	private void setAutoUpdate()
	{
		if (com.msay2.mire.preferences.Preferences.getPreferences(this).getAutoUpdate())
		{
			isConnected = Preferences.getPreferences(this).checkWiFi();
			if (isConnected)
			{
				obtainsWiFiYes(true);
			}
			else
			{
				obtainsWiFiNo(false);
			}
		}
		else if (com.msay2.mire.preferences.Preferences.getPreferences(this).getNoAutoUpdate())
		{ }
	}
	
	private void obtainsWiFiYes(Boolean status)
	{
		File newFolder = new File(storage + FragmentAbout.folder_mire);
		newFolder.mkdir();

		setNewApkDirectory();
	}

	private void setNewApkDirectory()
	{
		File newFolder = new File(storage + FragmentAbout.folder_mire + FragmentAbout.folder_update);
		newFolder.mkdir();
		if (newFolder != null)
		{
			getVersion();
		}
	}
	
	private void obtainsWiFiNo(Boolean status)
	{ }
	
	private void getVersion()
	{
		getUpdate = new AsyncTask<String, Void, Integer>()
		{
			@Override
			protected void onPreExecute() 
			{
				// Ignored this method -> onPreExecute()
			}

			@Override
			protected Integer doInBackground(String... params)
			{
				Integer result = 0;
				HttpURLConnection urlConnection;
				try
				{
					URL url = new URL(getResources().getString(R.string.link_update_json_file));
					urlConnection = (HttpURLConnection) url.openConnection();

					int statusCode = urlConnection.getResponseCode();
					if (statusCode == 200)
					{
						BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
						StringBuilder response = new StringBuilder();
						String line;

						while ((line = r.readLine()) != null)
						{
							response.append(line);
						}
						try
						{
							JSONObject obj = new JSONObject(response.toString());
							item = new ItemDataUpdate();
							item.setVersionName(obj.optString("versionName"));
							item.setVersionCode(obj.optString("versionCode"));
							item.setUrl(obj.optString("url"));
							JSONArray posts = obj.optJSONArray("release");
							item_update = new ArrayList<>();
							if (posts != null)
							{
								StringBuilder builder = new StringBuilder();

								for (int i = 0; i < posts.length(); ++i) 
								{
									builder.append(posts.getString(i).trim());
									if (i != posts.length() - 1)
									{
										builder.append(System.getProperty("line.separator"));
									}
								}
								item.setRelease(String.format(getResources().getString(R.string.updater_available_description), item.getVersionName().toString()) + "\n" + builder.toString());
							}
							item_update.add(item);
						}
						catch (JSONException e)
						{
							e.printStackTrace();
						}
						result = 1;
					}
					else 
					{
						result = 0;
					}
				} 
				catch (Exception e)
				{
					Log.d(TAG, e.getLocalizedMessage());
				}
				return result;
			}

			@Override
			protected void onPostExecute(Integer result)
			{
				if (result == 1) 
				{
					int version = com.msay2.mire.preferences.Preferences.VERSION_CODE;
					int versionPrimary = 0;
					String text = item.getVersionCode().toString();
					String url = item.getUrl().toString();
					String release = item.getRelease().toString();

					versionPrimary = Integer.parseInt(text);

					if (version < versionPrimary)
					{
						dialogYesUpdate(url, release);
					}
					else
					{ }
				} 
				else 
				{
					fr.yoann.dev.preferences.widget.SnackBar.makeText(MainActivity.this, getStringSrc(R.string.toast_wallpaper_error)).show(fr.yoann.dev.preferences.widget.SnackBar.LENGTH_LONG);
				}
			}

		}.execute();
	}
	
	private void dialogYesUpdate(String url, String content)
	{
		String title = getResources().getString(R.string.updater_not_available_title);
		String btn_download = getResources().getString(R.string.updater_btn_download);
		String btn_back = getResources().getString(R.string.updater_btn_back);
		
		fr.yoann.dev.preferences.Preferences.getDialog(this, title, content, btn_download, btn_back, downloading(url), back);
	}
	
	public DialogInterface.OnClickListener downloading(final String url)
	{
		return new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int position)
			{
				fr.yoann.dev.preferences.Preferences.LISTENER_CLICK();
				new DownloadAsyntask().execute(url);
			}
		};
	};
	
	public DialogInterface.OnClickListener back = new DialogInterface.OnClickListener()
	{
		@Override
		public void onClick(DialogInterface dialog, int position)
		{
			fr.yoann.dev.preferences.Preferences.LISTENER_CLICK();
		}
	};
	
	private ProgressDialog showDialog()
	{
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getResources().getString(R.string.updater_downloading));
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCancelable(false);
		progressDialog.show();

		return progressDialog;
	}
	
	class DownloadAsyntask extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();

			showDialog();
		}

		@Override
		protected String doInBackground(String... aurl)
		{
			int count;
			try
			{
				URL url = new URL(aurl[0]);
				URLConnection conexion = url.openConnection();
				conexion.connect();

				int lenghtOfFile = conexion.getContentLength();
				Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream("/sdcard" + FragmentAbout.folder_mire + FragmentAbout.folder_update + "/mire.apk");

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1) 
				{
					total += count;
					publishProgress("" + (int)((total*100) / lenghtOfFile));
					output.write(data, 0, count);
				}
				output.flush();
				output.close();
				input.close();
			} 
			catch (Exception e)
			{ }

			return null;
		}

		protected void onProgressUpdate(String... progress) 
		{
			Log.d("ANDRO_ASYNC", progress[0]);
			progressDialog.setProgress(Integer.parseInt(progress[0]));
		}

		@Override
		protected void onPostExecute(String unused) 
		{
			progressDialog.dismiss();

			Intent intent = new Intent(Intent.ACTION_VIEW);
			File file = new File("/mnt/sdcard" + FragmentAbout.folder_mire + FragmentAbout.folder_update + "/mire.apk");

			intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			startActivity(intent);
		}
	}
}
