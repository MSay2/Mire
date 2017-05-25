package com.msay2.mire.adapter;

import com.msay2.mire.R;
import com.msay2.mire.item_data.ItemDataDialogLib;

import fr.yoann.dev.preferences.widget.CircularImageView;

import android.net.Uri;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class AdapterDialogLib extends RecyclerView.Adapter<AdapterDialogLib.ViewHolder>
{
	private Context context;
	private ItemDataDialogLib[] item_data;
	
	public AdapterDialogLib(Context context, ItemDataDialogLib[] item_data)
	{
		this.context = context;
		this.item_data = item_data;
	}
	
	@Override
	public AdapterDialogLib.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_lib, parent, false);
		
		ViewHolder viewHolder = new ViewHolder(view);
		
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final AdapterDialogLib.ViewHolder holder, final int position)
	{
		ItemDataDialogLib item = item_data[position];
		
		final String[] URL_LIB = 
		{
			"https://github.com/msay2",
			"https://github.com/danielzeller/Depth-LIB-Android-",
			"https://github.com/takahirom/PreLollipopTransition",
			"https://github.com/bumptech/glide",
			"https://github.com/nostra13/Android-Universal-Image-Loader",
			"https://github.com/SufficientlySecure/html-textview"
		};
		
		Glide.with(context)
		     .load(item.getImage())
		     .into(holder.image);
		
		holder.name_dev.setText(item.getNameDev());
		holder.name_lib.setText(item.getNameLib());
		holder.itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_LIB[position]));
				context.startActivity(intent);
			}
		});
	}

	@Override
	public int getItemCount()
	{
		return item_data.length;
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder
	{
		public CircularImageView image;
		public TextView name_dev, name_lib;
		
		ViewHolder(View itemView)
		{
			super(itemView);
			
			image = (CircularImageView)itemView.findViewById(R.id.id_avatar);
			name_dev = (TextView)itemView.findViewById(R.id.id_name_dev);
			name_lib = (TextView)itemView.findViewById(R.id.id_name_lib);
		}
	}
}
