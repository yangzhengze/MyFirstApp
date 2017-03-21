package com.songhaoxiang.myfirstapp.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Administrator on 2016/7/18.
 */
public class LRCUtil {

	public LRCUtil() {
	}

	CreateFilesUtil createFilesUtil;
	public boolean Fileisprepare = false;

	public void readyLRC(NetMusic netMusic) {
		Fileisprepare = false;
		//System.out.println("调用到了创建文件的地方");
		createFilesUtil = new CreateFilesUtil();
		//System.out.println("songid==" + netMusic.songid);
		int result = createFilesUtil.CreateText(netMusic.songid);
		//System.out.println("result==" + result);
		switch (result) {
			case -1:
				Fileisprepare = true;
				//表示文件存在
				break;
			case 0:
				//表示成功创建文件，下面开始写入
				final String songid = netMusic.songid;
				final String songpingtai = netMusic.pingtai;
				new Thread(new Runnable() {
					@Override
					public void run() {
						String LRC = "";
						if (songpingtai.equals("1")) {
							LRC = downloadWYLRC(songid);
						} else {
							String appid = "16599";//要替换成自己的
							String secret = "8501f52ec00a41ecbdd2e9e58ad5b57d";//要替换成自己的
							LRC = downloadQQLRC(appid, secret, songid);
						}
						System.out.println("LRC===" + LRC);
						createFilesUtil.print(LRC);
						Fileisprepare = true;
					}
				}).start();
				break;
			case 1:
				break;
			case 2:
				break;
		}

	}

	private String downloadQQLRC(String appid, String secret, String songID) {
		String LRC = "";
		String QQurl = "http://route.showapi.com/213-2?showapi_appid=" + appid + "&musicid=" + songID + "&showapi_sign=" + secret;
		HttpGet httpRequest = new HttpGet(QQurl);
		  /*发送请求并等待响应*/
		HttpResponse httpResponse = null;
		try {
			httpResponse = new DefaultHttpClient().execute(httpRequest);
          /*若状态码为200 ok*/
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
            /*读*/
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				LRC = QQReaderLRCJSON(strResult);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (LRC == null || LRC.length() == 0) {
			LRC = "未搜索到相关歌曲歌词";
		}
		return LRC;
	}
	private String QQReaderLRCJSON(String json) {
		JSONObject jsonObj = null;
		String LRC = null;
		try {
			jsonObj = new JSONObject(json);
			JSONObject jsonObj1 = jsonObj.getJSONObject("showapi_res_body");
			LRC = jsonObj1.getString("lyric");
			LRC = HtmlChange.htmlchang(LRC);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return LRC;
	}


	private String downloadWYLRC(String songID) {
		String LRC = "";
		String WYurl = "http://music.163.com/api/song/lyric?os=pc&id=" + songID + "&lv=-1&kv=-1&tv=-1";
		HttpGet httpRequest = new HttpGet(WYurl);
		  /*发送请求并等待响应*/
		HttpResponse httpResponse = null;
		try {
			httpResponse = new DefaultHttpClient().execute(httpRequest);
          /*若状态码为200 ok*/
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
            /*读*/
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				LRC = WangyiReaderLRCJSON(strResult);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (LRC == null || LRC.length() == 0) {
			LRC = "未搜索到相关歌曲歌词";
		}
		return LRC;
	}

	private String WangyiReaderLRCJSON(String json) {
		JSONObject jsonObj = null;
		String LRC = null;
		try {
			jsonObj = new JSONObject(json);
			JSONObject jsonObj1 = jsonObj.getJSONObject("lrc");
			LRC = jsonObj1.getString("lyric");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return LRC;
	}

	private static class HtmlChange {
		private static String htmlchang(String LRC) {
			//System.out.println("处理前的歌词是"+LRC);
			LRC = LRC.replaceAll("&#58;", ":").replaceAll("&#46;", ".").replaceAll("&#43;", "+").replaceAll("&#10;", "\n");
			LRC = LRC.replaceAll("&#40;", "(").replaceAll("&#41;", ")").replaceAll("&#45;", "-").replaceAll("&#32;", " ");
			LRC = LRC.replaceAll("&#39;", "'").replaceAll("&#13;", "").replaceAll("&#38;", "&");
			//System.out.println("处理后的歌词是"+LRC);
			return LRC;

		}

	}

}
