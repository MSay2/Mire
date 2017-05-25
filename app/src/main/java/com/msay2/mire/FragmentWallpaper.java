package com.msay2.mire;

import android.os.*;
import android.app.*;

import com.msay2.mire.interfaces.MenuAnimation;
import com.msay2.mire.helpers.TransitionHelperWallpaper;
import com.msay2.mire.item_data.ItemDataWallpaper;
import com.msay2.mire.adapter.AdapterWallpaper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.DefaultItemAnimator;

import android.support.v4.content.ContextCompat;

import no.agens.depth.lib.MaterialMenuDrawable;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.graphics.Color;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

public class FragmentWallpaper extends Fragment implements MenuAnimation
{
	private View root;
	private boolean introAnimate;
	private ImageView menu;
	private MaterialMenuDrawable menuIcon;
	private RecyclerView recycler;
	private ArrayList<ItemDataWallpaper> item_data;
	private AsyncTask<String, Void, Integer> getWallpapers;
	private AdapterWallpaper adapter;
	
	public static final int TRANSFORM_DURATION = 900;
	
	public static String TAG = FragmentWallpaper.class.getSimpleName();
	
	public FragmentWallpaper()
	{ }
	
	public void setIntroAnimate(boolean introAnimate)
	{
        this.introAnimate = introAnimate;
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		root = inflater.inflate(R.layout.fragment_wallpaper, null);
		
		introAnimate();
		
		recycler = (RecyclerView)root.findViewById(R.id.ma_recyclerView);
		
		setupMenuButton();

		((MainActivity)getActivity()).setCurretMenuIndex(1);
		
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		recycler.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
		recycler.setItemAnimator(new DefaultItemAnimator());
		
		getWallpapers();
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

					TransitionHelperWallpaper.startIntroAnim(root, showShadowListener);
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
	
	@Override
    public void onDestroy()
	{
        if (getWallpapers != null)
		{
			getWallpapers.cancel(true);
		}
		super.onDestroy();
    }

	@Override
	public void animateTOMenu()
	{
		TransitionHelperWallpaper.animateToMenuState(root, new AnimatorListenerAdapter() 
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
		TransitionHelperWallpaper.startRevertFromMenu(root, showShadowListener);
		menuIcon.animateIconState(MaterialMenuDrawable.IconState.BURGER);
	}

	@Override
	public void exitFromMenu()
	{
		TransitionHelperWallpaper.animateMenuOut(root);
	}
	
	private void getWallpapers()
	{
		getWallpapers = new AsyncTask<String, Void, Integer>()
		{
			@Override
			protected void onPreExecute() 
			{
				//
			}

			@Override
			protected Integer doInBackground(String... params)
			{
				Integer result = 0;
				HttpURLConnection urlConnection;
				try
				{
					URL url = new URL("https://raw.githubusercontent.com/msay2/Mire_wallpaper_json/master/wallpaper/script/mire_wallpaper.json");
					urlConnection = (HttpURLConnection)url.openConnection();
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
						parseResult(response.toString());
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
					adapter = new AdapterWallpaper(getActivity(), item_data);
					recycler.setAdapter(adapter);
				} 
				else 
				{
					Toast.makeText(getActivity(), ((MainActivity)getActivity()).getStringSrc(R.string.toast_wallpaper_error), Toast.LENGTH_LONG).show();
					//Preferences.longToast(getActivity(), getActivity().getResources().getString(R.string.toast_wallpaper_get_wallpaper_failed));
				}
			}
		}.execute();
	}
	
	private void parseResult(String result)
	{
        try 
		{
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("wallpaper");
            item_data = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) 
			{
                JSONObject post = posts.optJSONObject(i);
                ItemDataWallpaper item = new ItemDataWallpaper();
                item.setImageUrl(post.optString("image"));
                item.setTitle(post.optString("title"));
				item.setText(post.optString("text"));
                item_data.add(item);
            }
        }
		catch (JSONException e)
		{
			//Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
