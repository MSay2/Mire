package com.msay2.mire.adapter;

import com.msay2.mire.R;
import com.msay2.mire.item_data.ItemDataWallpaper;
import com.msay2.mire.widget.SeizeNeufImageView;
import com.msay2.mire.widget.SquareImageView;
import com.msay2.mire.ActivitySetWallpapers;
import com.msay2.mire.ActivityWallpaperInfo;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.graphics.Palette;
import android.support.v7.app.AppCompatActivity;

import com.kogitune.activitytransition.ActivityTransitionLauncher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.animation.GlideAnimation;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.app.*;
import android.util.*;

public class AdapterWallpaper extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	private Context context;
	private ArrayList<ItemDataWallpaper> item_data;
	
	public AdapterWallpaper(Context context, ArrayList<ItemDataWallpaper> item_data)
	{
		this.context = context;
		this.item_data = item_data;
	}
	
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper, parent, false);
		
		ViewHolder viewHolder = new ViewHolder(view);
		
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position)
	{
		ItemDataWallpaper item = item_data.get(position);
		final ViewHolder vH = (ViewHolder)holder;

		Glide.with(context)
			.load(item.getImageUrl())
			.asBitmap()
			.into(new BitmapImageViewTarget(vH.image)
			{
				@Override
				public void onResourceReady(Bitmap bitmap, GlideAnimation anim)
				{
					super.onResourceReady(bitmap, anim);

					Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener()
					{
						@Override
						public void onGenerated(Palette palette)
						{
							Palette.Swatch vibrant = palette.getVibrantSwatch();
							if (vibrant != null)
							{
								vH.layout_container.setBackgroundColor(vibrant.getRgb());
								vH.title.setTextColor(vibrant.getBodyTextColor());
								vH.text.setTextColor(vibrant.getTitleTextColor());
							}
						}
					});
				}
			});

		vH.title.setText(item.getTitle());
		vH.text.setText(item.getText());
		
		if (position == 0)
		{
			vH.layout_container.setBackgroundColor(Color.parseColor("#365363"));
			vH.title.setTextColor(Color.parseColor("#FFFFFF"));
			vH.text.setTextColor(context.getResources().getColor(R.color.semi_white));
		}
		if (position == 1)
		{
			vH.layout_container.setBackgroundColor(Color.parseColor("#FFFAD3"));
			vH.title.setTextColor(Color.parseColor("#212121"));
			vH.text.setTextColor(context.getResources().getColor(R.color.semi_black));
		}
		if (position == 2)
		{
			vH.layout_container.setBackgroundColor(Color.parseColor("#FFCA7F"));
			vH.title.setTextColor(Color.parseColor("#212121"));
			vH.text.setTextColor(context.getResources().getColor(R.color.semi_black));
		}
		if (position == 3)
		{
			vH.layout_container.setBackgroundColor(Color.parseColor("#FFA87F"));
			vH.title.setTextColor(Color.parseColor("#212121"));
			vH.text.setTextColor(context.getResources().getColor(R.color.semi_black));
		}
		if (position == 5)
		{
			vH.layout_container.setBackgroundColor(Color.parseColor("#FFA4AB"));
			vH.title.setTextColor(Color.parseColor("#212121"));
			vH.text.setTextColor(context.getResources().getColor(R.color.semi_black));
		}
	}
	
	@Override
	public int getItemCount()
	{
		return item_data.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
	{
		public final SquareImageView image;
		public final TextView title, text;
		public LinearLayout layout_container, open_image;
		
		ViewHolder(View itemView)
		{
			super(itemView);
			
			image = (SquareImageView)itemView.findViewById(R.id.id_image);
			layout_container = (LinearLayout)itemView.findViewById(R.id.id_container);
			title = (TextView)itemView.findViewById(R.id.id_title);
			text = (TextView)itemView.findViewById(R.id.id_text);
			open_image = (LinearLayout)itemView.findViewById(R.id.id_open_image);
			
			open_image.setOnClickListener(this);
			open_image.setOnLongClickListener(this);
		}

		@Override
		public void onClick(View view)
		{
			int id = view.getId();
			int position = getAdapterPosition();
			if (id == R.id.id_open_image)
			{
				Intent intent = new Intent(context, ActivitySetWallpapers.class);
				intent.putExtra("id_img", item_data.get(position).getImageUrl());
				intent.putExtra("id_title", item_data.get(position).getTitle());
				intent.putExtra("id_text", item_data.get(position).getText());
				
				ActivityTransitionLauncher.with((AppCompatActivity)context)
				    .from(image, "walls")
				    .image(((BitmapDrawable)image.getDrawable()).getBitmap())
				    .launch(intent);
			}
		}

		@Override
		public boolean onLongClick(View view)
		{
			int id = view.getId();
			int position = getAdapterPosition();
			if (id == R.id.id_open_image)
			{
				Intent intent = new Intent(context, ActivityWallpaperInfo.class);
				intent.putExtra("id_img", item_data.get(position).getImageUrl());
				intent.putExtra("id_title", item_data.get(position).getTitle());
				intent.putExtra("id_text", item_data.get(position).getText());
				
				context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity)context).toBundle());
			}
			
			return true;
		}
	}
}
