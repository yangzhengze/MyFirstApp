package com.songhaoxiang.myfirstapp.utils;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by user on 2016/7/7.
 */
public class TinggeUtil {
	private ArrayList<NetMusic> netMusicList;
	public int flag = 1;

	public TinggeUtil() {
		netMusicList = new ArrayList<>();

	}

	public ArrayList<NetMusic> ReadHtml(String strResult) {
		String str1 = "<li class=\"ser_8\"><a href=\"http://";
		String str[] = strResult.split(str1);
		int count = 0;
		while (++count < str.length) {
			NetMusic netSong = new NetMusic();
			TinggeThread tinggeThread = new TinggeThread(str[count], netSong, netMusicList, this);
			tinggeThread.start();
			//新建线程加载图片
			netSong.albumname = "来自听歌123网站";
		}
		while (true) {
			if (flag  == count) {
				System.out.println("我要返回了！！");
				return netMusicList;
			}
			System.out.println("我貌似要死循环了"+flag+""+count);
		}
	}
}
