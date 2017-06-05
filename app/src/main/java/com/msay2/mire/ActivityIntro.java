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
	private ImageView aa, ab, ac, ba, bb, bc;
	private TextView title, message, title_1, message_1, title_2, message_2;
	private ViewPagerIndicator indicator;
	private TextView start;
	private RelativeLayout bar_button;
	
	private static final String TAG = "TAG";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		
		pager = (ViewPager)findViewById(R.id.ma_viewPager);
		adapterPager = new IntroPagerAdapter(getSupportFragmentManager());
		indicator = (ViewPagerIndicator)findViewById(R.id.ma_indicator);
		start = (TextView)findViewById(R.id.id_start);
		start.setOnClickListener(startClick);
		bar_button = (RelativeLayout)findViewById(R.id.id_bar_button);
		setupViewPager(pager, adapterPager, bar_button);
	}
	
	private void setupViewPager(final ViewPager viewPager, final ActivityIntro.IntroPagerAdapter adapter, View view)
	{
		adapter.add("tab a", new FragmentIntroA());
		adapter.add("tab 2", new FragmentIntroB());
		adapter.add("tab 3", new FragmentIntroC());
		
		viewPager.setAdapter(adapter);
		viewPager.setBackgroundColor(getResources().getColor(R.color.primary));
		indicator.setFinalStateViewPager(ActivityIntro.this, viewPager, adapter, view);
		viewPager.setPageTransformer(true, new ViewPager.PageTransformer()
		{
			@Override
			public void transformPage(View view, float position)
			{
				aa = (ImageView)view.findViewById(R.id.primer_aa);
				ab = (ImageView)view.findViewById(R.id.primer_ab);
				ac = (ImageView)view.findViewById(R.id.primer_ac);

				ba = (ImageView)view.findViewById(R.id.primer_ba);
				bb = (ImageView)view.findViewById(R.id.primer_bb);
				bc = (ImageView)view.findViewById(R.id.primer_bc);
				
				title = (TextView)view.findViewById(R.id.id_title);
				message = (TextView)view.findViewById(R.id.id_message);
				
				title_1 = (TextView)view.findViewById(R.id.id_title_1);
				message_1 = (TextView)view.findViewById(R.id.id_message_1);
				
				title_2 = (TextView)view.findViewById(R.id.id_title_2);
				message_2 = (TextView)view.findViewById(R.id.id_message_2);
				
				int pageWidth = view.getWidth();
				int pageHeight = view.getHeight();

				float ratio = (float)pageWidth / pageHeight;
				Log.d(TAG, "val " + ratio);
				
				float pageWidthTimesPosition = pageWidth * position;
				//float pageHeightTimesPosition = pageHeight * position;
				//float absPosition = Math.abs(position);
				
				if (position < -1)
				{
					//view.setAlpha(0);
				}
				else if (position <= 1)
				{
					if (position > 0 && position <= 1)
					{
						if (ba != null && bb != null && bc != null)
						{
							bb.setTranslationX(pageWidth * (1 - position) / 20);
							bb.setTranslationY(pageWidth * (1 - position) / 20);
							bc.setTranslationX(pageWidth * (1 - position) / 10);
							bc.setTranslationY(pageWidth * (1 - position) / 10);
							ba.setRotation(-6 * (1 - position));
							bb.setRotation(-2 * (1 - position));
						}
						
						if (aa != null && ab != null && ac != null) 
						{
							ac.setTranslationX(+ pageWidthTimesPosition * 2f);
							ab.setTranslationX(+ pageWidthTimesPosition * 3f);
							aa.setTranslationX(+ pageWidthTimesPosition * 4f);
						}
					}
					else if (position <= 0 && position > -1)
					{
						if (aa != null && ab != null && ac != null) 
						{
							ac.setTranslationY(pageWidth * (position) / 2);
							ab.setTranslationY(pageWidth * (position) / 3);
							aa.setTranslationY(pageWidth * (position) / 4);

							ac.setTranslationX(pageWidth * (position) / 2);
							ab.setTranslationX(pageWidth * (position) / 3);
							aa.setTranslationX(pageWidth * (position) / 4);

							ac.setRotation(4 * (position * 10));
							ab.setRotation(2 * (position * 10));
						}
						
						if (ba != null && bb != null && bc != null)
						{
							bb.setTranslationX(pageWidth * (1 - position) / 20);
							bb.setTranslationY(pageWidth * (1 - position) / 20);
							bc.setTranslationX(pageWidth * (1 - position) / 10);
							bc.setTranslationY(pageWidth * (1 - position) / 10);
							ba.setRotation(-6 * (1 - position));
							bb.setRotation(-2 * (1 - position));
						}
						
						if (message_2 != null)
						{
							message_2.setTranslationX(+ pageWidthTimesPosition * 2f);
						}
						
						/*if (title != null && message != null)
						{
							//title.setTranslationX(+ pageWidthTimesPosition * 4f);
							//message.setTranslationX(+ pageWidthTimesPosition * 2f);
						}*/
							//title.setAlpha(1.0f - absPosition );
							
							//View mDimLabel = view.findViewById(R.id.id_message);
							//mDimLabel.setTranslationX((float) (-(1 - position) * pageWidth));
							//mDimLabel.setTranslationX((float) (-(1 - position) * 1.5 * pageWidth));
					}
				}
				else
				{
					//view.setAlpha(0);
				}
			}
		});
	}
	
	public View.OnClickListener startClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			finishAfterTransition();
		}
	};

	@Override
	public void onBackPressed()
	{
		// TODO: NOT use this method
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
