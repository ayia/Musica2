package com.tyolar.inc .musica.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tyolar.inc.musica.R;
import com.tyolar.inc.musica.app2;
import com.tyolar.inc.musica.fragments.ArtistFragment;
import com.tyolar.inc.musica.model.apiurls;
import com.tyolar.inc.musica.model.artist;

public class Artist_Activity extends ActionsActivity {
	artist artist;
	app2 mapp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container_action);
		artist = new Gson().fromJson(getIntent().getStringExtra("artist"),
				artist.class);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
			toolbar.setNavigationIcon(R.drawable.ic_back);
			toolbar.setTitle(artist.getName());
			setTitle(artist.getName());
			getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		setMainPlayer();
		mapp = (app2) getApplicationContext();
		ImageView album_imageview = (ImageView) findViewById(R.id.image);
		String url = new apiurls().getArtimage();
		url = url.replace("[sid]", mapp.getAngami_id()).replace("[id]",
				artist.getArtistArt());

		Picasso.with(this).load(url).placeholder(R.drawable.ic_album_cover)
				.error(R.drawable.ic_album_cover).into(album_imageview);
		loadFragment(new ArtistFragment(artist));
	}

}
