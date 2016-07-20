package com.tyolar.inc.musica.fragments;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tyolar.inc.musica.PlayerActivity;
import com.tyolar.inc.musica.R;
import com.tyolar.inc.musica.app2;
import com.tyolar.inc.musica.model.song;

public class Mini_Player_View extends LinearLayout {
	ImageView play_button;
	ImageView pause_button;
	TextView txt_songtitle;
	TextView txt_songartist;
	Context ctx;
	app2 mapp;
	private  boolean loaded = false;
	
	public boolean isloaded() {
		return loaded;
	}

	public void setIsloaded(boolean isloaded) {
		this.loaded = isloaded;
	}



	public Mini_Player_View(Context context) {
		super(context);
		initview(context);
		// TODO Auto-generated constructor stub
	}
	
	public Mini_Player_View(Context context, AttributeSet attrs) {
		super(context, attrs);
		initview(context);
	}
	public Mini_Player_View(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initview(context);
	}

	public void setCopyof(Mini_Player_View m){
		this.setVisibility(m.getVisibility());
		play_button.setVisibility(m.play_button.getVisibility());
		pause_button.setVisibility(m.pause_button.getVisibility());
		txt_songtitle.setText(m.txt_songtitle.getText());
		txt_songartist.setText(m.txt_songartist.getText());
		
		
	}

	public void initview(Context context) {
		ctx=context;
		mapp = (app2) context.getApplicationContext();
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.miniplzyer, this, true);
		play_button = (ImageView) findViewById(R.id.play_button);
		pause_button = (ImageView)findViewById(R.id.pause_button);
		txt_songtitle = (TextView)findViewById(R.id.txt_songtitle);
		txt_songartist = (TextView)findViewById(R.id.txt_songartist);

		play_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				app2 mapp = (app2) v.getContext().getApplicationContext();
				mapp.getMusicService().handleAction(mapp.getMusicService().ACTION_PLAY);
			}
		});

		pause_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				app2 mapp = (app2) v.getContext().getApplicationContext();
				mapp.getMusicService().handleAction(mapp.getMusicService().ACTION_PAUSE);
			}
		});
		findViewById(R.id.paneltiel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent(ctx, PlayerActivity.class);
				ctx.startActivity(myIntent);
				
			}
		});
	
	}

	public void Pause() {
		pause_button.setVisibility(View.GONE);
		play_button.setVisibility(View.VISIBLE);
		setIsloaded(true);
	}

	public void setsong(song d) {
		txt_songtitle.setText(d.getTitle());
		txt_songartist.setText(d.getArtist());
		pause_button.setVisibility(View.VISIBLE);
		play_button.setVisibility(View.GONE);
		setIsloaded(true) ;
	}

	public void showLoading(song d) {
		txt_songtitle.setText(getResources().getString(R.string.loading));
		txt_songartist.setText("");
		play_button.setVisibility(View.INVISIBLE);
		pause_button.setVisibility(View.INVISIBLE);
		setIsloaded(true);
	}

}
