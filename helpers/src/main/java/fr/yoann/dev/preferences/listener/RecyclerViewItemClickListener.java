package fr.yoann.dev.preferences.listener;

import android.content.Context;
import android.view.GestureDetector;  
import android.view.MotionEvent;  
import android.view.View;

import android.support.v7.widget.RecyclerView;  

public class RecyclerViewItemClickListener implements RecyclerView.OnItemTouchListener
{
	private OnItemClickListener listener;
	private GestureDetector gestureDetector;

	public interface OnItemClickListener
	{
		public void onItemClick(View view, int position);
	}

	public RecyclerViewItemClickListener(Context context, OnItemClickListener listener)
	{
		this.listener = listener;
		
		gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener()
		{
			@Override
			public boolean onSingleTapUp(MotionEvent e)
			{
				return true;
			}
		});
	}

	@Override
	public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) 
	{
		View childView = view.findChildViewUnder(e.getX(), e.getY());
		if (childView != null && listener != null && gestureDetector.onTouchEvent(e))
		{
			listener.onItemClick(childView, view.getChildAdapterPosition(childView));
		}
		return false;
	}

	@Override
	public void onTouchEvent(RecyclerView view, MotionEvent motionEvent)
	{ }

	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean p1)
	{ }
}
