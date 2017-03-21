package com.songhaoxiang.myfirstapp.utils;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Administrator on 2016/7/8.
 */
public class TinggeThread extends Thread {
	private String str;
	private NetMusic netSong;
	private ArrayList<NetMusic> netMusicList;
	public int flag;
	private TinggeUtil tinggeUtil;

	public TinggeThread(String str, NetMusic netSong, ArrayList<NetMusic> netMusicList, TinggeUtil tinggeUtil) {
		this.str = str;
		this.netSong = netSong;
		this.netMusicList = netMusicList;
		this.tinggeUtil = tinggeUtil;
	}

	public void run() {
		synchronized (this) {
			tinggeUtil.flag++;
		}
		String playerUrl = "http://" + str.substring(0, str.indexOf("\"")).replace("p", "d");
		System.out.println("playerURL ===" + playerUrl);
		HttpGet httpRequest = new HttpGet(playerUrl);
		  /*发送请求并等待响应*/
		HttpResponse httpResponse = null;
		try {
			httpResponse = new DefaultHttpClient().execute(httpRequest);
				/*若状态码为200 ok*/
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
			/*读*/
				String downpage = EntityUtils.toString(httpResponse.getEntity());
				netSong.pingtai = "TG123";
				System.out.println("downpage==" + downpage);
				System.out.println("downpage.indexOf(\" <td height=\\\"51\\\"><a href=\\\"\") + 26==" + downpage.indexOf(" <td height=\"51\"><a href=\"") + 26);
				System.out.println("downpage.indexOf(\".mp3\\\" target\")==" + downpage.indexOf(".mp3\" target"));
				if (!downpage.contains(" <td height=\"51\"><a href=\"")) {
					return;
				}

				String downURL = downpage.substring(downpage.indexOf(" <td height=\"51\"><a href=\"") + 26, downpage.indexOf(".mp3\" target")) + ".mp3";
				httpRequest = new HttpGet(downURL);
				httpResponse = new DefaultHttpClient().execute(httpRequest);
				if (httpResponse.getStatusLine().getStatusCode() != 200) {
					return;
				} else {
					netSong.m4a = downURL;
					netSong.downUrl = netSong.m4a;
					System.out.println("downUrl===" + netSong.downUrl);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		netSong.songname = str.substring(str.indexOf("target=\"_blank\" title=\"") + 23, str.indexOf(".mp3\">"));
		netMusicList.add(netSong);

	}
}
