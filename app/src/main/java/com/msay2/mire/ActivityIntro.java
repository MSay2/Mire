package com.msay2.mire;

import android.app.*;
import android.os.*;

import com.msay2.mire.widget.ViewPagerIndicator;

import fr.yoann.dev.preferences.Preferences;
import fr.yoann.dev.preferences.utils.AnimUtils;
import fr.yoann.dev.preferences.helpers.ColorHelpers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.ArrayList;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.media.Image;
import android.util.Log;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.content.res.ColorStateList;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ActivityIntro extends AppCompatActivity
{
	private ViewPager pager;
	private IntroPagerAdapter adapterPager;
	private ViewPagerIndicator indicator;
	private RelativeLayout root_layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		
		root_layout = (RelativeLayout)findViewById(R.id.root_layout);
		Preferences.makeViewSystemBarBackground(root_layout);
		
		pager = (ViewPager)findViewById(R.id.ma_viewPager);
		adapterPager = new IntroPagerAdapter(getSupportFragmentManager());
		indicator = (ViewPagerIndicator)findViewById(R.id.ma_indicator);
		setupViewPager(pager);
	}
	
	private void setupViewPager(final ViewPager viewPager)
	{
		adapterPager.add("tab a", new FragmentIntroA());
		adapterPager.add("tab 2", new FragmentIntroB());
		adapterPager.add("tab 3", new FragmentIntroC());
		
		viewPager.setAdapter(adapterPager);
		viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.page_margin));
		indicator.setFinalStateViewPager(ActivityIntro.this, viewPager);
	}
	
	public static class IntroPagerAdapter extends FragmentPagerAdapter 
	{
		private final List<Fragment> fragmentList = new ArrayList<>();
		private final List<String> fragmentTitle = new ArrayList<>();

		public IntroPagerAdapter(FragmentManager fragmentManager)
		{
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) 
		{
			return fragmentList.get(position);
		}

		@Override
		public int getCount()
		{
			return fragmentList.size();
		}

		public void add(String title, Fragment fragment)
		{
			fragmentTitle.add(title);
			fragmentList.add(fragment);
		}

		@Override
		public CharSequence getPageTitle(int position) 
		{
			return fragmentTitle.get(position);
		}
	}
}
