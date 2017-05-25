package com.msay2.mire;

import android.os.*;
import android.app.*;

import android.support.v4.app.Fragment;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

public class FragmentIntroC extends Fragment
{
	private View root;
	
	public FragmentIntroC()
	{ }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		root = inflater.inflate(R.layout.fragment_intro_c, null);
		
		return root;
	}
}
