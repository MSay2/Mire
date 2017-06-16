package fr.yoann.dev.preferences.enum;

import android.view.Gravity;

public enum SnackBarTypePosition
{
	TOP(Gravity.TOP), BOTTOM(Gravity.BOTTOM), BOTTOM_CENTER(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

	private int gravity;

	SnackBarTypePosition(int gravity) 
	{
		this.gravity = gravity;
	}

	public int getGravity()
	{
		return gravity;
	}
}
