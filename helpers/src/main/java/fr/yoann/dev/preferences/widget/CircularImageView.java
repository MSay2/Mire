package fr.yoann.dev.preferences.widget;

import android.content.Context;
import android.util.AttributeSet;

import fr.yoann.dev.preferences.utils.ViewUtils;

public class CircularImageView extends ForegroundImageView 
{
    public CircularImageView(Context context, AttributeSet attrs)
	{
        super(context, attrs);
		
        setOutlineProvider(ViewUtils.CIRCULAR_OUTLINE);
        setClipToOutline(true);
    }
}
