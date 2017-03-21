package com.songhaoxiang.myfirstapp;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.songhaoxiang.myfirstapp.utils.MediaUtils;
import com.songhaoxiang.myfirstapp.utils.NetMusic;
import com.songhaoxiang.myfirstapp.utils.NotificationUtil;
import com.songhaoxiang.myfirstapp.vo.Mp3Info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, Runnable, MediaPlayer.OnBufferingUpdateListener {
	private MediaPlayer mediaPlayer;
	private NetMusic netMp3Info;
	private Mp3Info mp3Info;
	private NotificationUtil notificationUtil = new NotificationUtil();
	private ArrayList<Mp3Info> mp3Infos;
	private int netOnClickposition;
	private ArrayList<NetMusic> netmp3Infos;
	private int isJump = 0;//表示跳过了netplay的创建线程操作
	public boolean isPlaying = false;

	public boolean isNetmusic() {
		return isNetmusic;
	}

	private boolean isNetmusic = false;

	public int getLastPosition() {
		return lastPosition;
	}

	private int lastPosition = -1;
	private int currentPosition = -1;
	private int currentNetPosition = -1;
	private int CurrentProgress;
	public int duration;//时长
	private boolean isPrepared = true;
	private MusicUpdateListener musicUpdateListener;
	private ExecutorService es = Executors.newSingleThreadExecutor();

	public int getCurrentPosition() {
		return currentPosition;
	}

	public int getCurrentNetPosition() {
		return currentNetPosition;
	}

	public ArrayList<NetMusic> getNetmp3Infos() {
		return netmp3Infos;
	}

	public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener) {
		this.musicUpdateListener = musicUpdateListener;
	}

	//播放模式
	public static final int ORDER_PLAY = 1;
	public static final int RANDOM_PLAY = 2;
	public static final int SINGLE_PLAY = 3;
	public static final int NOTIFY_ID = 0x1;
	public static final String ACTION_BUTTON = "action_button";
	public static final String NOTIFY_BUTTON_TAG = "notify_button_tag";
	public static final String NOTIFY_PLAY_PAUSE = "notify_play_pause";
	public static final String NOTIFY_NEXT = "notify_next";
	public static final String NOTIFY_STOP = "notify_stop";
	private int play_mode = ORDER_PLAY;

	SharedPreferences.Editor editor = baoBaoMusicApplication.sharedPreferences.edit();

	public void setPlay_mode(int play_mode) {
		this.play_mode = play_mode;
	}

	public int getPlay_mode() {
		return play_mode;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				//System.out.println("prepare线程4**************" + Thread.currentThread());
				duration = mediaPlayer.getDuration();
				//System.out.println("prepare线程5**************" + Thread.currentThread());
				start();
				//System.out.println("prepare线程6**************" + Thread.currentThread());

				System.out.println("准备完毕！" + Thread.currentThread());
			}
		});
		mp3Infos = MediaUtils.getMp3Info(this);
		es.execute(updateStatusRunnable);
		baoBaoMusicApplication baoBaoMusicApplication = (com.songhaoxiang.myfirstapp.baoBaoMusicApplication) getApplication();
		lastPosition = baoBaoMusicApplication.sharedPreferences.getInt("lastPosition", -1);
		currentPosition = lastPosition;
		CurrentProgress = baoBaoMusicApplication.sharedPreferences.getInt("CurrentProgress", 0);
		play_mode = baoBaoMusicApplication.sharedPreferences.getInt("Play_Mode", 1);
		sendNotification();
		setOnReceiver();


	}

	private void setOnReceiver() {
		BroadcastReceiver onClickReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(ACTION_BUTTON)) {
					if (intent.getStringExtra(NOTIFY_BUTTON_TAG).equals(NOTIFY_PLAY_PAUSE)) {
						//在这里处理点击事件
						if (isPlaying()) {
							pause();
						} else {
							start();
						}
					} else {
						if (intent.getStringExtra(NOTIFY_BUTTON_TAG).equals(NOTIFY_NEXT)) {
							next();
						} else {
							if (intent.getStringExtra(NOTIFY_BUTTON_TAG).equals(NOTIFY_STOP)) {
								SysApplication.getInstance().exit();
								onDestroy();
							}
						}
					}
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_BUTTON);
		registerReceiver(onClickReceiver, filter);
	}


	private void sendNotification() {
		final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
		builder.setContent(remoteViews);
		builder.setSmallIcon(R.mipmap.ic_launcher);
		builder.setTicker("baoBaoMusic");
		manager.notify(NOTIFY_ID, builder.build());
		startForeground(1, builder.build());
	}


	public PlayService() {
	}


	public int getCurrentProgress() {
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				return mediaPlayer.getCurrentPosition();
			} else {
				return CurrentProgress;
			}
		}

		return 0;
	}


	private Random random = new Random();

	@Override
	public void onCompletion(MediaPlayer mp) {
		switch (play_mode) {
			case ORDER_PLAY:
				next();
				break;
			case RANDOM_PLAY:
				if (!isNetmusic) {
					play(random.nextInt(mp3Infos.size()));
				} else {
					netplay(random.nextInt(this.netmp3Infos.size()), this.netmp3Infos);
				}
				break;
			case SINGLE_PLAY:
				if (!isNetmusic) {

					play(currentPosition);
				} else {
					netplay(currentNetPosition, this.netmp3Infos);
				}
				break;
			default:
				break;
		}

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		mp.reset();
		return false;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		this.percent = percent;
		musicUpdateListener.onPublish(getCurrentProgress(), percent);
	}


	class playBinder extends Binder {
		public PlayService getPlayService() {
			return PlayService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		// throw new UnsupportedOperationException("Not yet implemented");
		return new playBinder();
	}

	public int getPercent() {
		return percent;
	}

	private int percent;
	Runnable updateStatusRunnable = new Runnable() {
		@Override
		public void run() {
			while (true) {
				if (musicUpdateListener != null && mediaPlayer != null) {
					CurrentProgress = getCurrentProgress();
					//System.out.println("执行到了这个更新时间的线程！！"+percent);
					musicUpdateListener.onPublish(CurrentProgress, percent);
					editor.putInt("CurrentProgress", CurrentProgress);
					editor.commit();
				}
				try {
					Thread.sleep(40);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	public void play(int position) {
		isPlaying = false;
		isNetmusic = false;
		currentNetPosition = -1;
		if (position >= 0 && position < mp3Infos.size()) {
			mp3Info = mp3Infos.get(position);
			if (position == lastPosition) {
				try {
					lastPosition = -1;
					mediaPlayer.reset();
					mediaPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));
					mediaPlayer.prepare();
					mediaPlayer.seekTo(CurrentProgress);
					currentPosition = position;
					start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				if (position == currentPosition) {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.pause();
					} else {
						start();
					}
				} else {
					try {
						mediaPlayer.reset();
						mediaPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));
						mediaPlayer.prepare();
						currentPosition = position;
						//start();

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			lastPosition = -1;
			editor.putInt("lastPosition", getCurrentPosition());
			editor.commit();
			changeUI(position);
		}

	}


	public void netplay(int position, ArrayList<NetMusic> netMp3Infos) {
		isNetmusic = true;
		isPlaying = false;
		currentPosition = -1;
		this.netmp3Infos = netMp3Infos;
		if (position >= 0 && position < netmp3Infos.size()) {
			// NetMusic netMp3Info = netmp3Infos.get(position);
			if (position == currentNetPosition) {
				if (isPrepared) {
					if (mediaPlayer.isPlaying() && isNetmusic) {
						mediaPlayer.pause();
					} else {
						start();
					}
				}
			} else {
				currentNetPosition = position;
				if (isPrepared) {
					isPrepared = false;
					Thread thread = new Thread(this);
					thread.start();
				}else {
					isJump ++;
				}
			}
			//changeUI(position);
		}
	}

	public void start() {
		if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
			mediaPlayer.start();
			isPlaying = true;
		}
		if (isNetmusic) {
			if (isJump!=0){
				isJump=0;
				isPrepared = false;
				Thread thread = new Thread(this);
				thread.start();
				return;
			}
			isPrepared = true;
			changeUI(getCurrentNetPosition());
		} else {
			changeUI(getCurrentPosition());
		}

		if (isNetmusic) {
			notificationUtil.updateNetNotification(getBaseContext(), netMp3Info, isPlaying(), isAppOnForeground());
		} else {
			notificationUtil.updateNotification(getBaseContext(), mp3Info, isPlaying(), isAppOnForeground());
		}


	}

	public void changeUI(int position) {
		if (musicUpdateListener != null) {
			musicUpdateListener.onChange(position);
		}
	}

	public void run() {
		try {
			netMp3Info = netmp3Infos.get(currentNetPosition);
			synchronized (mediaPlayer) {
				try {
					System.out.println("prepare线程1**************" + Thread.currentThread());
					mediaPlayer.reset();
					System.out.println("prepare线程2**************" + Thread.currentThread());
					mediaPlayer.setDataSource(this, Uri.parse(netMp3Info.getM4a()));
					System.out.println("prepare线程3**************" + Thread.currentThread());
					mediaPlayer.prepare();
				} catch (IllegalStateException e) {
					System.out.println("捕获到prepare线程异常**************" + Thread.currentThread());
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			System.out.println("捕获到prepare线程异常2**************");
			e.printStackTrace();
//			try {
////				mediaPlayer.setDataSource(this, Uri.parse(netMp3Info.getM4a()));
////				mediaPlayer.prepare();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
		}
		//System.out.println("**********线程结束！");
	}

	public void pause() {
		if (isPlaying) {
			CurrentProgress = getCurrentProgress();
			mediaPlayer.pause();
			isPlaying = false;
			if (isNetmusic) {
				notificationUtil.updateNetNotification(getBaseContext(), netMp3Info, isPlaying(), isAppOnForeground());
			} else {
				notificationUtil.updateNotification(getBaseContext(), mp3Info, isPlaying(), isAppOnForeground());
			}
			//  NotificationUtil.refresh(getApplicationContext(), isNetmusic, mp3Info, netMp3Info, isPlaying(), isAppOnForeground());
		}

	}

	public void next() {
		if (!isNetmusic) {
			changeUI(currentPosition);
			if (currentPosition >= mp3Infos.size() - 1) {
				currentPosition = -1;
			}
			if (RANDOM_PLAY == getPlay_mode()) {
				play(random.nextInt(mp3Infos.size()));
			} else {
				play(currentPosition + 1);
			}

		} else {
			if (currentNetPosition >= this.netmp3Infos.size() - 1) {
				currentNetPosition = 0;
			}
			if (RANDOM_PLAY == getPlay_mode()) {
				netplay(random.nextInt(this.netmp3Infos.size()), this.netmp3Infos);
			} else {
				netplay(currentNetPosition + 1, this.netmp3Infos);
			}
		}

	}

	public void upon() {
		if (!isNetmusic) {
			if (currentPosition <= 0) {
				currentPosition = mp3Infos.size();
			}
			play(currentPosition - 1);
		} else {
			if (currentNetPosition <= 0) {
				currentNetPosition = netmp3Infos.size();
			}
			netplay(currentNetPosition - 1, this.netmp3Infos);
		}

	}


	public int getDuration() {
		return mediaPlayer.getDuration();
	}

	public void seekTo(int msec) {
		mediaPlayer.seekTo(msec);

	}

	//更新状态接口
	public interface MusicUpdateListener {
		void onPublish(int Progress, int percent);

		void onChange(int Position);
	}


	public boolean isPlaying() {
		if (mediaPlayer != null) {
			return isPlaying;
		}
		return false;
	}

	//在进程中去寻找当前APP的信息，判断是否在前台运行
	private boolean isAppOnForeground() {
		ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(
				Context.ACTIVITY_SERVICE);
		String packageName = getApplicationContext().getPackageName();
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}

	public void onDestroy() {
		super.onDestroy();
		if (es != null && !es.isShutdown()) {
			es.shutdown();
			es = null;
		}

		editor.putInt("lastPosition", getCurrentPosition());
		editor.putInt("Play_Mode", getPlay_mode());
		editor.commit();
	}

}
