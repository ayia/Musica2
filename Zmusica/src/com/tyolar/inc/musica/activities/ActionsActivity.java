package com.tyolar.inc.musica.activities;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdTargetingOptions;
import com.tyolar.inc.musica.BaseActivity;
import com.tyolar.inc.musica.R;
import com.tyolar.inc.musica.app2;
import com.tyolar.inc.musica.fragments.Mini_Player_View;

@SuppressLint("NewApi")
public class ActionsActivity extends BaseActivity {

	public void loadFragment(Fragment fragment) {
		if (fragment != null) {
			FragmentTransaction transaction = getFragmentManager()
					.beginTransaction();
			transaction.add(R.id.frame_container, fragment);
			transaction.commit();
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
		  setupBottomAds();	
	}
	
	public void setMainPlayer(){
		app2 mapp = (app2) getApplication();
//		if(mapp.mini_player!=null && mapp.mini_player.isloaded){
			Mini_Player_View d = (Mini_Player_View) findViewById(R.id.miniplayer);
			d.setCopyof(mapp.mini_player);
			mapp.mini_player=d;
//		}
				
	}


}
