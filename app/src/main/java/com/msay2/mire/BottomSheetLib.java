package com.msay2.mire;

import android.app.*;
import android.os.*;

import fr.yoann.dev.preferences.Preferences;
import fr.yoann.dev.preferences.utils.AnimUtils;

import com.msay2.mire.widget.BottomSheet;
import com.msay2.mire.adapter.AdapterDialogLib;
import com.msay2.mire.item_data.ItemDataDialogLib;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.DefaultItemAnimator;

import android.support.v4.content.ContextCompat;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class BottomSheetLib extends AppCompatActivity
{
	private BottomSheet bottomSheet;
	private RecyclerView recycler;
	private ImageView close;
	private ViewGroup titleBar;
	private int stateDismiss = DISMISS_DOWN;
	
	private static final int DISMISS_DOWN = 0;
    private static final int DISMISS_CLOSE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bottom_sheet_lib);
		
		bottomSheet = (BottomSheet)findViewById(R.id.id_root);
		recycler = (RecyclerView)findViewById(R.id.ma_recyclerView);
		close = (ImageView)findViewById(R.id.id_close);
		titleBar = (ViewGroup)findViewById(R.id.title_bar);
		
		ItemDataDialogLib[] item = 
		{
			new ItemDataDialogLib("https://avatars1.githubusercontent.com/u/19941384?v=3&s=400", "Meclot Yoann (MSay2)", "Helpers"),
			new ItemDataDialogLib("https://avatars2.githubusercontent.com/u/1064277?v=3&s=400", "Daniel Zeller", "DepthLib"),
			new ItemDataDialogLib("https://avatars3.githubusercontent.com/u/1386930?v=3&s=400", "Takahirom", "PreLollipopTransition"),
			new ItemDataDialogLib("https://avatars1.githubusercontent.com/u/423539?v=3&s=220", "Bumptech", "Glide"),
			new ItemDataDialogLib("https://avatars1.githubusercontent.com/u/1223348?v=3&s=400", "Sergey Tarasevich", "UniversalImageLoader"),
			new ItemDataDialogLib("https://avatars2.githubusercontent.com/u/11572708?v=3&s=400", "Sufficiently Secure", "HTML TextView")
		};
		
		bottomSheet.registerCallback(registerCallback);
		recycler.setAdapter(new AdapterDialogLib(this, item));
		recycler.setLayoutManager(new LinearLayoutManager(this));
		recycler.setItemAnimator(new DefaultItemAnimator());
		recycler.addOnScrollListener(titleElevation);
		close.setOnClickListener(closeClick);
	}
	
	public BottomSheet.Callbacks registerCallback = new BottomSheet.Callbacks()
	{
		@Override
		public void onSheetDismissed()
		{
			finishAfterTransition();
		}

		@Override
		public void onSheetPositionChanged(int sheetTop, boolean userInteracted)
		{
			setStateAnimationClose(sheetTop, userInteracted);
		}
	};
	
	public View.OnClickListener closeClick = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			finishAfterTransition();
		}
	};
	
	private void setStateAnimationClose(int sheetTop, boolean userInteracted)
	{
		if (userInteracted && close.getVisibility() != View.VISIBLE)
		{
			close.setVisibility(View.VISIBLE);
			close.setAlpha(0f);
			close.animate()
				 .alpha(1f)
				 .setDuration(400L)
				 .setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(BottomSheetLib.this))
				 .start();
		}

		if (sheetTop == 0)
		{
			showClose();
		}
		else 
		{
			showDown();
		}
	}
	
	private void showClose()
	{
        if (stateDismiss == DISMISS_CLOSE) 
		{
			return;
		}
        stateDismiss = DISMISS_CLOSE;
		
        final AnimatedVectorDrawable downToClose = (AnimatedVectorDrawable)ContextCompat.getDrawable(this, R.animator.avd_down_to_close);
       
		close.setImageDrawable(downToClose);
        downToClose.start();
    }

	private void showDown()
	{
        if (stateDismiss == DISMISS_DOWN)
		{
			return;
		}
        stateDismiss = DISMISS_DOWN;
		
        final AnimatedVectorDrawable closeToDown = (AnimatedVectorDrawable)ContextCompat.getDrawable(this, R.animator.avd_close_to_down);
       
		close.setImageDrawable(closeToDown);
        closeToDown.start();
    }
	
	private RecyclerView.OnScrollListener titleElevation = new RecyclerView.OnScrollListener()
	{
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) 
		{
            final boolean raiseTitleBar = dy > 0 || recycler.computeVerticalScrollOffset() != 0;
            titleBar.setActivated(raiseTitleBar);
        }
    };

	@Override
	public void onBackPressed()
	{
		finishAfterTransition();
	}
}
