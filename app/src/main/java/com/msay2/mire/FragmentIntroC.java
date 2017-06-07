package com.msay2.mire;

import android.os.*;
import android.app.*;

import fr.yoann.dev.preferences.Preferences;

import android.support.v4.app.Fragment;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;

public class FragmentIntroC extends Fragment
{
	public static final String URL_PROJECT = "https://github.com/msay2/Mire/";
	
	private View root;
	private TextView open_source;
	
	public FragmentIntroC()
	{ }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		root = inflater.inflate(R.layout.fragment_intro_c, null);
		
		open_source = (TextView)root.findViewById(R.id.id_open_source);
		open_source.setText(Html.fromHtml(getResources().getString(R.string.fragment_intro_text_c_title)));
		open_source.setOnClickListener(open_source_click);
		
		return root;
	}
	
	public View.OnClickListener open_source_click = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			Preferences.intentUri(getActivity(), URL_PROJECT);
		}
	};
}
