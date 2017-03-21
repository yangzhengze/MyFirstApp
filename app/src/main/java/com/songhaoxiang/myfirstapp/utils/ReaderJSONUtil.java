package com.songhaoxiang.myfirstapp.utils;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.songhaoxiang.myfirstapp.NetMusicListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Administrator on 2016/7/3.
 */
public class ReaderJSONUtil implements Runnable {

	NetMusicListFragment netMusicListFragment;
	ArrayList<NetMusic> WangYilist = new ArrayList<>();
	ArrayList<NetMusic> QQlist = new ArrayList<>();
	ArrayList<NetMusic> TingGe123list = new ArrayList<>();

	public ReaderJSONUtil(NetMusicListFragment netMusicListFragment) {
		this.netMusicListFragment = netMusicListFragment;

	}

	public ReaderJSONUtil() {

	}


	public ArrayList<NetMusic> QQReaderJSONUtil(String json, NetMusicListFragment netMusicListFragment) {
		this.netMusicListFragment = netMusicListFragment;
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(json);
			System.out.println(json);
			JSONArray contentlist = jsonObj.getJSONObject("showapi_res_body").getJSONObject("pagebean").getJSONArray("contentlist");
			int i = 0;
			while (i < contentlist.length()) {
				NetMusic song = new NetMusic();
				JSONObject array1_2 = contentlist.getJSONObject(i);
				try {
					song.albumid = array1_2.getString("albumid");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					song.albumname = array1_2.getString("albumname");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					song.albumpic_big = array1_2.getString("albumpic_big");
				} catch (JSONException e) {
					e.printStackTrace();
					song.albumpic_big = "http://www.cizw.org.cn/imgall/nfwwolt2mnxw63bomnxa/community/011f50559d0f2c32f875370a39c451.jpg";
				}
				try {
					song.albumpic_small = array1_2.getString("albumpic_small");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					song.downUrl = array1_2.getString("downUrl");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					song.m4a = array1_2.getString("m4a");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					song.singerid = array1_2.getString("singerid");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					song.singername = array1_2.getString("singername");
				} catch (JSONException e) {
					song.singername = netMusicListFragment.songName;
					e.printStackTrace();
				}
				try {
					song.songid = array1_2.getString("songid");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					song.songname = array1_2.getString("songname");
				} catch (JSONException e) {
					song.songname = netMusicListFragment.songName;
					e.printStackTrace();

				}
				i++;
				song.pingtai = "0";
				QQlist.add(song);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println("QQlist.size() = " + QQlist.size());
		return QQlist;
	}

	String WYsongname;
	String TGsongname;


	public void WangyiGetJSON(String songname, NetMusicListFragment netMusicListFragment) {
		this.netMusicListFragment = netMusicListFragment;
		this.WYsongname = songname.replaceAll(" ", "%20");
		if (WangYilist != null) {
			WangYilist.clear();
		}
		Thread thread = new Thread(this);
		thread.start();

	}

	public void Tingge123(String songname) {
		this.TGsongname = songname.replaceAll(" ", "%20");
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = "http://www.tingge123.com/qqmusic/?search=" + TGsongname + "&submit.x=0&submit.y=0";
				System.out.println("url==============" + url);
				HttpGet httpRequest = new HttpGet(url);
				try {
		  /*发送请求并等待响应*/
					HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
		  /*若状态码为200 ok*/
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
			/*读*/
						String strResult = EntityUtils.toString(httpResponse.getEntity());
						//这里进行网页内容过滤
						System.out.println("开启线程获取list");
						TinggeUtil tinggeUtil = new TinggeUtil();
						TingGe123list = tinggeUtil.ReadHtml(strResult);
						System.out.println("已经获取到list");
						Message msg = new Message();
						msg.what = 2;
						handler.sendMessage(msg);
						System.out.println("发送Message");
					} else {
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void run() {
		String url = "http://s.music.163.com/search/get?src=lofter&type=1&filterDj=true&s=" + WYsongname + "&limit=20&offset=0&callback=";
		HttpGet httpRequest = new HttpGet(url);
		try {
		  /*发送请求并等待响应*/
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
		  /*若状态码为200 ok*/
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
			/*读*/
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				WangyiReaderJSON(strResult);
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			} else {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<NetMusic> WangyiReaderJSON(String json) {
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(json);
			JSONArray contentlist = jsonObj.getJSONObject("result").getJSONArray("songs");
			int i = 0;
			while (i < contentlist.length()) {
				NetMusic song = new NetMusic();
				JSONObject array1_2 = contentlist.getJSONObject(i);
				try {
					song.songid = array1_2.getString("id");
					System.out.println("song.songid==" + song.songid);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					song.songname = array1_2.getString("name");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					JSONArray array_artists = array1_2.getJSONArray("artists");
					for (int j = 0; j < array_artists.length(); j++) {
						if (song.singername != null) {
							song.singername += "♪" + array_artists.getJSONObject(j).getString("name");
						} else {
							song.singername = array_artists.getJSONObject(j).getString("name");
						}
						if (song.singerid != null) {
							song.singerid += "♪" + array_artists.getJSONObject(j).getString("id");
						} else {
							song.singerid = array_artists.getJSONObject(j).getString("id");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					song.albumid = array1_2.getJSONObject("album").getString("id");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					song.albumname = array1_2.getJSONObject("album").getString("name");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					song.albumpic_big = array1_2.getJSONObject("album").getString("picUrl");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					song.m4a = array1_2.getString("audio");
					song.downUrl = array1_2.getString("audio");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				i++;
				song.pingtai = "1";
				this.WangYilist.add(song);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return WangYilist;
	}



	private Handler handler = new UIHandler();

	private final class UIHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 2: //
					System.out.println("收到handler！");
					TingGe123list.addAll(WangYilist);
					netMusicListFragment.loadData(TingGe123list, -1);
					break;
				case 1: //
					netMusicListFragment.loadData(WangYilist, -1);
					break;
			}
		}
	}




}