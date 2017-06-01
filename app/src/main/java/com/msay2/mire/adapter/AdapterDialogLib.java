package com.msay2.mire.adapter;

import android.app.*;

import com.msay2.mire.R;
import com.msay2.mire.item_data.ItemDataDialogLib;
import com.msay2.mire.ActivityDetailsDevs;
import com.msay2.mire.helpers.CircleTransformHelper;

import android.net.Uri;
import android.util.Pair;
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
	private CircleTransformHelper circleTransform;
	
	public AdapterDialogLib(Context context, ItemDataDialogLib[] item_data)
	{
		this.context = context;
		this.item_data = item_data;
		circleTransform = new CircleTransformHelper(context);
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
		final ItemDataDialogLib item = item_data[position];
		
		final String[] URL_LIB = 
		{
			"https://github.com/msay2/Mire/tree/master/helpers",
			"https://github.com/danielzeller/Depth-LIB-Android-",
			"https://github.com/takahirom/PreLollipopTransition",
			"https://github.com/bumptech/glide",
			"https://github.com/nostra13/Android-Universal-Image-Loader",
			"https://github.com/SufficientlySecure/html-textview"
		};
		final String[] DESCRIPTION_LIB =
		{
			getString(R.string.description_lib_meclot_yoann),
			getString(R.string.description_lib_daniel_zeller),
			getString(R.string.description_lib_takahirom),
			getString(R.string.description_lib_bumptech),
			getString(R.string.description_lib_sergeytarasevich),
			getString(R.string.description_lib_sufficientlysecure)
		};
		
		Glide.with(context)
		     .load(item.getImage())
		     .transform(circleTransform)
		     .into(holder.image);
		
		holder.name_dev.setText(item.getNameDev());
		holder.name_lib.setText(item.getNameLib());
		holder.itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(context, ActivityDetailsDevs.class);
				intent.putExtra("image_dev", item.getImage());
				intent.putExtra("name_dev", item.getNameDev());
				intent.putExtra("url_lib", URL_LIB[position]);
				intent.putExtra("description_lib", DESCRIPTION_LIB[position]);
				
				Pair<View, String> pair1 = (Pair<View, String>)Pair.create(holder.image, "transition_dev");
				
				ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)context, pair1);
				
				context.startActivity(intent, options.toBundle());
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
		public ImageView image;
		public TextView name_dev, name_lib;

		ViewHolder(View itemView)
		{
			super(itemView);
			
			image = (ImageView)itemView.findViewById(R.id.id_avatar);
			name_dev = (TextView)itemView.findViewById(R.id.id_name_dev);
			name_lib = (TextView)itemView.findViewById(R.id.id_name_lib);
		}
	}
	
	public String getString(int id)
	{
		String text = context.getResources().getString(id);
		
		return text;
	}
}
