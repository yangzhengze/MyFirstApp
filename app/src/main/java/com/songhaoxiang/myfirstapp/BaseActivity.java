package com.songhaoxiang.myfirstapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;

import com.songhaoxiang.myfirstapp.utils.Constant;
import com.songhaoxiang.myfirstapp.vo.Mp3Info;

import java.util.ArrayList;

/**
 * Created by 宋浩祥 on 2016/1/27.
 */
public abstract class BaseActivity extends FragmentActivity {

    public PlayService playService;
    private boolean isBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SysApplication.getInstance().addActivity(this);
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayService.playBinder playBinder = (PlayService.playBinder) service;
            playService = playBinder.getPlayService();
            playService.setMusicUpdateListener(musicUpdateListener);
            musicUpdateListener.onPublish(playService.getCurrentProgress(), 0);
            if (playService != null) {
                if (!playService.isNetmusic()) {
                    musicUpdateListener.onChange(playService.getCurrentPosition());
                } else {
                    musicUpdateListener.onChange(playService.getCurrentNetPosition());
                }
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playService = null;
            isBound = false;
        }
    };

    private PlayService.MusicUpdateListener musicUpdateListener = new PlayService.MusicUpdateListener() {
        @Override
        public void onPublish(int Progress, int percent) {
            publish(Progress, percent);
        }

        @Override
        public void onChange(int Position) {
            change(Position);
        }
    };

    public abstract void publish(int Progress, int percent);

    public abstract void change(int Position);

    //绑定服务
    public void bindPlayService() {
        if (!isBound) {
            Intent intent = new Intent(this, PlayService.class);
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
            isBound = true;
        }
    }


    //解绑服务
    public void unbindPlayService() {
        if (isBound) {
            unbindService(conn);
            isBound = false;
        }
    }

}
