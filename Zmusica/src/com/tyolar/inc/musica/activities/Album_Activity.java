package com.tyolar.inc.musica.activities;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tyolar.inc.musica.R;
import com.tyolar.inc.musica.app2;
import com.tyolar.inc.musica.fragments.AlbumFragment;
import com.tyolar.inc.musica.model.album;
import com.tyolar.inc.musica.model.apiurls;

public class Album_Activity extends ActionsActivity {
	album album;
	app2 mapp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container_action);
		album = new Gson().fromJson(getIntent().getStringExtra("album"),
				album.class);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
			toolbar.setNavigationIcon(R.drawable.ic_back);
			toolbar.setTitle(album.getTitle());
			setTitle(album.getTitle());
			getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		mapp = (app2) getApplicationContext();
		setMainPlayer();
		ImageView album_imageview = (ImageView) findViewById(R.id.image);
		String url = new apiurls().getArtimage();
		url = url.replace("[sid]", mapp.getAngami_id()).replace("[id]",
				album.getCoverArt());

		Picasso.with(this).load(url).placeholder(R.drawable.ic_album_cover)
				.error(R.drawable.ic_album_cover).into(album_imageview);
		loadFragment(new AlbumFragment(album));
	}

}
