package com.msay2.mire;

import android.os.*;
import android.app.*;

import fr.yoann.dev.preferences.Preferences;
import fr.yoann.dev.preferences.widget.MSay2Button;

import com.msay2.mire.helpers.CircleTransformHelper;

import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

public class ActivityDetailsDevs extends AppCompatActivity
{
	private static final String NAME = "name_dev";
	private static final String IMAGE = "image_dev";
	private static final String URL_LIB = "url_lib";
	private static final String DESCRIPTION_LIB = "description_lib";
	
	private String name, image, url_lib, description_lib;
	private ImageView imageDev;
	private TextView nameDev, descriptionLib;
	private MSay2Button buttonLib;
	private LinearLayout content_text;
	private CircleTransformHelper circleTransform;
	private RelativeLayout root_layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_devs);
		
		if (savedInstanceState != null)
		{
			name = savedInstanceState.getString(NAME);
			image = savedInstanceState.getString(IMAGE);
			url_lib = savedInstanceState.getString(URL_LIB);
			description_lib = savedInstanceState.getString(DESCRIPTION_LIB);
		}
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null)
		{
			name = bundle.getString(NAME);
			image = bundle.getString(IMAGE);
			url_lib = bundle.getString(URL_LIB);
			description_lib = bundle.getString(DESCRIPTION_LIB);
		}
		
		circleTransform = new CircleTransformHelper(this);
		
		root_layout = (RelativeLayout)findViewById(R.id.root_layout);
		imageDev = (ImageView)findViewById(R.id.id_avatar);
		buttonLib = (MSay2Button)findViewById(R.id.id_view_git);
		nameDev = (TextView)findViewById(R.id.id_name_dev);
		descriptionLib = (TextView)findViewById(R.id.id_description_lib);
		content_text = (LinearLayout)findViewById(R.id.id_container_text);
		
		Glide.with(ActivityDetailsDevs.this)
			 .load(image)
		     .transform(circleTransform)
			 .into(imageDev);

		nameDev.setText(name);
		buttonLib.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Preferences.intentUri(ActivityDetailsDevs.this, url_lib);
			}
		});
		descriptionLib.setText(description_lib);
		root_layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putString(NAME, name);
		outState.putString(IMAGE, image);
		outState.putString(URL_LIB, url_lib);
		outState.putString(DESCRIPTION_LIB, description_lib);
		
		super.onSaveInstanceState(outState);
	}
}
