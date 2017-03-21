package com.songhaoxiang.myfirstapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.songhaoxiang.myfirstapp.adapter.MyMusicListAdapter;
import com.songhaoxiang.myfirstapp.utils.MediaUtils;
import com.songhaoxiang.myfirstapp.vo.Mp3Info;

import java.util.ArrayList;


public class LikeMusicFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView listView_my_music;
    private ArrayList<Mp3Info> likeMp3Infos;
    private ImageView imageView_albumIcon;
    private TextView textView_songName, textView_singer;
    private ImageView imageView_play_pause, imageView_next;
    private LinearLayout linearLayoutSong;
    private baoBaoMusicApplication app;
    private MainActivity mainActivity;
    private MyMusicListAdapter myMusicListAdapter;
    private int position = -1;


    public static LikeMusicFragment newInstance() {
        LikeMusicFragment likeMusicFragment = new LikeMusicFragment();
        return likeMusicFragment;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;

    }

    public LikeMusicFragment() {
        // Required empty public constructor
    }

    public void onResume() {
        super.onResume();
        mainActivity.bindPlayService();


    }

    @Override
    public void onPause() {
        super.onPause();
        mainActivity.unbindPlayService();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_like_music, null);
        listView_my_music = (ListView) view.findViewById(R.id.listView_my_music);
        imageView_albumIcon = (ImageView) view.findViewById(R.id.imageView_albumIcon);
        textView_songName = (TextView) view.findViewById(R.id.textView_songName);
        textView_singer = (TextView) view.findViewById(R.id.textView_singer);
        imageView_play_pause = (ImageView) view.findViewById(R.id.imageView_play_pause);
        imageView_next = (ImageView) view.findViewById(R.id.imageView_next);
        linearLayoutSong = (LinearLayout) view.findViewById(R.id.linearLayoutSong);
        listView_my_music.setOnItemClickListener(this);
        imageView_play_pause.setOnClickListener(this);
        imageView_next.setOnClickListener(this);
        imageView_albumIcon.setOnClickListener(this);
        linearLayoutSong.setOnClickListener(this);
        app = (com.songhaoxiang.myfirstapp.baoBaoMusicApplication) mainActivity.getApplication();

        loadData();

        return view;
    }


    //加载本地音乐列表
    private void loadData() {
        try {
            likeMp3Infos = (ArrayList<Mp3Info>) app.dbUtils.findAll(Mp3Info.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (likeMp3Infos != null) {
            myMusicListAdapter = new MyMusicListAdapter(mainActivity, likeMp3Infos,position);
        }
        //填充数据到列表
        listView_my_music.setAdapter(myMusicListAdapter);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClick(View v) {

    }
}
