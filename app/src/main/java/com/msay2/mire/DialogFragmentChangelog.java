package com.msay2.mire;

import android.app.*;
import android.os.*;

import android.support.annotation.NonNull;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.view.View;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TextView;

import android.content.DialogInterface;

import com.msay2.mire.adapter.AdapterChangelog;
import com.msay2.mire.preferences.Preferences;

public class DialogFragmentChangelog extends DialogFragment
{
    private static final String TAG = "mire.dialog.changelog";
	
	private ListView list;
	private AlertDialog dialog;

    private static DialogFragmentChangelog newInstance()
	{
        return new DialogFragmentChangelog();
    }

    public static void showChangelog(FragmentManager fm) 
	{
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(TAG);
        if (prev != null) 
		{
            ft.remove(prev);
        }
        try 
		{
            DialogFragment dialog = DialogFragmentChangelog.newInstance();
            dialog.show(ft, TAG);
        } 
		catch (IllegalArgumentException | IllegalStateException ignored)
		{ }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		View view = ((LayoutInflater)getActivity().getLayoutInflater()).inflate(R.layout.dialog_changelog, null);

		TextView versionName = (TextView)view.findViewById(R.id.id_version_name);
		versionName.setText(Preferences.VERSION_NAME);

		list = (ListView)view.findViewById(R.id.id_changelog_list);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
		    .setView(view)
		    .setPositiveButton(((MainActivity)getActivity()).getStringSrc(R.string.button_text_yes), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface view, int position)
				{ }
			});

        dialog = builder.create();
        dialog.show();

        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) 
	{
        super.onActivityCreated(savedInstanceState);

		String[] changelog = getActivity().getResources().getStringArray(R.array.changelog);

		list.setAdapter(new AdapterChangelog(getActivity(), changelog));
    }
}
