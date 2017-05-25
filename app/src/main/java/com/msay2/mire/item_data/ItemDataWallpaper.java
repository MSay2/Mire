package com.msay2.mire.item_data;

public class ItemDataWallpaper
{
	private String imageUrl, title, text;
	
	public ItemDataWallpaper()
	{ }
	
	public String getImageUrl()
	{
		return imageUrl;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setImageUrl(String url)
	{
		this.imageUrl = url;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
}
