package com.msay2.mire.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.msay2.mire.utils.Tags;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileHelper
{
    public static final String IMAGE_EXTENSION = ".jpeg";

    static boolean copyFile(@NonNull File file, @NonNull File target) 
	{
        try
		{
            if (!target.getParentFile().exists())
			{
                if (!target.getParentFile().mkdirs()) 
					return false;
            }

            InputStream inputStream = new FileInputStream(file);
            OutputStream outputStream = new FileOutputStream(target);

            byte[] buffer = new byte[1024];
            int read;

            while ((read = inputStream.read(buffer)) != -1)
			{
                outputStream.write(buffer, 0, read);
            }

            inputStream.close();
            outputStream.flush();
            outputStream.close();
            return true;
        }
		catch (Exception e) 
		{
            Log.d(Tags.LOG_TAG, Log.getStackTraceString(e));
        }
        return false;
    }

    @Nullable
    public static Uri getUriFromFile(Context context, String applicationId, File file) 
	{
        try 
		{
            return FileProvider.getUriForFile(context, applicationId + ".fileProvider", file);
        }
		catch (IllegalArgumentException e) 
		{
            Log.d(Tags.LOG_TAG, Log.getStackTraceString(e));
        }
        return null;
    }
}
