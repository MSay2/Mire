package fr.yoann.dev.preferences.utils;

import fr.yoann.dev.preferences.Preferences;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.animation.LinearInterpolator;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

/*
 Helper class for RecyclerView/Toolbar scroll listener
 */
public final class RecyclerViewUtils
{
	private RecyclerViewUtils()
	{ }
	
	public static class ToolbarOnScrollListener extends RecyclerView.OnScrollListener 
	{
		public static final String SHOW_HIDE_TOOLBAR_LISTENER_STATE = "fr.yoann.dev.preferences.utils:RecyclerViewUtils:show_hide_toolbar";
		
		private static final float TOOLBAR_ELEVATION = 4f;

		private Toolbar toolbar;
		private State state;

		public ToolbarOnScrollListener(Toolbar toolbar)
		{
			this.toolbar = toolbar;
			this.state = new State();
		}

		@TargetApi(Build.VERSION_CODES.LOLLIPOP)
		private void setElevation(float elevation) 
		{
			if (Preferences.isLollipop()) 
			{
				toolbar.setElevation(elevation == 5 ? 5 : TOOLBAR_ELEVATION);
			}
		}

		private void toolbarAnimateShow(final int verticalOffset)
		{
			toolbar.animate()
			    .translationY(0)
			    .setInterpolator(new LinearInterpolator())
			    .setDuration(180)
			    .setListener(new AnimatorListenerAdapter()
				{
					@Override
					public void onAnimationStart(Animator animation)
					{
						setElevation(verticalOffset == 5 ? 5 : TOOLBAR_ELEVATION);
					}
				});
		}

		private void toolbarAnimateHide()
		{
			toolbar.animate()
			    .translationY(-toolbar.getHeight())
			    .setInterpolator(new LinearInterpolator())
			    .setDuration(180)
			    .setListener(new AnimatorListenerAdapter()
				{
					@Override
					public void onAnimationEnd(Animator animation)
					{
						setElevation(5);
					}
				});
		}

		@Override
		public final void onScrollStateChanged(RecyclerView recyclerView, int newState)
		{
			if (newState == RecyclerView.SCROLL_STATE_IDLE) 
			{
				if (state.scrollingOffset > 0)
				{
					if (state.verticalOffset > toolbar.getHeight())
					{
						toolbarAnimateHide();
					} 
					else 
					{
						toolbarAnimateShow(state.verticalOffset);
					}
				} 
				else if (state.scrollingOffset < 0)
				{
					if (toolbar.getTranslationY() < toolbar.getHeight() * -0.6 && state.verticalOffset > toolbar.getHeight())
					{
						toolbarAnimateHide();
					} 
					else 
					{
						toolbarAnimateShow(state.verticalOffset);
					}
				}
			}
		}

		@Override
		public final void onScrolled(RecyclerView recyclerView, int dx, int dy)
		{
			int toolbarYOffset = (int) (dy - toolbar.getTranslationY());
			
			state.verticalOffset = recyclerView.computeVerticalScrollOffset();
			state.scrollingOffset = dy;
			
			toolbar.animate().cancel();
			if (state.scrollingOffset > 0) 
			{
				if (toolbarYOffset < toolbar.getHeight()) 
				{
					if (state.verticalOffset > toolbar.getHeight()) 
					{
						setElevation(TOOLBAR_ELEVATION);
					}
					toolbar.setTranslationY(state.translationY = -toolbarYOffset);
				} 
				else 
				{
					setElevation(5);
					toolbar.setTranslationY(state.translationY = -toolbar.getHeight());
				}
			} 
			else if (state.scrollingOffset < 0) 
			{
				if (toolbarYOffset < 0)
				{
					if (state.verticalOffset <= 0) 
					{
						setElevation(5);
					}
					toolbar.setTranslationY(state.translationY = 0);
				} 
				else 
				{
					if (state.verticalOffset > toolbar.getHeight()) 
					{
						setElevation(TOOLBAR_ELEVATION);
					}
					toolbar.setTranslationY(state.translationY = -toolbarYOffset);
				}
			}
		}

		@TargetApi(Build.VERSION_CODES.LOLLIPOP)
		public void onRestoreInstanceState(State state) 
		{
			this.state.verticalOffset = state.verticalOffset;
			this.state.scrollingOffset = state.scrollingOffset;
			if (Preferences.isLollipop()) 
			{
				toolbar.setElevation(state.elevation);
				toolbar.setTranslationY(state.translationY);
			}
		}

		@TargetApi(Build.VERSION_CODES.LOLLIPOP)
		public State onSaveInstanceState()
		{
			state.translationY = toolbar.getTranslationY();
			if (Preferences.isLollipop())
			{
				state.elevation = toolbar.getElevation();
			}
			return state;
		}

		public static final class State implements Parcelable 
		{
			public static Creator<State> CREATOR = new Creator<State>()
			{
				public State createFromParcel(Parcel parcel) 
				{
					return new State(parcel);
				}

				public State[] newArray(int size)
				{
					return new State[size];
				}
			};

			private int verticalOffset;
			private int scrollingOffset;
			private float translationY;
			private float elevation;

			State()
			{ }

			State(Parcel parcel) 
			{
				this.verticalOffset = parcel.readInt();
				this.scrollingOffset = parcel.readInt();
				this.translationY = parcel.readFloat();
				this.elevation = parcel.readFloat();
			}

			@Override
			public int describeContents()
			{
				return 0;
			}

			@Override
			public void writeToParcel(Parcel parcel, int flags) 
			{
				parcel.writeInt(verticalOffset);
				parcel.writeInt(scrollingOffset);
				parcel.writeFloat(translationY);
				parcel.writeFloat(elevation);
			}
		}
	}
}
