package com.msay2.mire;

import android.os.*;
import android.app.*;

import fr.yoann.dev.preferences.Preferences;
import fr.yoann.dev.preferences.helpers.ColorHelpers;
import fr.yoann.dev.preferences.utils.AnimUtils;

import com.msay2.mire.helpers.WallpaperHelper;
import com.msay2.mire.utils.ImageConfig;
import com.msay2.mire.widget.DoubleTapImageView;
import com.msay2.mire.transition.CircularRevealTransformEnter;
import com.msay2.mire.utils.MirePaletteUtils;

import com.kogitune.activitytransition.ActivityTransition;
import com.kogitune.activitytransition.ExitActivityTransition;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;

import android.support.design.widget.FloatingActionButton;

import android.support.annotation.ColorInt;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.FailReason;

import android.content.Intent;
import android.transition.Transition;
import android.util.Pair;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.graphics.drawable.*;
import android.support.v4.content.*;
import android.graphics.*;

public class ActivitySetWallpapers extends AppCompatActivity
{
	private static final String URL = "id_img";
    private static final String NAME = "id_title";
	private static final String TEXT = "id_text";
	
	private static String image, title, text;
	private static int mColor;
	
	private DoubleTapImageView wallpaper;
	private ProgressBar progress;
	private ExitActivityTransition exitTransition;
	private Transition.TransitionListener enterTransitionListener;
	private ImageView apply;
	private Animation anim, _anim;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_image);
		Preferences.makeAppFullscreen(this, Color.TRANSPARENT);
		
		setEnter();
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
		
		wallpaper = (DoubleTapImageView)findViewById(R.id.id_image);
		progress = (ProgressBar)findViewById(R.id.id_progress);
		apply = (ImageView)findViewById(R.id.id_apply);
		
		mColor = Preferences.getAttributeColor(this, R.attr.colorAccent);
		anim = AnimationUtils.loadAnimation(this, R.anim.slide_left_from_right);
		_anim = AnimationUtils.loadAnimation(this, R.anim.slide_right_from_left);
		
		exitTransition = ActivityTransition.with(this.getIntent())
		    .to(this, wallpaper, "walls")
		    .duration(300)
		    .start(savedInstanceState);
		
		apply.setOnClickListener(applyClick);
		apply.setOnLongClickListener(applyLongClick);
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState)
	{
        outState.putString(NAME, title);
        outState.putString(URL, image);
		outState.putString(TEXT, text);

        super.onSaveInstanceState(outState);
    }
	
	private void setEnter()
	{
		enterTransitionListener = new Transition.TransitionListener() 
		{
			@Override
			public void onTransitionStart(Transition transition)
			{ }

			@Override
			public void onTransitionEnd(Transition transition)
			{
				loadWallpaper(image);
			}

			@Override
			public void onTransitionCancel(Transition transition)
			{ }

			@Override
			public void onTransitionPause(Transition transition)
			{ }

			@Override
			public void onTransitionResume(Transition transition)
			{ }
		};
		getWindow().getEnterTransition().addListener(enterTransitionListener);
	}
	
	private void loadWallpaper(String url) 
	{
        DisplayImageOptions.Builder options = ImageConfig.getRawDefaultImageOptions();
        options.cacheInMemory(false);
        options.cacheOnDisk(true);

        ImageLoader.getInstance().handleSlowNetwork(true);
        ImageLoader.getInstance().displayImage(url, wallpaper, options.build(), new SimpleImageLoadingListener() 
		{
			@Override
			public void onLoadingStarted(String imageUri, View view) 
			{
				super.onLoadingStarted(imageUri, view);
				progress.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) 
			{
				super.onLoadingFailed(imageUri, view, failReason);

				OnWallpaperLoaded();
				
				Preferences.longToast(ActivitySetWallpapers.this, getStringSrc(R.string.toast_apply_wallpaper_error_loading));
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) 
			{
				super.onLoadingComplete(imageUri, view, loadedImage);

				if (loadedImage != null) 
				{
					Palette.from(loadedImage).generate(new Palette.PaletteAsyncListener()
					{
						@Override
						public void onGenerated(Palette palette)
						{
							OnWallpaperLoaded();
						}
					});
				}
			};
		});
	}
	
	private void OnWallpaperLoaded()
	{
        progress.setVisibility(View.GONE);
		apply.startAnimation(anim);
		apply.setVisibility(View.VISIBLE);
    }
	
	public View.OnClickListener applyClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			Intent intent = new Intent(ActivitySetWallpapers.this, ActivityDialogWallpaperChoiceOptions.class);
			intent.putExtra(URL, image);
			intent.putExtra(NAME, title);
			intent.putExtra(TEXT, text);
			
			CircularRevealTransformEnter.addExtras(intent, R.drawable.ic_more_vert_accent_24dp);
			
			Pair<View, String> pair = (Pair<View, String>)Pair.create((View)findViewById(R.id.id_apply), "transition_dialog");
			
			ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ActivitySetWallpapers.this, pair);
			
			startActivity(intent, options.toBundle());
		}
	};
	
	public View.OnLongClickListener applyLongClick = new View.OnLongClickListener()
	{
		@Override
		public boolean onLongClick(View view)
		{
			Preferences.rapidToast(ActivitySetWallpapers.this, getStringSrc(R.string.apply_options));
			return true;
		}
	};
	
	private String getStringSrc(int id)
	{
		String stringSrc = getResources().getString(id);
		
		return stringSrc;
	}

	@Override
	public void onBackPressed()
	{
		_anim.setAnimationListener(listenerAnim);
		apply.startAnimation(_anim);
		apply.setVisibility(View.GONE);
	}
	
	public Animation.AnimationListener listenerAnim = new Animation.AnimationListener()
	{
		@Override
		public void onAnimationStart(Animation animation)
		{ }

		@Override
		public void onAnimationEnd(Animation animation)
		{
			finishAfterTransition();
		}

		@Override
		public void onAnimationRepeat(Animation animation)
		{ }
	};
}
