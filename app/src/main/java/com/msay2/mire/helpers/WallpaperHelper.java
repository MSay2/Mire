package com.msay2.mire.helpers;

import android.app.*;
import android.os.*;

import fr.yoann.dev.preferences.widget.SnackBar;

import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.TextView;
import android.widget.Toast;

import com.msay2.mire.R;
import com.msay2.mire.utils.ImageConfig;
import com.msay2.mire.utils.Tags;
import com.msay2.mire.preferences.Preferences;
import com.msay2.mire.ActivityContact;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

public class WallpaperHelper 
{
    public static final int UNKNOWN = 0;
    public static final int CLOUD_WALLPAPERS = 1;
    public static final int EXTERNAL_APP = 2;

	private static TextView titleMessage;
	private static AlertDialog dialog;

    public static File getDefaultWallpapersDirectory(@NonNull Context context)
	{
        try 
		{
            if (Preferences.getPreferences(context).getWallsDirectory().length() == 0) 
			{
                return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + context.getResources().getString(R.string.app_name));
            }
            return new File(Preferences.getPreferences(context).getWallsDirectory());
        } 
		catch (Exception e) 
		{
            return new File(context.getFilesDir().toString() + "/Pictures/"+ context.getResources().getString(R.string.app_name));
        }
    }

    public static void downloadWallpaper(final @NonNull Context context, final String link, final String name, final String auteur)
	{
        File cache = ImageLoader.getInstance().getDiskCache().get(link);
        if (cache != null)
		{
            File target = new File(getDefaultWallpapersDirectory(context).toString() + File.separator + name + " " + "(" + auteur + ")" + FileHelper.IMAGE_EXTENSION);
            if (target.exists()) 
			{
                wallpaperSaved(context, target);
                return;
            }
            if (FileHelper.copyFile(cache, target))
			{
                wallpaperSaved(context, target);
				context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(target.toString()))));
                
				return;
            }
        }

        new AsyncTask<Void, Integer, Boolean>()
		{
            AlertDialog dialog;
			TextView titleMessage;
            HttpURLConnection connection;
            File output;
            File file;
            int fileLength;

            @Override
            protected void onPreExecute()
			{
				super.onPreExecute();
                output = getDefaultWallpapersDirectory(context);
                file = new File(output.toString() + File.separator + name + FileHelper.IMAGE_EXTENSION);

				View viewProgress = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_progress_indeterminate, null);

				titleMessage = (TextView)viewProgress.findViewById(R.id.id_title);
				ProgressBar progressBar = (ProgressBar)viewProgress.findViewById(R.id.id_progressBar);

				titleMessage.setText(context.getResources().getString(R.string.download_wallpaper_prompt));
				progressBar.setIndeterminate(true);

				AlertDialog.Builder builder = new AlertDialog.Builder(context)
				    .setView(viewProgress);

				dialog = builder.create();
				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(true);
				dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
				{
					@Override
					public void onDismiss(DialogInterface p1)
					{
						try 
						{
							if (connection != null)
							{
								connection.disconnect();
							}
						} 
						catch (Exception ignored)
						{ }
						cancel(true);
					}
				});
			dialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... voids)
			{
                while (!isCancelled())
				{
                    try 
					{
                        Thread.sleep(1);
                        if (!output.exists())
						{
							if (!output.mkdirs())
							{
								return false;
							}
						}
						
                        URL url = new URL(link);
                        connection = (HttpURLConnection)url.openConnection();
                        connection.setConnectTimeout(15000);

                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
						{
                            fileLength = connection.getContentLength();
                            InputStream stream = connection.getInputStream();
                            OutputStream output = new FileOutputStream(file);

                            byte data[] = new byte[1024];
                            long total = 0;
                            int count;

                            while ((count = stream.read(data)) != -1) 
							{
                                total += count;
                                if (fileLength > 0)
								{
									publishProgress((int)(total * 100 / fileLength));
								}
                                output.write(data, 0, count);
                            }

                            stream.close();
                            output.flush();
                            output.close();
                            return true;
                        }
                    }
					catch (Exception e) 
					{
                        Log.d(Tags.LOG_TAG, Log.getStackTraceString(e));
                        return false;
                    }
                }
                return false;
            }

            @Override
            protected void onProgressUpdate(Integer... values) 
			{
                super.onProgressUpdate(values);
				
                int downloaded = fileLength / 1014;
                String size = String.valueOf(values[0] * fileLength/1024/100) + " KB" + String.valueOf(fileLength == 0 ? "" : "/" + downloaded + " KB");
                String downloading = context.getResources().getString(R.string.download_wallpaper_prompt);
                String text = downloading +"\n"+ size + "";
                titleMessage.setText(text);
            }

            @Override
            protected void onCancelled() 
			{
                super.onCancelled();
                Toast.makeText(context, context.getResources().getString(R.string.toast_apply_wallpaper_canceled, Toast.LENGTH_LONG), Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean)
			{
                super.onPostExecute(aBoolean);
                try 
				{
                    dialog.dismiss();
                } 
				catch (IllegalArgumentException ignored)
				{ }

                if (aBoolean) 
				{
                    if (Preferences.getPreferences(context).getWallsDirectory().length() == 0)
					{
                        Preferences.getPreferences(context).setWallsDirectory(output.toString());
					}
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.toString()))));

                    wallpaperSaved(context, file);
                } 
				else
				{
                    Toast.makeText(context, context.getResources().getString(R.string.toast_save_wallpaper_failed), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private static void wallpaperSaved(@NonNull Context context, @NonNull File file) 
	{
		SnackBar.makeText(context, file.toString()).setAction(R.string.snackbar_button_text_open, new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				try 
				{
					Uri uri = FileHelper.getUriFromFile(context, context.getPackageName(), file);
					if (uri == null)
					{
						return;
					}
					context.startActivity(new Intent().setAction(Intent.ACTION_VIEW).setDataAndType(uri, "image/*").setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));
				} 
				catch (ActivityNotFoundException e)
				{
					Toast.makeText(context, context.getResources().getString(R.string.toast_no_wallpaper_app), Toast.LENGTH_LONG).show();
				}
			}
		}).show(6000);
		
		
        /*View rootView = ((AppCompatActivity)context).getWindow().getDecorView().findViewById(R.id.title);
        if (rootView != null)
		{
            Snackbar snackbar = Snackbar.make(rootView, file.toString(), 6000)
			    .setAction("Ouvrir", new View.OnClickListener()
				{
					@Override
					public void onClick(View p1)
					{
						try 
						{
                            Uri uri = FileHelper.getUriFromFile(context, context.getPackageName(), file);
                            if (uri == null) 
								return;
                            context.startActivity(new Intent().setAction(Intent.ACTION_VIEW).setDataAndType(uri, "image/*").setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));
                        } 
						catch (ActivityNotFoundException e)
						{
                            Toast.makeText(context, context.getResources().getString(R.string.toast_no_wallpaper_app), Toast.LENGTH_LONG).show();
                        }
					}
				})
			    .setActionTextColor(color);
            View snackBarView = snackbar.getView();
            TextView textView = (TextView)snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            if (textView != null)
			{
				textView.setMaxLines(5);
			}
            snackbar.show();
            return;
        }*/
    }
	
	public static AsyncTask<String, Void, Bitmap> applyWallpaper(final Context context, final String url)
	{
		View viewProgress = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_progress_indeterminate, null);

		titleMessage = (TextView)viewProgress.findViewById(R.id.id_title);
		ProgressBar progressBar = (ProgressBar)viewProgress.findViewById(R.id.id_progressBar);

		titleMessage.setText(context.getResources().getString(R.string.toast_apply_wallpaper));
		progressBar.setIndeterminate(true);

		AlertDialog.Builder builder = new AlertDialog.Builder(context)
			.setView(viewProgress);

		dialog = builder.create();
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		
		return new AsyncTask<String, Void, Bitmap>()
		{
			@Override
			protected void onPreExecute()
			{
				dialog.show();
			}
			
			@Override
			protected Bitmap doInBackground(String... params)
			{
				Bitmap result = null;
				try
				{
					result = Glide.with(context).load(url).asBitmap().into(-1, -1).get();
				} 
				catch (final ExecutionException e)
				{
					Log.e(Tags.LOG_TAG, e.getMessage());
				} 
				catch (final InterruptedException e)
				{
					Log.e(Tags.LOG_TAG, e.getMessage());
				}

				WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
				try 
				{
					wallpaperManager.setBitmap(result);
				} catch (IOException ex) 
				{
					ex.printStackTrace();
				}
				return result;
			}
			
			@Override
			protected void onPostExecute(Bitmap result) 
			{
				super.onPostExecute(result);

				WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
				try
				{
					wallpaperManager.setBitmap(result);
					dialog.dismiss();
					
					wallpaperApplyed(context);
				}
				catch (IOException ex) 
				{
					ex.printStackTrace();
					Toast.makeText(context, context.getResources().getString(R.string.toast_apply_wallpaper_failed), Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
	}
	
	private static void wallpaperApplyed(@NonNull Context context)
	{
		Toast.makeText(context, R.string.snackbar_apply_wallpaper_succed, Toast.LENGTH_LONG).show();
	}
}
