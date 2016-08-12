package com.tyolar.inc.musica.Services;

import java.io.IOException;
import java.util.ArrayList;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

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
	public static final String ACTION_NOTI_CLICK = "ACTION_NOTI_CLICK";
	public static final String ACTION_NOTI_REMOVE = "ACTION_NOTI_REMOVE";
	   
	private Context context;
	boolean pauseiscalled;
	private int slectedindex = 0;
	int numMessages = 0;
	private NotificationHandler notificationHandler;
	private MediaplayerState Stat = MediaplayerState.Retreving;
	private ArrayList<song> SongstoPlay = new ArrayList<song>();

	app2 mapp;
	 private BroadcastReceiver playerServiceBroadcastReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            try {
	                handleBroadcastReceived(context, intent);
	            } catch (IOException e) {
	                e.printStackTrace();
	              }
	        }
	    };
	
	    private void updateNotificationPlayer() {
	        if (!notificationHandler.isNotificationActive())
	            notificationHandler.setNotificationPlayer(false);
	        song song = getSongstoPlay().get(getSlectedindex());
	        notificationHandler.changeNotificationDetails(song, 
	        		getMediaPlayer().isPlaying());
	    }

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		mapp = (app2) getApplication();
		mapp.setMusicService(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;

	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		context = this;
	        IntentFilter filter = new IntentFilter();
	        filter.addAction(ACTION_PLAY);
	        filter.addAction(ACTION_NEXT);
	        filter.addAction(ACTION_PAUSE);
	        filter.addAction(ACTION_PREVIOUS);
	        filter.addAction(ACTION_NOTI_CLICK);
	        filter.addAction(ACTION_NOTI_REMOVE);
	        registerReceiver(playerServiceBroadcastReceiver, filter);
	        notificationHandler = new NotificationHandler(context, this,mapp);
	        return START_NOT_STICKY;
		}
	@Override
	public boolean onUnbind(Intent intent) {
		
		return super.onUnbind(intent);
	}
	
	
	public boolean isOnlySongInQueue() {
		if (getSlectedindex()==0 && getSongstoPlay().size()==1)
			return true;
		else
			return false;
		
}
	public void catchActions(String action){
		Intent  y=new Intent();
		
		y.setAction(action);
		try {
			handleBroadcastReceived(this,y);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private void handleBroadcastReceived(Context context, final Intent intent) throws IOException {
       String a=intent.getAction();
      
		switch (intent.getAction()) {
            case ACTION_PLAY:
            	Play();
				showPlayView();
			    break;
            case ACTION_PAUSE:
            	
            	pause();
		
            	showPauseView();
                break;
            case ACTION_PREVIOUS:
            	setSlectedindex(getSlectedindex() - 1);
				Play();
                break;
            case ACTION_NEXT:
            	setSlectedindex(getSlectedindex() + 1);
				Play();
                break;
            case ACTION_STOP:
                break;
           
        }
    }
	
	
	
	

	public void refrechNextPrevouse_ondelete() {
	
		if (mapp.getPlayerActivity() != null) {
			mapp.getPlayerActivity().update_nextBackButton();
		}

		switch (getStat()) {
		case Retreving:
			break;
		case Playing:
			catchActions(ACTION_PLAY);
			break;
		case Pause:
			catchActions(ACTION_PAUSE);
			break;
		case stop:

			break;
		}

	}


	

	public void pause() {
		getMediaPlayer().pause();
	}

	

	public void showPlayView() {
		setStat(MediaplayerState.Playing);
		updateNotificationPlayer();

		song song = getSongstoPlay().get(getSlectedindex());
		mapp.mini_player.setsong(song);
		if (mapp.getPlayerActivity() != null) {
			mapp.getPlayerActivity().setPlayingView(song);
		}
	}

	public void showPauseView() {
		setStat(MediaplayerState.Pause);
		updateNotificationPlayer();
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
						catchActions(ACTION_PLAY)	;
					} else{
					catchActions(ACTION_PAUSE)	;
					
					}
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
		if(slectedindex > 0)
		this.slectedindex = slectedindex;
		else
			this.slectedindex = 0;
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