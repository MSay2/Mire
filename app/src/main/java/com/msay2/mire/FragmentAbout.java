package com.msay2.mire;

import android.os.*;
import android.app.*;
import android.Manifest;

import fr.yoann.dev.preferences.Preferences;

import com.msay2.mire.interfaces.MenuAnimation;
import com.msay2.mire.helpers.TransitionHelperAbout;
import com.msay2.mire.adapter.AdapterDialogLib;
import com.msay2.mire.item_data.ItemDataDialogLib;
import com.msay2.mire.item_data.ItemDataUpdate;

import no.agens.depth.lib.MaterialMenuDrawable;
import no.agens.depth.lib.DepthFAB;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.graphics.Color;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentAbout extends Fragment implements MenuAnimation
{
	private boolean introAnimate;
	private View root;
	private ImageView menu;
	private LinearLayout contact, lib, intro, update;
	private MaterialMenuDrawable menuIcon;
	private AlertDialog dialog;
	private TextView update_title;
	private AsyncTask<String, Void, Integer> getUpdate;
	private List<ItemDataUpdate> item_update;
	private ItemDataUpdate item;
	private ProgressDialog progressDialog;
	private Preferences pref;
	private Boolean isConnected = false;
	private String storage = Preferences.getExternalStorage();
	
	public static final int TRANSFORM_DURATION = 900;
	public static final int REQUEST_WRITE_STORAGE = 128;
	public static final String TAG = FragmentAbout.class.getSimpleName();
	public static final String folder_mire = "/Mire";
	public static final String folder_update = "/Mise à jour";
	
	public FragmentAbout()
	{ }

	public void setIntroAnimate(boolean introAnimate)
	{
        this.introAnimate = introAnimate;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		root = inflater.inflate(R.layout.fragment_about, null);
		
		introAnimate();
		setupMenuButton();
		setupIntentContact();
		setupDialogLib();
		setupReviewIntro();
		setupUpdate();

		((MainActivity)getActivity()).setCurretMenuIndex(2);

		return root;
	}

	private void introAnimate()
	{
        if (introAnimate)
		{
			root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
			{
				@Override
				public void onGlobalLayout()
				{
					root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					
					TransitionHelperAbout.startIntroAnim(root, showShadowListener);
					showShadow();
				}
			});
		}
    }

	AnimatorListenerAdapter showShadowListener = new AnimatorListenerAdapter()
	{
        @Override
        public void onAnimationEnd(Animator animation) 
		{
            super.onAnimationEnd(animation);
			hideShadow();
        }
    };
	
	private void hideShadow()
	{
        View actionbarShadow = root.findViewById(R.id.actionbar_shadow);
        actionbarShadow.setVisibility(View.GONE);
    }

    private void showShadow() 
	{
        View actionbarShadow = root.findViewById(R.id.actionbar_shadow);
        actionbarShadow.setVisibility(View.VISIBLE);

		ObjectAnimator.ofFloat(actionbarShadow, View.ALPHA, 0, 0.8f).setDuration(400).start();
    }

	private void setupMenuButton() 
	{
        menu = (ImageView)root.findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!((MainActivity)getActivity()).isMenuVisible)
				{
					((MainActivity)getActivity()).showMenu();
				}
				else
				{
					((MainActivity)getActivity()).onBackPressed();
				}
			}
		});
        menuIcon = new MaterialMenuDrawable(getActivity(), Color.WHITE, MaterialMenuDrawable.Stroke.THIN, TRANSFORM_DURATION);
        menu.setImageDrawable(menuIcon);
    }
	
	private void setupIntentContact()
	{
		contact = (LinearLayout)root.findViewById(R.id.id_contact);
		contact.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Preferences.start(getActivity(), ActivityContact.class);
			}
		});
	}
	
	private void setupDialogLib()
	{
		lib = (LinearLayout)root.findViewById(R.id.id_lib);
		lib.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Preferences.start(getActivity(), BottomSheetLib.class);
			}
		});
	}
	
	private void setupReviewIntro()
	{
		intro = (LinearLayout)root.findViewById(R.id.id_intro);
		intro.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Preferences.start(getActivity(), ActivityIntro.class);
			}
		});
	}
	
	private void setupUpdate()
	{
		String title = String.format(((MainActivity)getActivity()).getStringSrc(R.string.fragment_about_update_title), com.msay2.mire.preferences.Preferences.VERSION_NAME);
		
		update = (LinearLayout)root.findViewById(R.id.id_update);
		update.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				requestPermissions();
			}
		});
		update_title = (TextView)root.findViewById(R.id.id_update_title);
		update_title.setText(title);
	}

	@Override
	public void animateTOMenu()
	{
		TransitionHelperAbout.animateToMenuState(root, new AnimatorListenerAdapter() 
		{
			@Override
			public void onAnimationEnd(Animator animation) 
			{
				super.onAnimationEnd(animation);
			}
		});
		menuIcon.animateIconState(MaterialMenuDrawable.IconState.ARROW);
		showShadow();
	}

	@Override
	public void revertFromMenu()
	{
		TransitionHelperAbout.startRevertFromMenu(root, showShadowListener);
		menuIcon.animateIconState(MaterialMenuDrawable.IconState.BURGER);
	}

	@Override
	public void exitFromMenu()
	{
		TransitionHelperAbout.animateMenuOut(root);
	}
	
	private void requestPermissions()
	{
		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) 
		{
			if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) 
			{
				MainActivity.setDialogReshowRequestPermission(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
			}

			else
			{
				ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
			}
		}
		else
		{
			setStateWiFi();
		}
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
						Preferences.longToast(getActivity(), getResources().getString(R.string.toast_permission_close_app));
					}
				}
			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
	
	private void setStateWiFi()
	{
		isConnected = pref.getPreferences(getActivity()).checkWiFi();
		if (isConnected)
		{
			obtainsWiFi(true);
		}
		else
		{
			noObtainsWiFi(getActivity(), false);
		}
	}
	
	private void noObtainsWiFi(Context context, Boolean status)
	{
		View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_update, null);

		TextView title = (TextView)view.findViewById(R.id.id_title);
		TextView content = (TextView)view.findViewById(R.id.id_content_text);

		title.setText(context.getResources().getString(R.string.no_wifi_access));
		content.setText(context.getResources().getString(R.string.no_wifi_access_prompt));

		AlertDialog.Builder builder = new AlertDialog.Builder(context)
		    .setView(view)
		    .setPositiveButton(context.getResources().getString(R.string.updater_btn_ok), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface view, int position)
				{ }
			});

		dialog = builder.create();
		dialog.show();
	}
	
	private void obtainsWiFi(Boolean status)
	{
		File newFolder = new File(storage + folder_mire);
		newFolder.mkdir();
		
		setNewApkDirectory();
	}
	
	private void setNewApkDirectory()
	{
		File newFolder = new File(storage + folder_mire + folder_update);
		newFolder.mkdir();
		if (newFolder != null)
		{
			getVersion();
		}
	}
	
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
					URL url = new URL(getActivity().getResources().getString(R.string.link_update_json_file));
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
					{
						dialogNoUpdate();
					}
				} 
				else 
				{
					Toast.makeText(getActivity(), ((MainActivity)getActivity()).getStringSrc(R.string.toast_wallpaper_error), Toast.LENGTH_LONG).show();
				}
			}

		}.execute();
	}
	
	private void dialogNoUpdate()
	{
		View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_update, null);

		TextView title = (TextView)view.findViewById(R.id.id_title);
		TextView content = (TextView)view.findViewById(R.id.id_content_text);

		title.setText(getResources().getString(R.string.updater_not_available_title));
		content.setText(String.format(getResources().getString(R.string.updater_not_available_description), getResources().getString(R.string.app_name)));

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
		    .setView(view)
		    .setCancelable(true)
		    .setPositiveButton(getResources().getString(R.string.updater_btn_ok), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int position)
				{ }
			});

		dialog = builder.create();
		dialog.show();
	}
	
	private void dialogYesUpdate(final String url, final String content_release)
	{
		View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_update, null);

		TextView title = (TextView)view.findViewById(R.id.id_title);
		TextView content = (TextView)view.findViewById(R.id.id_content_text);

		title.setText(getResources().getString(R.string.updater_available_title));
		content.setText(content_release);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
		    .setView(view)
		    .setCancelable(true)
		    .setPositiveButton(getResources().getString(R.string.updater_btn_download), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int position)
				{
					new DownloadAsyntask().execute(url);
				}
			})
		    .setNegativeButton(getResources().getString(R.string.updater_btn_back), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int position)
				{ }
			});

		dialog = builder.create();
		dialog.show();
	}
	
    private ProgressDialog showDialog()
	{
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage(getActivity().getResources().getString(R.string.updater_downloading));
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
				OutputStream output = new FileOutputStream("/sdcard" + folder_mire + folder_update + "/mire.apk");

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
			File file = new File("/mnt/sdcard" + folder_mire + folder_update + "/mire.apk");
			
			intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			startActivity(intent);
		}
	}
}
