package com.songhaoxiang.myfirstapp;

import android.app.Application;
import android.content.SharedPreferences;

import com.lidroid.xutils.DbUtils;
import com.songhaoxiang.myfirstapp.utils.Constant;

/**
 * Created by 宋浩祥 on 2016/2/4.
 */
public class baoBaoMusicApplication extends Application{

    public static SharedPreferences sharedPreferences;
    public static DbUtils dbUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(Constant.SP_NAME,MODE_PRIVATE);
        dbUtils = DbUtils.create(getApplicationContext(),Constant.DB_NAME);

    }
}
