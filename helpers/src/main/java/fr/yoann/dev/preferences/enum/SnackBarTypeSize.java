package fr.yoann.dev.preferences.enum;

public enum SnackBarTypeSize
{
	SIZE_LINE(48, 48, 1),
	SIZE_ELEVATION_NORMAL(6);

    private int minHeight;
    private int maxHeight;
    private int maxLines;
	private int size_elevation;

    SnackBarTypeSize(int minHeight, int maxHeight, int maxLines) 
	{
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.maxLines = maxLines;
    }

	SnackBarTypeSize(int size_elevation)
	{
		this.size_elevation = size_elevation;
	}

    public int getMinHeight()
	{
        return minHeight;
    }

    public int getMaxHeight()
	{
        return maxHeight;
    }

    public int getMaxLines() 
	{
        return maxLines;
    }

	public int getSizeElevation()
	{
		return size_elevation;
	}
}
