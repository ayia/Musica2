package com.tyolar.inc.musica;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdTargetingOptions;
import com.google.gson.Gson;
import com.tyolar.inc.musica.DAO.PlayListDAO;
import com.tyolar.inc.musica.adapter.PlayListSongAdapter;
import com.tyolar.inc.musica.composants.LocalePopupMenu;
import com.tyolar.inc.musica.fragments.Mini_Player_View;
import com.tyolar.inc.musica.model.album;
import com.tyolar.inc.musica.model.apiurls;
import com.tyolar.inc.musica.model.artist;
import com.tyolar.inc.musica.model.song;

@SuppressLint("NewApi")
public class BaseActivity extends ActionBarActivity {
	private String titel;
	private Menu _menu = null;
	private Intent playIntent;
	private boolean musicBound = false;
	private Handler mHandler = new Handler();
	RemoteViews notificationView;

	// private Mini_Player__Fragment mControlsFragment;
	app2 mapp;

	@Override
	protected void onStart() {
		super.onStart();
		mapp = (app2) getApplication();
		mapp.setBaseactivity(this);

	}

	public void setupBottomAds() {

		try {
			AdLayout adView = new AdLayout(this);
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.adsview);

			// Ensure the ad scales to fill the screen width.
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			layout.addView(adView, lp);

			// Asynchronous task to retrieve an ad.
			adView.loadAd(new AdTargetingOptions());
		} catch (Exception d) {

			d.printStackTrace();
		}
	}

	public void playSong(final song d) {

		String uri = new apiurls().getAudiourl();

		uri = uri.replace("[id]", d.getId()).replace("[sid]",
				mapp.getAngami_id());
		final Uri myUri = Uri.parse(uri);
		// Play
	}

	protected void onResume() {
		super.onResume();
		mapp.setCurrentActivity(this);
		setupBottomAds();
	}

	public void updateList_Playsong(song d) {

		playSong(d);
	}

	public void LoadMiniPlayer() {
		mapp = (app2) getApplication();
		if (mapp.mini_player == null) {
			mapp.mini_player = (Mini_Player_View) findViewById(R.id.miniplayer);

		}
//		if (mapp.mini_player.isloaded()) {
			Mini_Player_View d = (Mini_Player_View) findViewById(R.id.miniplayer);
			d.setCopyof(mapp.mini_player);
			mapp.mini_player = d;

//		}
	}

	public void initMiniPlayer(final song d) {
		mapp.mini_player.setVisibility(View.VISIBLE);
		mapp.getMusicService().getSongstoPlay().add(d);
		mapp.getMusicService().setSlectedindex(
				mapp.getMusicService().getSongstoPlay().size() - 1);
		mapp.getMusicService().Play();

	}

	public void startfragment(final Intent data) {
		// Collect data from the intent and use it

		if (data.getExtras().containsKey("artist")) {

			artist artist = new Gson().fromJson(data.getStringExtra("artist"),
					artist.class);
			Intent myIntent = new Intent(this,
					com.tyolar.inc.musica.activities.Artist_Activity.class);
			myIntent.putExtra("artist", artist.toJson());
			startActivity(myIntent);
		}

		if (data.getExtras().containsKey("album")) {
			album album = new Gson().fromJson(data.getStringExtra("album"),
					album.class);
			Intent myIntent = new Intent(this,
					com.tyolar.inc.musica.activities.Album_Activity.class);
			myIntent.putExtra("album", album.toJson());
			startActivity(myIntent);

		}

	}

	public void PlayAllSongs(ArrayList<song> songtoplay) {
		mapp.getMusicService().setSongstoPlay(songtoplay);
		app2 mapp = (app2) getApplication();
		mapp.getMusicService().setSlectedindex(0);
		mapp.mini_player.setVisibility(View.VISIBLE);
		mapp.getMusicService().Play();
	}

	public void loadFragment(CFragment fragment) {
		String backStateName = fragment.getClass().getName();

		if (fragment != null) {

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

		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}

	}

	@Override
	public void onBackPressed() {
		android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
		if (fragmentManager.getBackStackEntryCount() > 1) {
			fragmentManager.popBackStack();
			fragmentManager.executePendingTransactions();
			CFragment df = (CFragment) fragmentManager
					.findFragmentById(R.id.frame_container);
			setTitle(df.getTitel());
		} else {
			super.finish();
		}

	}

	private void setHeaderTitel(String titel) {
		getActionBar().setTitle(titel);
	}

	private BaseActivity getme() {
		return this;
	}

	private Menu getMenu() {
		return _menu;
	}

	public void showMenuItemMusic(final View v, final song track) {
		// TODO Auto-generated method stub
		LocalePopupMenu popup = new LocalePopupMenu(this, v);
		popup.inflate(R.menu.menu_list_music);
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {

				case R.id.add_to_playlist:
					Staticui.show_add_playlist_view(getme(), track);
					break;

				case R.id.play_next:
					Playnext(track);
					break;
				case R.id.add_toquee:
					addtoquey(track);
					break;

				default:
					return false;
				}

				return false;
			}

		});

		popup.show();
	}

	public void showMenuItemPlayList(final View v, final song track,
			final PlayListSongAdapter adapter) {
		// TODO Auto-generated method stub
		LocalePopupMenu popup = new LocalePopupMenu(this, v);
		popup.inflate(R.menu.menu_playlist_songs);
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {

				case R.id.remove__playlist:
					mapp.getPlaylist()
							.getSongs()
							.remove(mapp.getPlaylist().getSongs()
									.indexOf(track));
					PlayListDAO.updateRow(BaseActivity.this, mapp.getPlaylist());
					Toast.makeText(getApplicationContext(),
							getString(R.string.one_song_deleted),
							Toast.LENGTH_LONG).show();
					adapter.invalidate();
					break;
				case R.id.add_toquee:
					addtoquey(track);
					break;
				case R.id.go_artist:
					artist f = new artist(track.getArtistID(), track
							.getArtist(), track.getArtistArt());
					Intent myIntent = new Intent(
							getme(),
							com.tyolar.inc.musica.activities.Artist_Activity.class);
					myIntent.putExtra("artist", f.toJson());
					startActivity(myIntent);
					break;
				case R.id.go_album:
					album album = new album(track.getAlbumID(), track
							.getAlbum(), track.getArtist(),
							track.getArtistID(), track.getCoverArt(), track
									.getArtistArt());
					Intent myIntent1 = new Intent(
							v.getContext(),
							com.tyolar.inc.musica.activities.Album_Activity.class);
					myIntent1.putExtra("album", album.toJson());
					startActivity(myIntent1);
					break;

				default:
					return false;
				}

				return false;
			}

		});

		popup.show();
	}

	private void addtoquey(song track) {
		// TODO Auto-generated method stub
		if (!mapp.getMusicService().getSongstoPlay().contains(track)) {
			mapp.getMusicService().getSongstoPlay().add(track);
			if (mapp.mini_player.isloaded())
				mapp.getMusicService().showPlayView();
		}

	}

	private void Playnext(song track) {
		// TODO Auto-generated method stub
		System.out.println("sddssd");
		if (mapp.getMusicService().getSongstoPlay().size() > 0) {

			if (!mapp.getMusicService().getSongstoPlay().contains(track))
				mapp.getMusicService()
						.getSongstoPlay()
						.add(mapp.getMusicService().getSlectedindex() + 1,
								track);
			else
				mapp.getMusicService()
						.getSongstoPlay()
						.add(mapp.getMusicService().getSlectedindex() + 1,
								track);
			if (mapp.mini_player.isloaded())
				mapp.getMusicService().showPlayView();
		}

	}

	// public void ShowMiniPlayer(song track) {
	// if (mapp.MusicNotification == null) {
	// mapp.MusicNotification = new MusicNotification(this);
	// mapp.MusicNotification.ShowMiniPlayer(track);
	// } else {
	// mapp.MusicNotification.setSongView(track);
	// }
	//
	// }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (this instanceof MainActivity) {
			NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			mNM.cancelAll();
		}
		super.onDestroy();

	}

	public void togglePlaybackState() {

	}

}
