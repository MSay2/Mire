package com.msay2.mire.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.support.annotation.NonNull;

import com.msay2.mire.R;
import com.msay2.mire.helpers.DrawableHelper;

import fr.yoann.dev.preferences.Preferences;

import org.sufficientlysecure.htmltextview.HtmlTextView;

public class AdapterChangelog extends BaseAdapter 
{
    private final Context context;
    private final String[] changelog;

    public AdapterChangelog(@NonNull Context context, @NonNull String[] changelog) 
	{
        this.context = context;
        this.changelog = changelog;
    }

    @Override
    public int getCount() 
	{
        return changelog.length;
    }

    @Override
    public String getItem(int position) 
	{
        return changelog[position];
    }

    @Override
    public long getItemId(int position) 
	{
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) 
	{
        ViewHolder holder;
        if (view == null) 
		{
            view = View.inflate(context, R.layout.item_dialog_changelog, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } 
		else 
		{
            holder = (ViewHolder)view.getTag();
        }
        holder.changelog.setHtml(changelog[position]);

        return view;
    }

    private class ViewHolder 
	{
        final HtmlTextView changelog;

        ViewHolder(View view)
		{
			int color = Preferences.getAttributeColor(context, R.attr.colorAccent);

            changelog = (HtmlTextView)view.findViewById(R.id.id_text);
            changelog.setCompoundDrawablesWithIntrinsicBounds(DrawableHelper.getTintedDrawable(context, R.drawable.ic_changelog_circle, color), null, null, null);
        }
    }
}
