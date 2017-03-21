package com.songhaoxiang.myfirstapp.utils;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Administrator on 2016/7/8.
 */
public class BaiduPicThread {
	NetMusic netSong;

	public BaiduPicThread(NetMusic netMusic) {
		this.netSong = netMusic;
	}

	public void run() {
		String picUrl = "http://image.baidu.com/search/index?tn=baiduimage&ipn=r&ct=201326592&cl=2&lm=-1&st=-1&fm=result&fr=&sf=1&fmq=1467943318713_R&pv=&ic=0&nc=1&z=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&ie=utf-8&word=" + netSong.songname.replaceAll(" ", "+");
		HttpGet httpRequest = new HttpGet(picUrl);
		try {
			HttpResponse httpResponse = null;
			httpResponse = new DefaultHttpClient().execute(httpRequest);
                /*若状态码为200 ok*/
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
			/*读*/
				String picpage = EntityUtils.toString(httpResponse.getEntity());
				String pichtm1[] = picpage.split("\"pageNum\":0,");
				String pichtm2[] = pichtm1[1].split("\"pageNum\":1,");
				String pichtm3[] = pichtm2[1].split("\"pageNum\":2,");
				String pic1URL = pichtm2[0].substring(pichtm2[0].indexOf("\"objURL\":\"") + 10, pichtm2[0].indexOf("\"",pichtm2[0].indexOf("\"objURL\":\"") + 10));
				String pic2URL = pichtm3[0].substring(pichtm3[0].indexOf("\"objURL\":\"") + 10, pichtm3[0].indexOf("\"",pichtm3[0].indexOf("\"objURL\":\"") + 10));
				String pic3URL = pichtm3[1].substring(pichtm3[1].indexOf("\"objURL\":\"") + 10, pichtm3[1].indexOf("\"",pichtm3[1].indexOf("\"objURL\":\"") + 10));
				System.out.println("99999999999"+pic1URL);
				System.out.println(pic2URL);
				System.out.println(pic3URL);
				httpRequest = new HttpGet(pic1URL);
				httpResponse = new DefaultHttpClient().execute(httpRequest);
				/*若状态码为200 ok*/
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					netSong.albumpic_big = pic1URL;
				} else {
					httpRequest = new HttpGet(pic2URL);
					httpResponse = new DefaultHttpClient().execute(httpRequest);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						netSong.albumpic_big = pic2URL;
					} else {
						netSong.albumpic_big = pic3URL;
					}
				}
			}
		} catch (IOException e) {
			System.out.println("获取百度图片出现异常");
			e.printStackTrace();
		}
	}
}
