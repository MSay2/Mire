package com.msay2.mire;

import android.os.*;
import android.app.*;

import fr.yoann.dev.preferences.Preferences;

import com.msay2.mire.helpers.WallpaperHelper;

import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import no.agens.depth.lib.tween.interpolators.ExpoIn;
import no.agens.depth.lib.tween.interpolators.QuintOut;

import android.animation.ObjectAnimator;
import android.text.Spanned;
import android.text.Html;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.Button;

public class ActivityWallpaperInfo extends AppCompatActivity implements View.OnClickListener
{
	private static final String URL = "id_img";
    private static final String NAME = "id_title";
	private static final String TEXT = "id_text";
	
	private String image, title, text;
	private ImageView wallpaper;
	private TextView wTitle, wAuteur, location;
	private LinearLayout apply, save;
	private int color;
	private FrameLayout parent_window;
	
	public static boolean isSnackBarVisible = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallpapers_info);
		
		Preferences.makeAppFullscreen(this, Color.TRANSPARENT);
		
		if (savedInstanceState != null) 
		{
            image = savedInstanceState.getString(URL);
            title = savedInstanceState.getString(NAME);
			text = savedInstanceState.getString(TEXT);
        }

		Bundle bundle = getIntent().getExtras();
        if (bundle != null)
		{
            image = bundle.getString(URL);
            title = bundle.getString(NAME);
			text = bundle.getString(TEXT);
        }
		
		color = Preferences.getAttributeColor(this, R.attr.colorAccent);
		
		wallpaper = (ImageView)findViewById(R.id.id_wallpaper);
		wTitle = (TextView)findViewById(R.id.id_title);
		wAuteur = (TextView)findViewById(R.id.id_auteur);
		apply = (LinearLayout)findViewById(R.id.id_apply);
		save = (LinearLayout)findViewById(R.id.id_save);
		parent_window = (FrameLayout)findViewById(R.id.id_parent_window);
		location = (TextView)findViewById(R.id.id_location);
		
		Spanned title_info = Html.fromHtml(getResources().getString(R.string.wallpaper_info_title) + title);
		Spanned auteur_info = Html.fromHtml(getResources().getString(R.string.wallpaper_info_auteur) + text);
		String file_location = Preferences.getExternalStorage() + "/Pictures/" + getResources().getString(R.string.app_name) + "/";
		
		Glide.with(this)
		     .load(image)
		     .into(wallpaper);
		
		wTitle.setText(title_info);
		wAuteur.setText(auteur_info);
		apply.setOnClickListener(this);
		save.setOnClickListener(this);
		parent_window.setOnClickListener(this);
		location.setText(file_location);
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState)
	{
        outState.putString(NAME, title);
        outState.putString(URL, image);
		outState.putString(TEXT, text);

        super.onSaveInstanceState(outState);
    }

	@Override
	public void onClick(View view)
	{
		int id = view.getId();
		if (id == R.id.id_apply)
		{
			WallpaperHelper.applyWallpaper(ActivityWallpaperInfo.this, image);
		}
		else if (id == R.id.id_save)
		{
			WallpaperHelper.downloadWallpaper(ActivityWallpaperInfo.this, image, title, text);
		}
		else if (id == R.id.id_parent_window)
		{
			finishAfterTransition();
		}
	}
	
	@Override
	public void onBackPressed()
	{
		finishAfterTransition();
	}
}
