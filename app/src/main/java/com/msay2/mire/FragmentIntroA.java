package com.msay2.mire;

import android.os.*;
import android.app.*;

import android.support.v4.app.Fragment;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

public class FragmentIntroA extends Fragment
{
	private View root;
	
	public FragmentIntroA()
	{ }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		root = inflater.inflate(R.layout.fragment_intro_a, null);

		return root;
	}
}
