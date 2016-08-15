package com.tyolar.inc.musica.Services;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;

import android.support.annotation.DrawableRes;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RemoteViews;
import android.support.v7.graphics.Palette;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tyolar.inc.musica.R;
import com.tyolar.inc.musica.app2;
import com.tyolar.inc.musica.model.apiurls;
import com.tyolar.inc.musica.model.song;

import java.io.File;

/**
 * Created by architjn on 15/12/15.
 */
public class NotificationHandler {

    private static final int NOTIFICATION_ID = 272448;
    private Context context;
    private AutoMediaBrowserService service;
    private boolean notificationActive;
    private app2 mapp;
    private Notification notificationCompat;
    private NotificationManager notificationManager;

    public NotificationHandler(Context context, AutoMediaBrowserService service,app2 mapp) {
        this.context = context;
        this.service = service;
        this.mapp=mapp;
        
    }

    private Notification.Builder createBuiderNotification(boolean removable) {
        Intent notificationIntent = new Intent();
        notificationIntent.setAction(AutoMediaBrowserService.ACTION_NOTI_CLICK);
        PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);
        Intent deleteIntent = new Intent();
        deleteIntent.setAction(AutoMediaBrowserService.ACTION_NOTI_REMOVE);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context, 0, deleteIntent, 0);
        if (removable)
            return new Notification.Builder(context)
                    .setOngoing(false)
                    .setSmallIcon(R.drawable.ic_launcher_white)
                    .setContentIntent(contentIntent)
                    .setDeleteIntent(deletePendingIntent);
        else
            return new Notification.Builder(context)
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_launcher_white)
                    .setContentIntent(contentIntent)
                    .setDeleteIntent(deletePendingIntent);
    }

    public void setNotificationPlayer(boolean removable) {
        notificationCompat = createBuiderNotification(removable).build();
        RemoteViews notiLayoutBig = new RemoteViews(context.getPackageName(),
                R.layout.notification_layout);
        RemoteViews notiCollapsedView = new RemoteViews(context.getPackageName(),
                R.layout.notification_small);
        if (Build.VERSION.SDK_INT >= 16) {
            notificationCompat.bigContentView = notiLayoutBig;
        }
        notificationCompat.contentView = notiCollapsedView;
        notificationCompat.priority = Notification.PRIORITY_MAX;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (!removable)
            service.startForeground(NOTIFICATION_ID, notificationCompat);
        notificationManager.notify(NOTIFICATION_ID, notificationCompat);
        notificationActive = true;
    }


    public void changeNotificationDetails(song song, boolean playing) {
        if (Build.VERSION.SDK_INT >= 16) {
            notificationCompat.bigContentView.setTextViewText(R.id.noti_name, song.getTitle());
            notificationCompat.bigContentView.setTextViewText(R.id.noti_artist, song.getArtist());
            notificationCompat.contentView.setTextViewText(R.id.noti_name, song.getTitle());
            notificationCompat.contentView.setTextViewText(R.id.noti_artist, song.getArtist());
            Intent prevClick = new Intent();
            prevClick.setAction(AutoMediaBrowserService.ACTION_PREVIOUS);
            PendingIntent prevClickIntent = PendingIntent.getBroadcast(context, 21121, prevClick, 0);
            notificationCompat.bigContentView.setOnClickPendingIntent(R.id.noti_prev_button, prevClickIntent);
            notificationCompat.contentView.setOnClickPendingIntent(R.id.noti_prev_button, prevClickIntent);
        
            notificationCompat.contentView.setViewVisibility(R.id.noti_prev_button, View. INVISIBLE);
    		notificationCompat.bigContentView.setViewVisibility(R.id.noti_prev_button, View. INVISIBLE);
    		notificationCompat.contentView.setViewVisibility(R.id.noti_next_button, View. INVISIBLE);
    		notificationCompat.bigContentView.setViewVisibility(R.id.noti_next_button, View. INVISIBLE);
    		
        	if (mapp.getMusicService().getSlectedindex() > 0) {
        		notificationCompat.contentView.setViewVisibility(R.id.noti_prev_button, View. VISIBLE);
        		notificationCompat.bigContentView.setViewVisibility(R.id.noti_prev_button, View. VISIBLE);
        		
        	
        	}
    		if (mapp.getMusicService().getSlectedindex() < mapp.getMusicService()
    				.getSongstoPlay().size() - 1) {
    			notificationCompat.contentView.setViewVisibility(R.id.noti_next_button, View. VISIBLE);
        		notificationCompat.bigContentView.setViewVisibility(R.id.noti_next_button, View. VISIBLE);
        		
    		}

            
            
            Intent nextClick = new Intent();
            nextClick.setAction(AutoMediaBrowserService.ACTION_NEXT);
            PendingIntent nextClickIntent = PendingIntent.getBroadcast(context, 21221, nextClick, 0);
            notificationCompat.bigContentView.setOnClickPendingIntent(R.id.noti_next_button, nextClickIntent);
            notificationCompat.contentView.setOnClickPendingIntent(R.id.noti_next_button, nextClickIntent);
           
            String url = new apiurls().getArtimage();
            String path  = url.replace("[sid]", mapp.getAngami_id()).replace("[id]",
    				song.getCoverArt());
            
         
            int playStateRes;
            if (playing){
                playStateRes = R.drawable.ic_fab_pause_btn_normal;
                Intent playClick = new Intent();
                playClick.setAction(AutoMediaBrowserService.ACTION_PAUSE);
                PendingIntent playClickIntent = PendingIntent.getBroadcast(context, 21021, playClick, 0);
                notificationCompat.bigContentView.setOnClickPendingIntent(R.id.noti_play_button, playClickIntent);
                notificationCompat.contentView.setOnClickPendingIntent(R.id.noti_play_button, playClickIntent);
               
            
            }
            else{
                playStateRes = R.drawable.ic_fab_play_btn_normal;
                Intent playClick = new Intent();
                playClick.setAction(AutoMediaBrowserService.ACTION_PLAY);
                PendingIntent playClickIntent = PendingIntent.getBroadcast(context, 21021, playClick, 0);
                notificationCompat.bigContentView.setOnClickPendingIntent(R.id.noti_play_button, playClickIntent);
                notificationCompat.contentView.setOnClickPendingIntent(R.id.noti_play_button, playClickIntent);
                   
            }
           
            notificationCompat.bigContentView
                    .setImageViewResource(R.id.noti_play_button, playStateRes);
            notificationCompat.contentView
                    .setImageViewResource(R.id.noti_play_button, playStateRes);
            if (path != null && !path.matches("")) {
                Picasso.with(context).load(path).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        notificationCompat.bigContentView.setImageViewBitmap(R.id.noti_album_art, bitmap);
                        notificationCompat.contentView.setImageViewBitmap(R.id.noti_album_art, bitmap);
                        notificationManager.notify(NOTIFICATION_ID, notificationCompat);
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
//                                notificationCompat.color = palette.getDarkVibrantColor(
//                                        palette.getDarkMutedColor(
//                                                palette.getMutedColor(0xffffffff)));
                                notificationManager.notify(NOTIFICATION_ID, notificationCompat);
                            }
                        });
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        setDefaultImageView();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
            } else {
                setDefaultImageView();
            }
        }
    }

    private void setDefaultImageView() {
      
        notificationCompat.bigContentView.setImageViewBitmap(R.id.noti_album_art,
                getBitmapOfVector(R.drawable.ic_launcher_white,
                        dpToPx(100), dpToPx(100)));
        notificationCompat.contentView.setImageViewBitmap(R.id.noti_album_art,
                getBitmapOfVector(R.drawable.ic_launcher_white,
                        dpToPx(50), dpToPx(50)));
        notificationManager.notify(NOTIFICATION_ID, notificationCompat);
    }
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    
    @SuppressLint("NewApi")
	public Bitmap getBitmapOfVector(@DrawableRes int id, int height, int width) {
        Drawable vectorDrawable = context.getDrawable(id);
        if (vectorDrawable != null)
            vectorDrawable.setBounds(0, 0, width, height);
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        if (vectorDrawable != null)
            vectorDrawable.draw(canvas);
        return bm;
    }
    public void updateNotificationView() {
        notificationManager.notify(NOTIFICATION_ID, notificationCompat);
    }

    public boolean isNotificationActive() {
        return notificationActive;
    }

    public void setNotificationActive(boolean notificationActive) {
        this.notificationActive = notificationActive;
    }

    public Notification getNotificationCompat() {
        return notificationCompat;
    }
}