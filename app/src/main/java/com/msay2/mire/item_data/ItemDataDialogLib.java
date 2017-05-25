package com.msay2.mire.item_data;

public class ItemDataDialogLib
{
	private String image;
	private String name_dev, name_lib;
	
	public ItemDataDialogLib(String image, String name_dev, String name_lib)
	{
		this.image = image;
		this.name_dev = name_dev;
		this.name_lib = name_lib;
	}
	
	public String getImage()
	{
		return image;
	}
	
	public String getNameDev()
	{
		return name_dev;
	}
	
	public String getNameLib()
	{
		return name_lib;
	}
	
	public void setImage(String image)
	{
		this.image = image;
	}
	
	public void setNameDev(String name)
	{
		this.name_dev = name;
	}
	
	public void setNameLib(String name)
	{
		this.name_lib = name;
	}
}
