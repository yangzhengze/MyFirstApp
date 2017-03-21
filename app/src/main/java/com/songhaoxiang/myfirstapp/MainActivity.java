/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.songhaoxiang.myfirstapp;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.astuetz.PagerSlidingTabStrip;
//import com.astuetz.viewpager.extensions.sample.SuperAwesomeCardFragment;
////import com.songhaoxiang.myfirstapp.R;
//import com.songhaoxiang.myfirstapp.adapter.MyMusicListAdapter;

public class MainActivity extends BaseActivity {

    private final Handler handler = new Handler();

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;

    private Drawable oldBackground = null;
    private int currentColor = 0xefd2576d;

    private MyMusicListFragment myMusicListFragment;
    public NetMusicListFragment netMusicListFragment;
    private LikeMusicFragment likeMusicFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        myMusicListFragment = MyMusicListFragment.newInstance();
        netMusicListFragment = NetMusicListFragment.newInstance();
        likeMusicFragment = LikeMusicFragment.newInstance();
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        changeColor(currentColor);

    }


    @Override
    public void publish(int Progress,int percent) {
        //更新进度条
    }

    @Override
    public void change(int Position) {
        //切换播放位置
        if (myMusicListFragment != null) {
            System.out.println("******************pager.getCurrentItem()="+pager.getCurrentItem());
            myMusicListFragment.changeUIStatusOnPlay(Position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        baoBaoMusicApplication baoBaoMusicApplication = (com.songhaoxiang.myfirstapp.baoBaoMusicApplication) getApplication();


    }



    private void changeColor(int newColor) {

        tabs.setIndicatorColor(newColor);

        // change ActionBar color just if an ActionBar is available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Drawable colorDrawable = new ColorDrawable(newColor);
            Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
            LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});

            if (oldBackground == null) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ld.setCallback(drawableCallback);
                } else {
                    getActionBar().setBackgroundDrawable(ld);
                }

            } else {

                TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, ld});

                // workaround for broken ActionBarContainer drawable handling on
                // pre-API 17 builds
                // https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    td.setCallback(drawableCallback);
                } else {
                    getActionBar().setBackgroundDrawable(td);
                }

                td.startTransition(200);

            }

            oldBackground = ld;

            // http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayShowTitleEnabled(true);

        }

        currentColor = newColor;

    }

    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.rotate_fragment_in, R.anim.rotate_main_out);
    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            handler.removeCallbacks(what);
        }
    };

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"我的音乐", "网络音乐","我喜欢","最近播放"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (myMusicListFragment == null) {
                    return myMusicListFragment.newInstance();
                }
                return myMusicListFragment;

            } else if (position == 1) {
                if (netMusicListFragment == null) {
                    return netMusicListFragment.newInstance();
                }
                return netMusicListFragment;
            }else if (position == 2){
                if (likeMusicFragment == null) {
                    return likeMusicFragment.newInstance();
                }
                return likeMusicFragment;
            }else if (position == 4){
                if (myMusicListFragment == null) {
                    return myMusicListFragment.newInstance();
                }
                return myMusicListFragment;
            }
            return myMusicListFragment.newInstance();
        }

    }



}