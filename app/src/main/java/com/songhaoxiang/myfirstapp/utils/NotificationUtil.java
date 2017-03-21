package com.songhaoxiang.myfirstapp.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.songhaoxiang.myfirstapp.MainActivity;
import com.songhaoxiang.myfirstapp.R;
import com.songhaoxiang.myfirstapp.vo.Mp3Info;

import java.util.logging.Handler;

/**
 * Created by user on 2016/6/29.
 */
public class NotificationUtil {
	String title = null;
	String singer = null;
	Bitmap albumBitmap;
	Context context;
	boolean isPlaying;
	boolean isAppOnForeground;

	public static final String ACTION_BUTTON = "action_button";
	public static final String NOTIFY_BUTTON_TAG = "notify_button_tag";
	public static final String NOTIFY_PLAY_PAUSE = "notify_play_pause";
	public static final String NOTIFY_NEXT = "notify_next";
	public static final String NOTIFY_STOP = "notify_stop";
	public static final int NOTIFY_ID = 0x1;

	public void NotificationUtil() {

	}

	public void refresh(Context context, boolean isNetmusic, Mp3Info mp3Info, NetMusic netMp3Info, boolean isPlaying, boolean isAppOnForeground) {
		if (isNetmusic)
			updateNetNotification(context, netMp3Info, isPlaying, isAppOnForeground);
		else
			updateNotification(context, mp3Info, isPlaying, isAppOnForeground);

	}

	public void updateNotification(Context context, Mp3Info mp3Info, boolean isPlaying, boolean isAppOnForeground) {
		title = mp3Info.getTitle();
		singer = mp3Info.getArtist();
		albumBitmap = MediaUtils.getArtwork(context, mp3Info.getId(), mp3Info.getAlbumId(), true, false);
		commonSettingNotification(context, isPlaying, isAppOnForeground);
//        if (title.length() > 7) {
//            title = title.substring(0, 6) + "...";
//        }
//        if (singer.length() > 7) {
//            singer = singer.substring(0, 6);
//        }


	}

	public void updateNetNotification(Context context, NetMusic netMp3Info, boolean isPlaying, boolean isAppOnForeground) {
		this.context = context;
		this.isPlaying = isPlaying;
		this.isAppOnForeground = isAppOnForeground;
		title = netMp3Info.getSongname();
		singer = netMp3Info.getSingername();
		final String album_pic_url = netMp3Info.getAlbumpic_big();
		new Thread(new Runnable() {
			@Override
			public void run() {
				BitmapUtil bitmapUtil = new BitmapUtil(album_pic_url);
				albumBitmap = bitmapUtil.getNetImage(album_pic_url);
				Message message = new Message();
				message.what = 0x1;
				handler.sendMessage(message);
			}
		}).start();
	}

	private android.os.Handler handler = new android.os.Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0x1:
					commonSettingNotification(context, isPlaying, isAppOnForeground);
					break;
			}
		}
	};

	private void commonSettingNotification(Context context, boolean isPlaying, boolean isAppOnForeground) {
		final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
		remoteViews.setTextViewText(R.id.notify_textView_songName, title);
		remoteViews.setTextViewText(R.id.notify_textView_singer, singer);
		if (isPlaying) {
			remoteViews.setImageViewResource(R.id.imageView_play_pause_notify, R.mipmap.notifi_pause);
		} else {
			remoteViews.setImageViewResource(R.id.imageView_play_pause_notify, R.mipmap.notifi_play);
		}
		if (albumBitmap != null) {
			remoteViews.setImageViewBitmap(R.id.imageView_music_icon_notify, albumBitmap);
		}
		if (!isAppOnForeground) {
			Intent activityIntent = new Intent(context, MainActivity.class);
			PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
			remoteViews.setOnClickPendingIntent(R.id.imageView_music_icon_notify, activityPendingIntent);
			remoteViews.setOnClickPendingIntent(R.id.linearLayout_notify, activityPendingIntent);
		}
		Intent buttonIntent = new Intent(ACTION_BUTTON);
		buttonIntent.putExtra(NOTIFY_BUTTON_TAG, NOTIFY_PLAY_PAUSE);
		PendingIntent pendButtonIntent_play = PendingIntent.getBroadcast(context, 0, buttonIntent, 0);
		remoteViews.setOnClickPendingIntent(R.id.imageView_play_pause_notify, pendButtonIntent_play);

		buttonIntent.putExtra(NOTIFY_BUTTON_TAG, NOTIFY_NEXT);
		PendingIntent pendButtonIntent_next = PendingIntent.getBroadcast(context, 1, buttonIntent, 0);
		remoteViews.setOnClickPendingIntent(R.id.imageView_next_notify, pendButtonIntent_next);

		buttonIntent.putExtra(NOTIFY_BUTTON_TAG, NOTIFY_STOP);
		PendingIntent pendButtonIntent_stop = PendingIntent.getBroadcast(context, 2, buttonIntent, 0);
		remoteViews.setOnClickPendingIntent(R.id.imageView_stop_notify, pendButtonIntent_stop);

		builder.setContent(remoteViews);
		builder.setSmallIcon(R.mipmap.ic_launcher);
		builder.setTicker("baoBaoMusic");
		manager.notify(NOTIFY_ID, builder.build());
	}


}
