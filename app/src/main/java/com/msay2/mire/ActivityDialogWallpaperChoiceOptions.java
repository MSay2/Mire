package com.msay2.mire;

import android.app.*;
import android.os.*;

import fr.yoann.dev.preferences.Preferences;

import com.msay2.mire.transition.CircularRevealTransformEnter;
import com.msay2.mire.transition.MorphTransform;
import com.msay2.mire.helpers.WallpaperHelper;

import android.support.v7.app.AppCompatActivity;

import android.support.v4.content.ContextCompat;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ActivityDialogWallpaperChoiceOptions extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener
{
	private LinearLayout window;
	private ViewGroup container;
	private ImageView apply, save;
	private int[] ids = {R.id.root_window, R.id.id_container_transition, R.id.id_apply, R.id.id_save};
	private static String image, title, text;
	private static int mColor;
	
	private static final String URL = "id_img";
    private static final String NAME = "id_title";
	private static final String TEXT = "id_text";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_wallpaper_choice_options);
		Preferences.makeAppFullscreen(this, Color.TRANSPARENT);
		
		container = (ViewGroup)findViewById(ids[1]);
		if (!CircularRevealTransformEnter.setup(this, container))
		{
			MorphTransform.setup(this, container, ContextCompat.getColor(this, R.color.white), getResources().getDimensionPixelSize(R.dimen.dialog_corners));
		}
		
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
		
		window = (LinearLayout)findViewById(ids[0]);
		apply = (ImageView)findViewById(ids[2]);
		save = (ImageView)findViewById(ids[3]);
		
		window.setOnClickListener(this);
		container.setOnClickListener(this);
		apply.setOnClickListener(this);
		save.setOnClickListener(this);
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
		if (id == ids[0])
		{
			finishAfterTransition();
		}
		else if (id == ids[1])
		{
			// TODO : Not use the listener
		}
		else if (id == ids[2])
		{
			WallpaperHelper.applyWallpaper(ActivityDialogWallpaperChoiceOptions.this, image);
		}
		else if (id == ids[3])
		{
			WallpaperHelper.downloadWallpaper(ActivityDialogWallpaperChoiceOptions.this, mColor, image, title, text);
		}
	}

	@Override
	public boolean onLongClick(View view)
	{
		int id = view.getId();
		if (id == ids[2])
		{
			Preferences.longToast(ActivityDialogWallpaperChoiceOptions.this, getStringSrc(R.string.apply_wallpaper));
		}
		else if (id == ids[3])
		{
			Preferences.longToast(ActivityDialogWallpaperChoiceOptions.this, getStringSrc(R.string.apply_save));
		}
		return true;
	}
	
	private String getStringSrc(int id)
	{
		String stringSrc = getResources().getString(id);
		
		return stringSrc;
	}
}
