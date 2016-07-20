package com.tyolar.inc.musica.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdTargetingOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tyolar.inc.musica.CFragment;
import com.tyolar.inc.musica.R;
import com.tyolar.inc.musica.app2;
import com.tyolar.inc.musica.fragments.ArtistFragment;
import com.tyolar.inc.musica.model.apiurls;
import com.tyolar.inc.musica.model.artist;

public class More_Activity extends ActionsActivity {
	CFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);
		app2 mapp = (app2) getApplication();
		this.fragment = mapp.CFragment;
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
			toolbar.setNavigationIcon(R.drawable.ic_back);
			toolbar.setTitle(this.fragment.getTitel());
			setTitle(this.fragment.getTitel());
			getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		setMainPlayer();
		android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
		android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		fragmentTransaction.add(R.id.frame_container, fragment,
				fragment.getTitel());
		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.addToBackStack(fragment.getTitel())
				.commitAllowingStateLoss();
		fragmentManager.executePendingTransactions();
		  setupBottomAds();	
	}

}
