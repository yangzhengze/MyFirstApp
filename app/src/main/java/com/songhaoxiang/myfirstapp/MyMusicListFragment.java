package com.songhaoxiang.myfirstapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.songhaoxiang.myfirstapp.adapter.MyMusicListAdapter;
import com.songhaoxiang.myfirstapp.utils.BaiduPicThread;
import com.songhaoxiang.myfirstapp.utils.BitmapUtil;
import com.songhaoxiang.myfirstapp.utils.ChooseUtil;
import com.songhaoxiang.myfirstapp.utils.MediaUtils;
import com.songhaoxiang.myfirstapp.utils.NetMusic;
import com.songhaoxiang.myfirstapp.utils.ReaderJSONUtil;
import com.songhaoxiang.myfirstapp.vo.Mp3Info;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.client.cache.Resource;

/**
 * Created by 宋浩祥 on 2016/1/25.
 */
public class MyMusicListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, Runnable {
	private ListView listView_my_music;
	private ArrayList<Mp3Info> mp3Infos;
	private ImageView imageView_albumIcon;
	private TextView textView_songName, textView_singer;
	private ImageView imageView_play_pause, imageView_next;
	private LinearLayout linearLayoutSong;
	private Bitmap albumBitmap = null;
	private NetMusic netmp3Info;
	private boolean first = true;
	private int lastPosition = -1;

	private MainActivity mainActivity;
	private MyMusicListAdapter myMusicListAdapter;
	private NetMusicListFragment netMusicListFragment;
	private int position = -1;


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mainActivity = (MainActivity) context;

	}

	public static MyMusicListFragment newInstance() {
		return new MyMusicListFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.my_music_list_layout, null);
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
		netMusicListFragment = mainActivity.netMusicListFragment;
		loadData();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		mainActivity.bindPlayService();
	}


	@Override
	public void onPause() {
		super.onPause();
		mainActivity.unbindPlayService();
	}


	//加载本地音乐列表
	private void loadData() {
		mp3Infos = MediaUtils.getMp3Info(mainActivity);
		myMusicListAdapter = new MyMusicListAdapter(mainActivity, mp3Infos, position);
		//填充数据到列表
		listView_my_music.setAdapter(myMusicListAdapter);

	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mainActivity.playService.play(position);
	}

	public void changeUIStatusOnPlay(int position) {
		System.out.println("确实是调用了这句话！！！！");
		if (mainActivity.playService.isPlaying) {
			imageView_play_pause.setImageResource(R.mipmap.pause);
		} else {
			imageView_play_pause.setImageResource(R.mipmap.play);
		}
		if (!mainActivity.playService.isNetmusic()) {
			if (position >= 0 && position < mp3Infos.size()) {
				this.position = position;
				myMusicListAdapter.setPosition(position);
				myMusicListAdapter.notifyDataSetChanged();
				if (netMusicListFragment.list != null &&!netMusicListFragment.list.isEmpty()) {
					netMusicListFragment.netMusicListAdapter.setPosition(-1);
					netMusicListFragment.netMusicListAdapter.notifyDataSetChanged();
				}
				Mp3Info mp3Info = mp3Infos.get(position);
				textView_singer.setText(mp3Info.getArtist());
				textView_songName.setText(mp3Info.getTitle());
				Bitmap albumBitmap = MediaUtils.getArtwork(mainActivity, mp3Info.getId(), mp3Info.getAlbumId(), true, true);
				if (albumBitmap != null) {
					imageView_albumIcon.setImageBitmap(albumBitmap);
				}
			}
		} else {
			myMusicListAdapter.setPosition(-1);
			myMusicListAdapter.notifyDataSetChanged();
			if(netMusicListFragment!=null &&netMusicListFragment.netMusicListAdapter!=null){
				netMusicListFragment.netMusicListAdapter.setPosition(position);
				netMusicListFragment.netMusicListAdapter.notifyDataSetChanged();
			}
			ArrayList<NetMusic> mp3Infos = mainActivity.playService.getNetmp3Infos();
			if (position >= 0 && position < mp3Infos.size()) {
				netmp3Info = mp3Infos.get(position);
				textView_songName.setText(netmp3Info.getSongname());
				ChooseUtil chooseUtil = new ChooseUtil();
				chooseUtil.chooseUtil(textView_singer, netmp3Info.getSingername(), netmp3Info.getAlbumname(), netmp3Info.getSongname());
				Thread thread = new Thread(this);
				thread.start();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.imageView_play_pause:
				if (mainActivity.playService.isPlaying()) {
					imageView_play_pause.setImageResource(R.mipmap.play);
					mainActivity.playService.pause();
				} else {
					if (mainActivity.playService.isNetmusic()) {
						mainActivity.playService.netplay(mainActivity.playService.getCurrentNetPosition(), mainActivity.playService.getNetmp3Infos());
					} else {
						mainActivity.playService.play(mainActivity.playService.getCurrentPosition());
					}
					imageView_play_pause.setImageResource(R.mipmap.pause);
				}
				break;
			case R.id.imageView_next:
				mainActivity.playService.next();
				break;
			default:
				Intent intent = new Intent(mainActivity, MusicPlayActivity.class);
				startActivity(intent);
				mainActivity.overridePendingTransition(R.anim.rotate_main_in, R.anim.rotate_fragment_out);
				break;
		}
	}


	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 200:
					if (albumBitmap != null) {
						imageView_albumIcon.setImageBitmap(albumBitmap);
					}
			}
		}
	};

	@Override
	public void run() {
		if (netmp3Info.getAlbumpic_big() == null) {
			BaiduPicThread baiduPicThread = new BaiduPicThread(netmp3Info);
			baiduPicThread.run();//这里并不是一个线程  只是模拟的
		}
		BitmapUtil bitmapUtil = new BitmapUtil(mainActivity);
		albumBitmap = bitmapUtil.getNetImage(netmp3Info.getAlbumpic_big());
		//albumBitmap = BitmapFactory.decodeStream(new URL(netmp3Info.getAlbumpic_big()).openStream());
		handler.sendEmptyMessage(200);
	}
}
