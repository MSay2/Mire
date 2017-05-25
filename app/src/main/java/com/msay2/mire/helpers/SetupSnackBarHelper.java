package com.msay2.mire.helpers;

import android.app.*;
import android.os.*;

import com.msay2.mire.R;

import no.agens.depth.lib.tween.interpolators.ExpoIn;
import no.agens.depth.lib.tween.interpolators.QuintOut;

import android.animation.ObjectAnimator;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

public class SetupSnackBarHelper
{
	private static ViewGroup snackbar;
	private static TextView text;
	private static Button button;
	
	public static boolean isSnackBarVisible = false;
	
	public static void setupSnackBar(Activity activity, String contentText, String buttonText, View.OnClickListener listener)
	{
		snackbar = (ViewGroup)activity.findViewById(R.id.snackbar_container);
		snackbar.setTranslationY(20000);
		
		text = (TextView)snackbar.findViewById(R.id.id_snackbar_text);
		button = (Button)snackbar.findViewById(R.id.id_snackbar_button);
		
		text.setText(contentText);
		button.setText(buttonText);
		
		button.setOnClickListener(listener);
	}
	
	public static void showSnackBar()
	{
		isSnackBarVisible = true;
		ObjectAnimator translationY = ObjectAnimator.ofFloat(snackbar, View.TRANSLATION_Y, snackbar.getHeight(), 0);
        translationY.setDuration(450);
        translationY.setInterpolator(new QuintOut());
        translationY.setStartDelay(150);
		translationY.start();
	}
	
	public static void hideSnackBar()
	{
		isSnackBarVisible = false;
		ObjectAnimator translationY = ObjectAnimator.ofFloat(snackbar, View.TRANSLATION_Y, snackbar.getHeight());
        translationY.setDuration(350);
        translationY.setInterpolator(new ExpoIn());
		translationY.start();
	}
	
	public static void hideSnackBarAndFinish(final Activity activity)
	{
		isSnackBarVisible = false;
		ObjectAnimator translationY = ObjectAnimator.ofFloat(snackbar, View.TRANSLATION_Y, snackbar.getHeight());
        translationY.setDuration(350);
        translationY.setInterpolator(new ExpoIn());
		translationY.addListener(new AnimatorListener()
		{
			@Override
			public void onAnimationStart(Animator animation)
			{ }

			@Override
			public void onAnimationCancel(Animator animation)
			{ }

			@Override
			public void onAnimationRepeat(Animator animation)
			{ }

			@Override
			public void onAnimationEnd(Animator animation)
			{
				activity.finishAfterTransition();
			}
		});
        translationY.start();
	}
}
