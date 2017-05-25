package com.msay2.mire;

import android.os.*;
import android.app.*;

import com.msay2.mire.interfaces.MenuAnimation;
import com.msay2.mire.helpers.TransitionHelper;

import fr.yoann.dev.preferences.Preferences;

import no.agens.depth.lib.MaterialMenuDrawable;
import no.agens.depth.lib.DepthFAB;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;

import android.util.DisplayMetrics;
import android.graphics.Color;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

public class FragmentHome extends Fragment implements MenuAnimation
{
	private boolean introAnimate;
	private View root;
	private ImageView menu;
	private MaterialMenuDrawable menuIcon;
	
	public static final int TRANSFORM_DURATION = 900;
	public static final String URL_TOPIQUE_MIRE = "http://www.phonandroid.com/forum/mire-un-pack-de-fonds-d-ecrans-t172914.html";
	
	public FragmentHome()
	{ }
	
	public void setIntroAnimate(boolean introAnimate)
	{
        this.introAnimate = introAnimate;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		root = inflater.inflate(R.layout.fragment_home, null);
		
		DepthFAB dl = (DepthFAB)root.findViewById(R.id.fab_container);
		dl.setCustomShadowElevation(TransitionHelper.FAB_ELEVATION);
		dl.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Preferences.intentUri(getActivity(), URL_TOPIQUE_MIRE);
			}
		});
		
		introAnimate();
		setupMenuButton();
		
		((MainActivity)getActivity()).setCurretMenuIndex(0);
		
		return root;
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
						
					TransitionHelper.startIntroAnim(root, showShadowListener);
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
	public void animateTOMenu()
	{
		TransitionHelper.animateToMenuState(root, new AnimatorListenerAdapter() 
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
		TransitionHelper.startRevertFromMenu(root, showShadowListener);
		menuIcon.animateIconState(MaterialMenuDrawable.IconState.BURGER);
	}

	@Override
	public void exitFromMenu()
	{
		TransitionHelper.animateMenuOut(root);
	}
}
