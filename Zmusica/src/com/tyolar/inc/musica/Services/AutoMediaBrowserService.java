package com.tyolar.inc.musica.Services;

import java.util.ArrayList;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.NotificationCompat;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tyolar.inc.musica.PlayerActivity;
import com.tyolar.inc.musica.R;
import com.tyolar.inc.musica.app2;
import com.tyolar.inc.musica.model.apiurls;
import com.tyolar.inc.musica.model.song;
import com.tyolar.inc.musica.utils.tools;

/**
 * Created by paulruiz on 2/13/15.
 */
public class AutoMediaBrowserService extends Service {

	public static final String ACTION_PLAY = "action_play";
	public static final String ACTION_PAUSE = "action_pause";
	public static final String ACTION_NEXT = "action_next";
	public static final String ACTION_PREVIOUS = "action_previous";
	public static final String ACTION_STOP = "action_stop";
	boolean pauseiscalled;
	private int slectedindex = 0;
	int numMessages = 0;

	private MediaplayerState Stat = MediaplayerState.Retreving;
	private ArrayList<song> SongstoPlay = new ArrayList<song>();
	private MediaSessionManager mManager;
	private MediaSession mSession;
	app2 mapp;
	private MediaController mController;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		initMediaSessions();
		mapp = (app2) getApplication();
		mapp.setMusicService(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;

	}

	public void handleAction(String action) {

		if (action.equalsIgnoreCase(ACTION_PLAY)) {
			mController.getTransportControls().play();
		} else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
			mController.getTransportControls().pause();
		} else if (action.equalsIgnoreCase(ACTION_PREVIOUS)) {
			mController.getTransportControls().skipToPrevious();
		} else if (action.equalsIgnoreCase(ACTION_NEXT)) {
			mController.getTransportControls().skipToNext();
		} else if (action.equalsIgnoreCase(ACTION_STOP)) {
			mController.getTransportControls().stop();
		}
	}

	private void handleIntent(Intent intent) {
		if (intent == null || intent.getAction() == null)
			return;
		String action = intent.getAction();
		handleAction(action);

	}

	private NotificationCompat.Action generateAction(int icon, String title,
			String intentAction) {
		Intent intent = new Intent(getApplicationContext(),
				AutoMediaBrowserService.class);
		intent.setAction(intentAction);

		PendingIntent pendingIntent = PendingIntent.getService(
				getApplicationContext(), 1, intent, 0);
		return new NotificationCompat.Action.Builder(icon, title, pendingIntent)
				.build();
	}

	private void buildmusicNotification(NotificationCompat.Action action,
			song song) {
		int playPauseButtonPosition = 0;
		numMessages = 0;
		Intent myIntent = new Intent(getApplicationContext(),
				PlayerActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, myIntent,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		String url = new apiurls().getArtimage();
		url = url.replace("[sid]", mapp.getAngami_id()).replace("[id]",
				song.getCoverArt());

		if (mapp.builder == null) {
			mapp.builder = new NotificationCompat.Builder(this);
		}

		mapp.builder.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(song.getTitle())
				.setContentText(song.getArtist()).setAutoCancel(false)
				.setContentIntent(pendingIntent).setOngoing(true);

		Target mTarget = new Target() {
			@Override
			public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
				// Do whatever you want with the Bitmap
				mapp.builder.setLargeIcon(bitmap);
			}

			@Override
			public void onBitmapFailed(Drawable errorDrawable) {
			}

			@Override
			public void onPrepareLoad(Drawable placeHolderDrawable) {
			}
		};
		Picasso.with(this).load(url).into(mTarget);
		mapp.builder.mActions.clear();
		if (slectedindex > 0) {
			playPauseButtonPosition = 1;
			mapp.builder.addAction(generateAction(
					android.R.drawable.ic_media_previous, "", ACTION_PREVIOUS));
		} else {
			mapp.builder.addAction(new NotificationCompat.Action(0, "", null));
		}

		mapp.builder.addAction(action);

		if (slectedindex < getSongstoPlay().size() - 1) {
			playPauseButtonPosition = 1;
			mapp.builder.addAction(generateAction(
					android.R.drawable.ic_media_next, "", ACTION_NEXT));

		} else {
			mapp.builder.addAction(new NotificationCompat.Action(0, "", null));
		}

		mapp.builder.setStyle(new NotificationCompat.MediaStyle());
	}

	private void buildNotification(NotificationCompat.Action action) {
		int ActionsButtonPosition = 0;
		song song = getSongstoPlay().get(getSlectedindex());
		buildmusicNotification(action, song);
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, mapp.builder.build());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mManager == null) {
			initMediaSessions();
		}

		handleIntent(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	public void refrechNextPrevouse_ondelete() {
		System.out.print("dddd");
		if (mapp.getPlayerActivity() != null) {
			mapp.getPlayerActivity().update_nextBackButton();
		}

		switch (getStat()) {
		case Retreving:
			break;
		case Playing:
			buildNotification(generateAction(android.R.drawable.ic_media_pause,
					"", ACTION_PAUSE));
			break;
		case Pause:
			buildNotification(generateAction(android.R.drawable.ic_media_play,
					"", ACTION_PLAY));
			break;
		case stop:

			break;
		}

	}

	private void initMediaSessions() {
		mSession = new MediaSession(getApplicationContext(),
				"simple player session");
		mController = new MediaController(getApplicationContext(),
				mSession.getSessionToken());
		mSession.setCallback(new MediaSession.Callback() {
			@Override
			public void onPlay() {
				super.onPlay();
				Play();
				showPlayView();

			}

			@Override
			public void onPause() {
				super.onPause();
				pause();
				showPauseView();
			}

			@Override
			public void onSkipToNext() {
				super.onSkipToNext();
				setSlectedindex(getSlectedindex() + 1);
				Play();
			}

			@Override
			public void onSkipToPrevious() {
				super.onSkipToPrevious();
				setSlectedindex(getSlectedindex() - 1);
				Play();
			}

			@Override
			public void onStop() {
				super.onStop();
				// Stop media player here
				NotificationManager notificationManager = (NotificationManager) getApplicationContext()
						.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.cancel(1);
				Intent intent = new Intent(getApplicationContext(),
						AutoMediaBrowserService.class);
				stopService(intent);
			}

		});
	}

	@Override
	public boolean onUnbind(Intent intent) {
		mSession.release();
		return super.onUnbind(intent);
	}

	public void pause() {
		getMediaPlayer().pause();
	}

	public void showLoading(song song) {
		setStat(MediaplayerState.Retreving);
		mapp.mini_player.showLoading(song);
		if (mapp.builder == null) {
			mapp.builder = new NotificationCompat.Builder(this);
		}
		mapp.builder.mActions.clear();
		mapp.builder
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(
						getApplication().getResources().getString(
								R.string.loading))
				.setContentText(song.getTitle()).setAutoCancel(false)
				.setOngoing(true);
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, mapp.builder.build());
		if (mapp.getPlayerActivity() != null) {
			mapp.getPlayerActivity().setLoadingView(song);
		}
	}

	public void showPlayView() {
		setStat(MediaplayerState.Playing);
		buildNotification(generateAction(android.R.drawable.ic_media_pause, "",
				ACTION_PAUSE));

		song song = getSongstoPlay().get(getSlectedindex());
		mapp.mini_player.setsong(song);
		if (mapp.getPlayerActivity() != null) {
			mapp.getPlayerActivity().setPlayingView(song);
		}
	}

	public void showPauseView() {
		setStat(MediaplayerState.Pause);
		buildNotification(generateAction(android.R.drawable.ic_media_play, "",
				ACTION_PLAY));
		song song = getSongstoPlay().get(getSlectedindex());
		mapp.mini_player.Pause();
		pauseiscalled = true;
		if (mapp.getPlayerActivity() != null) {
			mapp.getPlayerActivity().setPauseView(song);
		}
	}

	public void Play() {
		if (pauseiscalled) {
			getMediaPlayer().start();
		} else {
			getMediaPlayer().pause();
			relaxResources(true);
			song song = getSongstoPlay().get(getSlectedindex());
			showLoading(song);
			try {
				createMediaPlayerIfNeeded();
				setStat(MediaplayerState.Retreving);
				String uri = new apiurls().getAudiourl();
				uri = uri.replace("[id]", song.getId()).replace("[sid]",
						mapp.getAngami_id());
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.permitAll().build();
				StrictMode.setThreadPolicy(policy);
				getMediaPlayer().setDataSource(
						tools.getLocation(uri.toString()));
				getMediaPlayer().prepareAsync();
			} catch (Exception ex) {

				ShowError();
			}
		}
		pauseiscalled = false;
	}

	private void ShowError() {

	}

	private void createMediaPlayerIfNeeded() {
		mapp.newMediaPlaye();
		getMediaPlayer().setAudioStreamType(AudioManager.STREAM_MUSIC);
		// mMediaPlayer.setWakeMode(getApplicationContext(),
		// PowerManager.PARTIAL_WAKE_LOCK);
		getMediaPlayer().setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mp.start();
				showPlayView();

			}
		});
		getMediaPlayer().setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				if (getMediaPlayer().getCurrentPosition() > 7) {

					if (getSlectedindex() < getSongstoPlay().size() - 1) {
						setSlectedindex(getSlectedindex() + 1);
						Play();
					} else
						showPauseView();
				}

			}
		});

	}

	private void relaxResources(boolean releaseMediaPlayer) {
		if (releaseMediaPlayer && getMediaPlayer() != null) {
			getMediaPlayer().reset();
			getMediaPlayer().release();
			mapp.clearMediaPlayer();
		}
	}

	public boolean isPlaying() {
		return (getMediaPlayer() != null && getMediaPlayer().isPlaying());
	}

	public int getSlectedindex() {
		return slectedindex;
	}

	public void setSlectedindex(int slectedindex) {
		this.slectedindex = slectedindex;
	}

	public MediaplayerState getStat() {
		return Stat;
	}

	public void setStat(MediaplayerState stat) {

		this.Stat = stat;
		switch (Stat) {
		case Retreving:
			break;
		case Playing:
			// handleAction(ACTION_PLAY);
			break;
		case Pause:
			// handleAction(ACTION_PAUSE);
			// pause();
			// showPauseView();
			break;
		case stop:
			break;
		}

	}

	public MediaPlayer getMediaPlayer() {
		return mapp.mMediaPlayer;

	}

	public ArrayList<song> getSongstoPlay() {
		return SongstoPlay;
	}

	public void setSongstoPlay(ArrayList<song> songstoPlay) {
		SongstoPlay = songstoPlay;
	}

}