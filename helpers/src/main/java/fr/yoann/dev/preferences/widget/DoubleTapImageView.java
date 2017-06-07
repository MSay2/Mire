package fr.yoann.dev.preferences.widget;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.util.AttributeSet;
import android.util.Log;

public class DoubleTapImageView extends ImageView
{
    private GestureDetector gestureDetector;
	private Context context;

    public DoubleTapImageView(Context context, AttributeSet attrs) 
	{
        super(context, attrs);

		this.context = context;
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) 
	{
        return gestureDetector.onTouchEvent(e);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener 
	{
        @Override
        public boolean onDown(MotionEvent e) 
		{
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) 
		{
			// TODO : This is for the double tap event, implented your method
            return true;
        }
    }
}
