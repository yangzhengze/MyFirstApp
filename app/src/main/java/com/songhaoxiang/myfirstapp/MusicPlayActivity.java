package com.songhaoxiang.myfirstapp;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.songhaoxiang.myfirstapp.lrc.IfLrcView;
import com.songhaoxiang.myfirstapp.lrc.LrcView;
import com.songhaoxiang.myfirstapp.utils.BitmapUtil;
import com.songhaoxiang.myfirstapp.utils.LRCUtil;
import com.songhaoxiang.myfirstapp.utils.MediaUtils;
import com.songhaoxiang.myfirstapp.utils.NetMusic;
import com.songhaoxiang.myfirstapp.utils.ReaderJSONUtil;
import com.songhaoxiang.myfirstapp.vo.Mp3Info;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MusicPlayActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, Runnable {

    private RelativeLayout main_play_layout;
    private ImageView imageView6_change_bg, imageView_music_icon, imageView3_play_pause, imageView4_next, imageView5_upon, imageView2_play_mode, imageView_favorite,imageview_background;
    private TextView textView_main_songName, textView2_start_time, textView3_end_time, textview_loadLRC;
    private SeekBar seekBar;
    private ViewPager viewPager;
   // private Bitmap albumBitmap = null;
    private LrcView lrcView;
    private int newTime;
    private ArrayList<Mp3Info> mp3Infos;
    private ArrayList<View> views = new ArrayList<>();
    private boolean lrc_only_init = true;
    private int lastProgress;
    private boolean song_change = false;

    private int bg_id = 1;
    private float alpha = (float) 0.5;
    private static final int UPDATE_TIME = 0x1;
    private static MyHandler myHandler;
    private baoBaoMusicApplication app;
    private NetMusic mp3Info;
    SharedPreferences.Editor editor = baoBaoMusicApplication.sharedPreferences.edit();
    private int maxPeogress;
    private boolean lrcViewIsPrepared = false;
    //private Bitmap albumBitmap_gaosi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        initUI();
        mp3Infos = MediaUtils.getMp3Info(this);
        setUIOnClick();
        myHandler = new MyHandler(this);
        app = (com.songhaoxiang.myfirstapp.baoBaoMusicApplication) getApplication();
        bindPlayService();

    }

    public void onResume() {
        super.onResume();
        //bindPlayService();
    }

    @Override
    public void onPause() {
        super.onPause();
       // unbindPlayService();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
           // playService.pause();
            playService.seekTo(progress);
           // playService.start();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void run() {
        if (mp3Info.getAlbumpic_big() != null) {
            BitmapUtil bitmapUtil = new BitmapUtil(this);
            Bitmap albumBitmap = bitmapUtil.getNetImage(mp3Info.getAlbumpic_big());
           // System.out.println(albumBitmap.toString());
            //System.out.println(albumBitmap2.toString());
            Message message = new Message();
            message.obj = albumBitmap;
            message.what = 100;
            myHandler.sendMessage(message);
            Bitmap albumBitmap2 = bitmapUtil.getNetImage(mp3Info.getAlbumpic_big());
            Bitmap albumBitmap_gaosi = bitmapUtil.getGaosiBitmap(albumBitmap2);
            Message message1 = new Message();
            message1.obj = albumBitmap_gaosi;
            message1.what = 101;
            myHandler.sendMessage(message1);
        }
       // myHandler.sendEmptyMessage(100);
    }

    class MyHandler extends Handler {
        private MusicPlayActivity musicPlayActivity;

        public MyHandler(MusicPlayActivity musicPlayActivity) {
            this.musicPlayActivity = musicPlayActivity;

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (musicPlayActivity != null) {
                switch (msg.what) {
                    case UPDATE_TIME:
                        musicPlayActivity.textView2_start_time.setText(MediaUtils.formatTime(msg.arg1));
                        break;
                    case 100:
                        if (msg.obj != null) {
                            imageView_music_icon.setImageBitmap((Bitmap) msg.obj);
                        }
                        break;
                    case 101:
                        if(msg.obj!=null){
                            imageview_background.setImageBitmap((Bitmap) msg.obj);
                        }
                        break;
                    case 200:
                        textview_loadLRC.setVisibility(views.get(1).GONE);
                        lrcView.setVisibility(views.get(1).VISIBLE);
                        break;
                    case 300:
                        textview_loadLRC.setVisibility(views.get(1).VISIBLE);
                        lrcView.setVisibility(views.get(1).GONE);
                        break;
                    default:
                        break;
                }
            }
        }
    }


    @Override
    public void publish(int Progress, int percent) {
        Message msg = myHandler.obtainMessage(UPDATE_TIME);
        msg.arg1 = Progress;
        myHandler.sendMessage(msg);
        seekBar.setSecondaryProgress(maxPeogress * percent / 100);
        seekBar.setProgress(Progress);
        // System.out.println("执行到了更新第二进度的地方！！"+maxPeogress*percent/100+"   "+Progress);
        if (!lrcViewIsPrepared) {
            myHandler.sendEmptyMessage(300);
        } else {
            if (lrc_only_init) {
                if (song_change) {
                    if (playService.isNetmusic()) {
                        lrc_only_init = false;
                        lrcViewIsPrepared = false;
                        final NetMusic netMusic = playService.getNetmp3Infos().get(playService.getCurrentNetPosition());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                initLrcView(netMusic);
                            }
                        }).start();
                    }else{
                        //这里写本地音乐加载
                    }
                }
                //textview_loadLRC.setText("");
                if (lastProgress != Progress) {
                    newTime = lrcView.showLrc(Progress);
                    lrcView.postInvalidate();
                    //lrcView.setBackground(getDrawable(R.mipmap.icon));
                    lastProgress = Progress;
                }
            }
        }
    }


    @Override
    public void change(int Position) {
        System.out.println("*******执行了MusicPlayActivity的change方法*******");
        if (this.playService.isPlaying()) {
            imageView3_play_pause.setImageResource(R.mipmap.main_pause);
        } else {
            imageView3_play_pause.setImageResource(R.mipmap.main_play);
        }
        if (!playService.isNetmusic()) {
            lrcView.setVisibility(views.get(1).GONE);
            Mp3Info mp3Info = mp3Infos.get(Position);
            textView_main_songName.setText(mp3Info.getTitle());
            Bitmap albumBitmap = MediaUtils.getArtwork(this, mp3Info.getId(), mp3Info.getAlbumId(), true, false);
            if (albumBitmap != null) {
                imageView_music_icon.setImageBitmap(albumBitmap);
            }
            textView3_end_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));
            maxPeogress = (int) mp3Info.getDuration();
            seekBar.setMax(maxPeogress);
            try {
                Mp3Info likeMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId", "=", mp3Info.getId()));
                if (likeMp3Info != null) {
                    imageView_favorite.setImageResource(R.mipmap.xin_hong);
                } else {
                    imageView_favorite.setImageResource(R.mipmap.xin_bai);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        } else {
            if (lrc_only_init) {
                lrc_only_init = false;
                lrcView.setVisibility(views.get(1).GONE);
                textview_loadLRC.setVisibility(views.get(1).VISIBLE);
                final NetMusic netMusic = playService.getNetmp3Infos().get(Position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initLrcView(netMusic);
                    }
                }).start();
            } else {
                song_change = true;
            }
            mp3Info = playService.getNetmp3Infos().get(Position);
            Thread thread = new Thread(this);
            thread.start();
            textView_main_songName.setText(mp3Info.getSongname());
            textView3_end_time.setText(MediaUtils.formatTime(playService.duration));
            maxPeogress = playService.duration;
            seekBar.setMax(maxPeogress);
        }
        switch (playService.getPlay_mode()) {
            case 1:
                imageView2_play_mode.setImageResource(R.mipmap.order);
                break;
            case 2:
                imageView2_play_mode.setImageResource(R.mipmap.random);
                break;
            case 3:
                imageView2_play_mode.setImageResource(R.mipmap.single);
                break;
            default:
                break;
        }

    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.rotate_fragment_in, R.anim.rotate_main_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imageView6_change_bg:
                bg_id = bg_id > 6 ? 0 : bg_id;
                switch (bg_id) {
                    case 0:
                        main_play_layout.setBackgroundResource(R.mipmap.main_bg1);
                        break;
                    case 1:
                        main_play_layout.setBackgroundResource(R.mipmap.main_bg2);
                        break;
                    case 2:
                        main_play_layout.setBackgroundResource(R.mipmap.main_bg3);
                        break;
                    case 3:
                        main_play_layout.setBackgroundResource(R.mipmap.main_bg4);
                        break;
                    case 4:
                        main_play_layout.setBackgroundResource(R.mipmap.main_bg5);
                        break;
                    case 5:
                        main_play_layout.setBackgroundResource(R.mipmap.main_bg6);
                        break;
                    case 6:
                        main_play_layout.setBackgroundResource(R.mipmap.main_bg7);
                        imageView_music_icon.setAlpha(alpha);
                        break;
                }
                bg_id++;
                break;
            case R.id.imageView_music_icon:
                if (alpha <= 0) {
                    alpha = 1;
                }
                imageView_music_icon.setAlpha(alpha);
                alpha = (float) (alpha - 0.2);
                break;
            case R.id.imageView3_play_pause:
                if (playService.isPlaying()) {
                    imageView3_play_pause.setImageResource(R.mipmap.main_play);
                    playService.pause();
                } else {
                    imageView3_play_pause.setImageResource(R.mipmap.main_pause);
                    playService.start();
                }
                break;
            case R.id.imageView4_next:
                System.out.println("*******执行了MusicPlayActivity的playService.next()方法*******");
                playService.next();
                break;
            case R.id.imageView5_upon:
                playService.upon();
                break;
            case R.id.imageView2_play_mode: {
                switch (playService.getPlay_mode()) {
                    case 1:
                        imageView2_play_mode.setImageResource(R.mipmap.random);
                        playService.setPlay_mode(2);
                        Toast.makeText(this, "已设置为: 随机播放", Toast.LENGTH_SHORT).show();
                        editor.putInt("Play_Mode", playService.getPlay_mode());
                        editor.commit();
                        break;
                    case 2:
                        imageView2_play_mode.setImageResource(R.mipmap.single);
                        playService.setPlay_mode(3);
                        Toast.makeText(this, "已设置为: 单曲循环", Toast.LENGTH_SHORT).show();
                        editor.putInt("Play_Mode", playService.getPlay_mode());
                        editor.commit();
                        break;
                    case 3:
                        imageView2_play_mode.setImageResource(R.mipmap.order);
                        Toast.makeText(this, "已设置为: 列表循环", Toast.LENGTH_SHORT).show();
                        playService.setPlay_mode(1);
                        editor.putInt("Play_Mode", playService.getPlay_mode());
                        editor.commit();
                        break;
                    default:
                        break;
                }
                break;
            }
            case R.id.imageView_favorite:
                if (!playService.isNetmusic()) {
                    Mp3Info mp3Info = null;
                    if (playService.getCurrentPosition() < 0) {
                        mp3Info = mp3Infos.get(playService.getLastPosition());
                    } else {
                        mp3Info = mp3Infos.get(playService.getCurrentPosition());
                    }
                    try {
                        Mp3Info likeMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId", "=", mp3Info.getId()));
                        System.out.println("*******************likeMp3Info=" + likeMp3Info);
                        if (likeMp3Info == null) {
                            mp3Info.setMp3InfoId(mp3Info.getId());
                            app.dbUtils.save(mp3Info);
                            imageView_favorite.setImageResource(R.mipmap.xin_hong);
                            Toast.makeText(this, "已将当前歌曲加入我喜欢列表", Toast.LENGTH_SHORT).show();
                        } else {
                            app.dbUtils.deleteById(Mp3Info.class, likeMp3Info.getId());
                            imageView_favorite.setImageResource(R.mipmap.xin_bai);
                            Toast.makeText(this, "已将当前歌曲从我喜欢列表中移除", Toast.LENGTH_SHORT).show();
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }else{
                    NetMusic netmp3Info = null;
                    if (playService.getCurrentNetPosition() < 0) {
                       Toast.makeText(this,"请先播放一首歌曲再收藏",Toast.LENGTH_LONG).show();
                    } else {
                        netmp3Info = playService.getNetmp3Infos().get(playService.getCurrentNetPosition());
                        imageView_favorite.setImageResource(R.mipmap.xin_hong);
                        //app.dbUtils.save(netmp3Info);
                    }

                }
                break;

            default:
                break;

        }
    }

    private void initUI() {
        views.add(getLayoutInflater().inflate(R.layout.main_album_image_layout, null));
        views.add(getLayoutInflater().inflate(R.layout.lrc_layout, null));
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new myAdapter());
        main_play_layout = (RelativeLayout) findViewById(R.id.main_play_layout);
        imageView6_change_bg = (ImageView) findViewById(R.id.imageView6_change_bg);
        imageView_music_icon = (ImageView) views.get(0).findViewById(R.id.imageView_music_icon);
        imageView_favorite = (ImageView) views.get(0).findViewById(R.id.imageView_favorite);
        imageView3_play_pause = (ImageView) findViewById(R.id.imageView3_play_pause);
        imageView4_next = (ImageView) findViewById(R.id.imageView4_next);
        imageView5_upon = (ImageView) findViewById(R.id.imageView5_upon);
        imageView2_play_mode = (ImageView) findViewById(R.id.imageView2_play_mode);
        imageview_background = (ImageView) findViewById(R.id.imageView1_background);
        textView_main_songName = (TextView) views.get(0).findViewById(R.id.textView_main_songName);
        textView2_start_time = (TextView) findViewById(R.id.textView2_start_time);
        textView3_end_time = (TextView) findViewById(R.id.textView3_end_time);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        lrcView = (LrcView) views.get(1).findViewById(R.id.LrcView);
        textview_loadLRC = (TextView) views.get(1).findViewById(R.id.textview_loadLRC);


    }

    private void initLrcView(NetMusic netMusic) {
//从网络下载加载歌词
        LRCUtil lrcUtil = new LRCUtil();
        lrcUtil.readyLRC(netMusic);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/lrc" + "/" + netMusic.songid + ".lrc";
        File file = new File(path);
        while (true) {
            if (file.exists()&&lrcUtil.Fileisprepare) {
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader input = new InputStreamReader(fileInputStream);
            lrcView.parseLrcFile(input, playService.getDuration());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        lrcView.setListener(new IfLrcView.OnSeekToListener() {
            public void onLrcSeeked(int newTime) {
                // TODO Auto-generated method stub
                playService.seekTo(newTime);
            }

            public void onScrollToX(int offsetX) {
                // TODO Auto-generated method stub

            }

        });
        lrc_only_init = true;
        lrcViewIsPrepared = true;
        myHandler.sendEmptyMessage(200);

    }

    class myAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = views.get(position);
            container.addView(v);
            return v;
        }
    }

    private void setUIOnClick() {
        imageView_music_icon.setOnClickListener(this);
        imageView6_change_bg.setOnClickListener(this);
        imageView3_play_pause.setOnClickListener(this);
        imageView4_next.setOnClickListener(this);
        imageView5_upon.setOnClickListener(this);
        imageView2_play_mode.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        imageView_favorite.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindPlayService();
    }
}

