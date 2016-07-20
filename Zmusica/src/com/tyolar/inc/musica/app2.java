package com.tyolar.inc.musica;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.tyolar.inc.musica.Services.AutoMediaBrowserService;
import com.tyolar.inc.musica.fragments.Mini_Player_View;
import com.tyolar.inc.musica.model.PlayList;

public class app2 extends Application implements ActivityLifecycleCallbacks {
	public static boolean isFullPlayerisshown;
	private static BaseActivity baseactivity;
	public static PlayerActivity PlayerActivity;
	public static MediaPlayer mMediaPlayer;
	private static AutoMediaBrowserService MusicService;
	private static PlayList playlist;
	public CFragment CFragment;
	public Mini_Player_View mini_player=null;
	public NotificationCompat.Builder builder=null;
	private static String admob_banner_unit=null;
	private static String admob_fullscreen_unit=null;

	private static String angami_id = "i2:dikcfiel:893o4q52493n6n41:ecdhdddidccgce:ZN:d:ra:n1.7.8:qqrq366snr";
	// analystics
	public static final String TAG = app2.class.getSimpleName();
	private static app2 mInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		newMediaPlaye();
		mInstance = this;
		AnalyticsTrackers.initialize(this);
		AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
		registerActivityLifecycleCallbacks(this);
	}
	public static String getAdmob_banner_unit() {
		return admob_banner_unit;
	}

	public static void setAdmob_banner_unit(String admob_banner_unit) {
		app2.admob_banner_unit = admob_banner_unit;
	}

	public static String getAdmob_fullscreen_unit() {
		return admob_fullscreen_unit;
	}

	public static void setAdmob_fullscreen_unit(String admob_fullscreen_unit) {
		app2.admob_fullscreen_unit = admob_fullscreen_unit;
	}
	public void clearMediaPlayer(){
		mMediaPlayer=null;
	}

	
	public void newMediaPlaye(){
		mMediaPlayer=new MediaPlayer();
	}

	public static AutoMediaBrowserService getMusicService() {
		return MusicService;
	}
	

	public void setMusicService(AutoMediaBrowserService musicService) {
		MusicService = musicService;
	}

	public static PlayList getPlaylist() {
		return playlist;
	}

	public static void setPlaylist(PlayList playlist) {
		app2.playlist = playlist;
	}

	public static String getAngami_id() {
		return angami_id;
	}

	public static PlayerActivity getPlayerActivity() {
		return PlayerActivity;
	}

	public static void setPlayerActivity(PlayerActivity playerActivity) {
		PlayerActivity = playerActivity;
	}

	public static void setAngami_id(String angami_id) {
		app2.angami_id = angami_id;
	}

	private Activity mCurrentActivity = null;

	public Activity getCurrentActivity() {
		return mCurrentActivity;
	}

	public void setCurrentActivity(Activity mCurrentActivity) {
		this.mCurrentActivity = mCurrentActivity;
	}

	public static BaseActivity getBaseactivity() {
		return baseactivity;
	}

	public static void setBaseactivity(BaseActivity baseactivity) {
		app2.baseactivity = baseactivity;
	}

	// Activity Listner

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityStarted(Activity activity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityResumed(Activity activity) {
		// TODO Auto-generated method stub
		if (activity instanceof PlayerActivity) {
			isFullPlayerisshown = true;
		}
	}

	@Override
	public void onActivityPaused(Activity activity) {
		// TODO Auto-generated method stub
		if (activity instanceof PlayerActivity) {
			isFullPlayerisshown = false;
		}
	}

	@Override
	public void onActivityStopped(Activity activity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		// TODO Auto-generated method stub

	}

	// Anlaystics Area

	public static synchronized app2 getInstance() {
		return mInstance;
	}

	public synchronized Tracker getGoogleAnalyticsTracker() {
		AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
		return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
	}

	/***
	 * Tracking screen view
	 * 
	 * @param screenName
	 *            screen name to be displayed on GA dashboard
	 */
	public void trackScreenView(String screenName) {
		Tracker t = getGoogleAnalyticsTracker();

		// Set screen name.
		t.setScreenName(screenName);

		// Send a screen view.
		t.send(new HitBuilders.ScreenViewBuilder().build());

		GoogleAnalytics.getInstance(this).dispatchLocalHits();
	}

	/***
	 * Tracking exception
	 * 
	 * @param e
	 *            exception to be tracked
	 */
	public void trackException(Exception e) {
		if (e != null) {
			Tracker t = getGoogleAnalyticsTracker();

			t.send(new HitBuilders.ExceptionBuilder()
					.setDescription(
							new StandardExceptionParser(this, null)
									.getDescription(Thread.currentThread()
											.getName(), e)).setFatal(false)
					.build());
		}
	}

	/***
	 * Tracking event
	 * 
	 * @param category
	 *            event category
	 * @param action
	 *            action of the event
	 * @param label
	 *            label
	 */
	public void trackEvent(String category, String action, String label) {
		Tracker t = getGoogleAnalyticsTracker();

		// Build and send an Event.
		t.send(new HitBuilders.EventBuilder().setCategory(category)
				.setAction(action).setLabel(label).build());
	}
}
