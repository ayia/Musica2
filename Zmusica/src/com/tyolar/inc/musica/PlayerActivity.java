package com.tyolar.inc.musica;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.AdTargetingOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
//import com.google.android.gms.analytics.GoogleAnalytics;
import com.squareup.picasso.Picasso;
import com.tyolar.inc.musica.adapter.List_Song_Player_Adapter;
import com.tyolar.inc.musica.composants.LocalePopupMenu;
import com.tyolar.inc.musica.composants.TouchInterceptor;
import com.tyolar.inc.musica.library.media.MediaListener;
import com.tyolar.inc.musica.model.album;
import com.tyolar.inc.musica.model.apiurls;
import com.tyolar.inc.musica.model.artist;
import com.tyolar.inc.musica.model.song;

public class PlayerActivity extends Activity {
	private SeekBar seekbar;
	Menu menu;
	private ImageView songimage;
	private TextView duration;
	private TextView current_time;
	private TextView song_artist;
	private TextView song_titel;
	private TextView song_album;
	com.tyolar.inc.musica.widget.ProgressBarCircular loader_stat;
	ImageButton player_pause;
	ImageButton player_play;
	public ImageView back;
	public ImageView next;
	private com.tyolar.inc.musica.composants.TouchInterceptor SongsListView;
	List_Song_Player_Adapter adp;
	app2 mapp;
	private static final int AUDIO_PROGRESS_UPDATE_TIME = 100;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView();
	}

	protected void setView() {
		setContentView(R.layout.activity_player);

		mapp = (app2) getApplication();
		mapp.PlayerActivity = this;
		song_titel = (TextView) findViewById(R.id.song_titel);
		loader_stat = (com.tyolar.inc.musica.widget.ProgressBarCircular) findViewById(R.id.loading_progress);
		song_artist = (TextView) findViewById(R.id.song_artist);
		song_album = (TextView) findViewById(R.id.song_album);
		seekbar = (SeekBar) findViewById(R.id.seekprogress);
		SongsListView = (TouchInterceptor) findViewById(R.id.SongsListView);
		player_pause = (ImageButton) findViewById(R.id.player_pause);
		player_play = (ImageButton) findViewById(R.id.player_play);
		back = (ImageView) findViewById(R.id.preview_song2);
		next = (ImageView) findViewById(R.id.next_song2);
		songimage = (ImageView) findViewById(R.id.song_umage);
		current_time = (TextView) findViewById(R.id.current_time);
		duration = (TextView) findViewById(R.id.duration);
		adp = new List_Song_Player_Adapter(this, mapp.getMusicService()
				.getSongstoPlay());
		SongsListView.setAdapter(adp);
		mProgressUpdateHandler = new Handler();
		player_play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				app2 mapp = (app2) v.getContext().getApplicationContext();
				app2.getMusicService().catchActions(
						mapp.getMusicService().ACTION_PLAY);

			}
		});

		player_pause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				app2 mapp = (app2) v.getContext().getApplicationContext();
				app2.getMusicService().catchActions(
						mapp.getMusicService().ACTION_PAUSE);

			}
		});

		findViewById(R.id.reduce).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		findViewById(R.id.more_icon).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onOverflowPressed(v, mapp.getMusicService().getSongstoPlay()
						.get(mapp.getMusicService().getSlectedindex()));

			}
		});
		findViewById(R.id.playlist).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showhideplayedmusic();
			}
		});
		final InterstitialAd mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId("ca-app-pub-3908763514019803/6819661174");
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				"SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID").build();
		mInterstitialAd.loadAd(adRequest);
		// Begin listening to interstitial & show ads.
		mInterstitialAd.setAdListener(new AdListener() {
			public void onAdLoaded() {
				mInterstitialAd.show();
			}
		});
		setupBottomAds();

	}

	private Handler mProgressUpdateHandler;
	private Runnable mUpdateProgress = new Runnable() {
		public void run() {
			if (mProgressUpdateHandler != null
					&& mapp.getMusicService().getMediaPlayer().isPlaying()) {
				seekbar.setProgress((int) mapp.getMusicService()
						.getMediaPlayer().getCurrentPosition());
				int currentTime = mapp.getMusicService().getMediaPlayer()
						.getCurrentPosition();
				updateRuntime(currentTime);
				// repeat the process
				mProgressUpdateHandler.postDelayed(this,
						AUDIO_PROGRESS_UPDATE_TIME);
			} else {
				// DO NOT update UI if the player is paused
			}
		}
	};

	private void updateRuntime(int currentTime) {

		StringBuilder playbackStr = new StringBuilder();
		playbackStr.append(String.format(
				"%02d:%02d",
				TimeUnit.MILLISECONDS.toMinutes((long) currentTime),
				TimeUnit.MILLISECONDS.toSeconds((long) currentTime)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes((long) currentTime))));

		current_time.setText(playbackStr);

	}

	private void initMediaSeekBar() {
		// update seekbar
		long finalTime = mapp.getMusicService().getMediaPlayer().getDuration();
		seekbar.setMax((int) finalTime);

		seekbar.setProgress(0);

		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mapp.getMusicService().getMediaPlayer()
						.seekTo(seekBar.getProgress());
				updateRuntime(seekBar.getProgress());
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		});
	}

	private void setTotalTime() {
		StringBuilder playbackStr = new StringBuilder();
		long totalDuration = 0;
		try {
			totalDuration = mapp.getMusicService().getMediaPlayer()
					.getDuration();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (totalDuration != 0) {
			playbackStr.append(String.format(
					"%02d:%02d",
					TimeUnit.MILLISECONDS.toMinutes((long) totalDuration),
					TimeUnit.MILLISECONDS.toSeconds((long) totalDuration)
							- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
									.toMinutes((long) totalDuration))));
		}

		duration.setText(playbackStr);
	}

	void setImage(song song) {
		String url = new apiurls().getArtimage();
		url = url.replace("[sid]", mapp.getAngami_id()).replace("[id]",
				song.getCoverArt());
		Picasso.with(this).load(url).into(songimage);
		;
	}

	public void setLoadingView(song song) {
		song_artist.setText(song.getArtist());
		song_titel.setText(song.getTitle());
		song_album.setText(song.getAlbum());

		loader_stat.setVisibility(View.VISIBLE);
		player_pause.setVisibility(View.GONE);
		player_play.setVisibility(View.GONE);
		back.setVisibility(View.GONE);
		next.setVisibility(View.GONE);
		update_nextBackButton();
		setImage(song);
	}

	public void setPlayingView(song song) {
		song_artist.setText(song.getArtist());
		song_titel.setText(song.getTitle());
		song_album.setText(song.getAlbum());
		loader_stat.setVisibility(View.GONE);
		player_pause.setVisibility(View.VISIBLE);
		player_play.setVisibility(View.GONE);
		back.setVisibility(View.INVISIBLE);
		next.setVisibility(View.INVISIBLE);
		setTotalTime();
		setImage(song);
		initMediaSeekBar();
		adp.notifyDataSetChanged();
		mProgressUpdateHandler.postDelayed(mUpdateProgress,
				AUDIO_PROGRESS_UPDATE_TIME);
		update_nextBackButton();
	}

	public void setPauseView(song song) {
		song_artist.setText(song.getArtist());
		song_titel.setText(song.getTitle());
		song_album.setText(song.getAlbum());
		loader_stat.setVisibility(View.GONE);
		player_pause.setVisibility(View.GONE);
		player_play.setVisibility(View.VISIBLE);
		back.setVisibility(View.INVISIBLE);
		next.setVisibility(View.INVISIBLE);
		setTotalTime();
		setImage(song);
		update_nextBackButton();
	}

	public void initializeViewsandPlay(song song) {
		// TODO Auto-generated method stub
		setTitle(song.getTitle());
		song_titel.setText(song.getTitle());
		song_artist.setText(song.getArtist());
		song_album.setText(song.getAlbum());
		// adp.notifyDataSetChanged();

		// mapp.getAudioWife().getInstance().updateUI();
		String url = new apiurls().getArtimage();
		url = url.replace("[sid]", mapp.getAngami_id()).replace("[id]",
				song.getCoverArt());
		Picasso.with(this).load(url)
				.placeholder(R.drawable.ic_music_note_black_48dp)
				.error(R.drawable.ic_launcher).into(songimage);

		update_nextBackButton();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	public void update_nextBackButton() {
		System.out.print("dddd");
		back.setVisibility(View.INVISIBLE);
		next.setVisibility(View.INVISIBLE);
		if (mapp.getMusicService().getSlectedindex() > 0) {
			back.setVisibility(View.VISIBLE);
		}
		if (mapp.getMusicService().getSlectedindex() < mapp.getMusicService()
				.getSongstoPlay().size() - 1) {
			next.setVisibility(View.VISIBLE);
		}

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				app2 mapp = (app2) v.getContext().getApplicationContext();
				mapp.getMusicService().catchActions(
						mapp.getMusicService().ACTION_PREVIOUS);
			}
		});
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View qv) {
				app2 mapp = (app2) qv.getContext().getApplicationContext();
				mapp.getMusicService().catchActions(
						mapp.getMusicService().ACTION_NEXT);
			}
		});

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		GoogleAnalytics.getInstance(PlayerActivity.this).reportActivityStart(
				this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		GoogleAnalytics.getInstance(PlayerActivity.this).reportActivityStop(
				this);
		mapp.setPlayerActivity(null);
	}

	protected void onResume() {
		super.onResume();
		update_nextBackButton();
		mapp.setCurrentActivity(this);
		app2.setPlayerActivity(this);
		song song = app2.getMusicService().getSongstoPlay()
				.get(app2.getMusicService().getSlectedindex());
		switch (app2.getMusicService().getStat()) {
		case Retreving:
			setLoadingView(song);
			break;
		case Playing:
			setPlayingView(song);
			break;
		case Pause:
			setPauseView(song);
			break;
		case stop:
			break;
		}
	}

	public void onOverflowPressed(final View v, final song track) {
		// TODO Auto-generated method stub
		LocalePopupMenu popup = new LocalePopupMenu(this, v);
		popup.inflate(R.menu.menu_player_more);
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent();
				song playedsong = mapp.getMusicService().getSongstoPlay()
						.get(mapp.getMusicService().getSlectedindex());
				switch (item.getItemId()) {

				case R.id.save_quee:
					Staticui.create_playlist(getme(), mapp.getMusicService()
							.getSongstoPlay());
					break;

				case R.id.add_playlist:
					Staticui.show_add_playlist_view(
							getme(),
							mapp.getMusicService()
									.getSongstoPlay()
									.get(mapp.getMusicService()
											.getSlectedindex()));
					break;
				case R.id.go_artist:
					artist d = new artist(playedsong.getArtistID(), playedsong
							.getArtist(), playedsong.getArtistArt());
					intent.putExtra("artist", d.toJson());
					mapp.getBaseactivity().startfragment(intent);
					finish();
					break;
				case R.id.go_album:
					album album = new album(playedsong.getAlbumID(), playedsong
							.getAlbum(), playedsong.getArtist(), playedsong
							.getArtistID(), playedsong.getCoverArt(),
							playedsong.getArtistArt());

					Intent myIntent = new Intent(
							getme(),
							com.tyolar.inc.musica.activities.Album_Activity.class);
					myIntent.putExtra("album", album.toJson());
					getme().startActivity(myIntent);

					finish();
					break;
				default:
					return false;
				}

				return false;
			}
		});

		popup.show();
	}

	protected Activity getme() {
		// TODO Auto-generated method stub
		return this;
	}

	public PlayerActivity getcontext() {
		return this;
	}

	private void showhideplayedmusic() {
		app2 mapp = (app2) getApplication();

		if (SongsListView.getVisibility() == View.VISIBLE)
			SongsListView.setVisibility(View.GONE);
		else {
			SongsListView.setVisibility(View.VISIBLE);
			// SongsListView.setSelection(mapp.getMusicaService()
			// .getSelectedtrackindex());
			SongsListView.setDropListener(mDropListener);
		}

	}

	private void setupBottomAds() {
		AdRegistration.enableLogging(false);
		// AdRegistration.enableTesting(false);
		try {
			AdRegistration.setAppKey(mapp.getAdmob_fullscreen_unit());
		} catch (final IllegalArgumentException e) {
			Log.e("Ads", e.getMessage());
			return;
		}

		AdLayout adView = new AdLayout(this);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.adsview);
		// Ensure the ad scales to fill the screen width.
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.addView(adView, lp);

		// Asynchronous task to retrieve an ad.
		adView.loadAd(new AdTargetingOptions());
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// NotificationManager mNM = (NotificationManager)
		// getSystemService(NOTIFICATION_SERVICE);
		// mNM.cancelAll();
		super.onDestroy();
	}

	private TouchInterceptor.DropListener mDropListener = new TouchInterceptor.DropListener() {
		public void drop(int from, int to) {
			// update the currently playing list
			// Collections.swap(mapp.getMusicaService().getSongtoplay(), from,
			// to);
			// SongsListView.setAdapter(new
			// List_Song_Player_Adapter(getcontext(),
			// mapp.getMusicaService().getSongtoplay()));
		}
	};

}
