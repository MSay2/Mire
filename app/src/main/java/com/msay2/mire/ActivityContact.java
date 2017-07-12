package com.msay2.mire;

import android.os.*;
import android.app.*;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import fr.yoann.dev.preferences.Preferences;
import fr.yoann.dev.preferences.utils.AnimUtils;

import android.text.TextWatcher;
import android.text.Editable;
import android.text.util.Linkify;
import android.net.Uri;
import android.graphics.Color;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.view.View;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActivityContact extends AppCompatActivity implements View.OnClickListener
{
	private Toolbar toolbar;
	private EditText edit;
	private ImageView more;
	private LinearLayout more_fonction, balise_fonction;
	private RelativeLayout dismiss_more, root;
	private TextView clear, send, balise;
	private TextView question, suggestion, bogue;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		Preferences.makeAppFullscreen(this, Color.TRANSPARENT);
		
		root = (RelativeLayout)findViewById(R.id.id_root);
		toolbar = (Toolbar)findViewById(R.id.ma_toolbar);
		edit = (EditText)findViewById(R.id.id_recipient);
		more = (ImageView)findViewById(R.id.id_more);
		more_fonction = (LinearLayout)findViewById(R.id.id_more_fonction);
		dismiss_more = (RelativeLayout)findViewById(R.id.id_dismiss_more_fonction);
		clear = (TextView)findViewById(R.id.id_clear);
		send = (TextView)findViewById(R.id.id_send);
		balise = (TextView)findViewById(R.id.id_balise);
		balise_fonction = (LinearLayout)findViewById(R.id.id_balise_fonction);
		question = (TextView)findViewById(R.id.id_question);
		suggestion = (TextView)findViewById(R.id.id_suggestion);
		bogue = (TextView)findViewById(R.id.id_bogue);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		toolbar.setNavigationIcon(R.drawable.ic_action_close_semi_black_24dp);
		toolbar.setNavigationOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				finishAfterTransition();
			}
		});
		edit.setHorizontallyScrolling(false);
		edit.setAutoLinkMask(Linkify.ALL);
		edit.addTextChangedListener(textChanged);
		more.setOnClickListener(this);
		dismiss_more.setOnClickListener(this);
		clear.setOnClickListener(this);
		send.setOnClickListener(this);
		balise.setOnClickListener(this);
		question.setOnClickListener(this);
		suggestion.setOnClickListener(this);
		bogue.setOnClickListener(this);
	}

	public TextWatcher textChanged = new TextWatcher()
	{
		@Override
		public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
		{ }

		@Override
		public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
		{
			if (0 < edit.length())
			{
				if (more.getVisibility() == View.GONE)
				{
					AnimUtils._alphaAnimationEnter(ActivityContact.this, more);
				}
			}
			else
			{
				if (more.getVisibility() == View.VISIBLE)
				{
					AnimUtils._alphaAnimationEnd(ActivityContact.this, more);
				}

				if (more_fonction.getVisibility() == View.VISIBLE)
				{
					AnimUtils._alphaAnimationEnd(ActivityContact.this, more_fonction);
				}

				if (balise_fonction.getVisibility() == View.VISIBLE)
				{
					AnimUtils._alphaAnimationEnd(ActivityContact.this, balise_fonction);
				}
				
				if (dismiss_more.getVisibility() == View.VISIBLE)
				{
					AnimUtils._alphaAnimationEnd(ActivityContact.this, dismiss_more);
				}
			}
		}

		@Override
		public void afterTextChanged(Editable p1)
		{ }
	};

	@Override
	public void onClick(View view)
	{
		int id = view.getId();
		if (id == R.id.id_more)
		{
			if (more_fonction.getVisibility() == View.VISIBLE)
			{
				AnimUtils._alphaAnimationEnd(ActivityContact.this, more_fonction);
				AnimUtils._alphaAnimationEnd(ActivityContact.this, dismiss_more);
			}
			else
			{
				AnimUtils._alphaAnimationEnter(ActivityContact.this, more_fonction);
				AnimUtils._alphaAnimationEnter(ActivityContact.this, dismiss_more);
			}
		}
		else if (id == R.id.id_dismiss_more_fonction)
		{
			if (more_fonction.getVisibility() == View.VISIBLE)
			{
				AnimUtils._alphaAnimationEnd(ActivityContact.this, more_fonction);
				AnimUtils._alphaAnimationEnd(ActivityContact.this, dismiss_more);
			}
			if (balise_fonction.getVisibility() == View.VISIBLE)
			{
				AnimUtils._alphaAnimationEnd(ActivityContact.this, balise_fonction);
				AnimUtils._alphaAnimationEnd(ActivityContact.this, dismiss_more);
			}
		}
		else if (id == R.id.id_clear)
		{
			setClearText();
			if (more_fonction.getVisibility() == View.VISIBLE)
			{
				AnimUtils._alphaAnimationEnd(ActivityContact.this, more_fonction);
			}
			dismiss_more.setVisibility(View.GONE);
		}
		else if (id == R.id.id_send)
		{
			setSendMail();
			if (more_fonction.getVisibility() == View.VISIBLE)
			{
				AnimUtils._alphaAnimationEnd(ActivityContact.this, more_fonction);
			}
			dismiss_more.setVisibility(View.GONE);
		}
		else if (id == R.id.id_balise)
		{
			AnimUtils._alphaAnimationEnd(ActivityContact.this, more_fonction);
			if (more_fonction.getVisibility() == View.GONE)
			{
				AnimUtils._alphaAnimationEnter(ActivityContact.this, balise_fonction);
			}
		}
		else if (id == R.id.id_question)
		{
			setSendMail(0);
			if (balise_fonction.getVisibility() == View.VISIBLE)
			{
				AnimUtils._alphaAnimationEnd(ActivityContact.this, balise_fonction);
			}
			dismiss_more.setVisibility(View.GONE);
		}
		else if (id == R.id.id_suggestion)
		{
			setSendMail(1);
			if (balise_fonction.getVisibility() == View.VISIBLE)
			{
				AnimUtils._alphaAnimationEnd(ActivityContact.this, balise_fonction);
			}
			dismiss_more.setVisibility(View.GONE);
		}
		else if (id == R.id.id_bogue)
		{
			setSendMail(2);
			if (balise_fonction.getVisibility() == View.VISIBLE)
			{
				AnimUtils._alphaAnimationEnd(ActivityContact.this, balise_fonction);
			}
			dismiss_more.setVisibility(View.GONE);
		}
	}

	private void setClearText()
	{
		edit.setText("");
	}

	private void setSendMail(int numbers)
	{
		String[] recipient = {"yoannmsay2@hotmail.com"};
		String[] balises =
		{
			getStringSrc(R.string.mail_question), 
			getStringSrc(R.string.mail_suggestion), 
			getStringSrc(R.string.mail_bug)
		};
		String sous_sujet = getStringSrc(R.string.app_name) + ": " + balises[numbers];

		Intent email = new Intent(Intent.ACTION_SEND, Uri.fromParts("mailto", "abc@gmail.com", "null"));
		email.setType("text/plain");
		email.putExtra(Intent.EXTRA_EMAIL, recipient);
		email.putExtra(Intent.EXTRA_SUBJECT, sous_sujet);
		email.putExtra(Intent.EXTRA_TEXT, edit.getText().toString());
		try
		{
			startActivity(Intent.createChooser(email, getStringSrc(R.string.toast_mail_choose_plarforme)));
		} 

		catch (ActivityNotFoundException ex)
		{
			Preferences.longToast(ActivityContact.this, getStringSrc(R.string.toast_mail_send_failed));
		}
	}

	private void setSendMail()
	{
		String[] recipient = {"yoannmsay2@hotmail.com"};
		String balises = getStringSrc(R.string.mail_default);
		String sous_sujet = getStringSrc(R.string.app_name) + ": " + balises;

		Intent email = new Intent(Intent.ACTION_SEND, Uri.fromParts("mailto", "abc@gmail.com", "null"));
		email.setType("text/plain");
		email.putExtra(Intent.EXTRA_EMAIL, recipient);
		email.putExtra(Intent.EXTRA_SUBJECT, sous_sujet);
		email.putExtra(Intent.EXTRA_TEXT, edit.getText().toString());
		try
		{
			startActivity(Intent.createChooser(email, getStringSrc(R.string.toast_mail_choose_plarforme)));
		} 

		catch (ActivityNotFoundException ex)
		{
			Preferences.longToast(ActivityContact.this, getStringSrc(R.string.toast_mail_send_failed));
		}
	}
	
	private String getStringSrc(int id)
	{
		String stringSrc = getResources().getString(id);
		
		return stringSrc;
	}
	
	@Override
	public void onBackPressed()
	{
		finishAfterTransition();
	}
}
