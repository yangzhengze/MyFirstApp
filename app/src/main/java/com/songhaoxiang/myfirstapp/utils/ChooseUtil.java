package com.songhaoxiang.myfirstapp.utils;

import android.widget.TextView;

/**
 * Created by Administrator on 2016/7/3.
 */
public class ChooseUtil {
	public void chooseUtil(){

	}
	public void chooseUtil(TextView textView_singer,String singer,String albumname,String songname){
		if (singer == null && albumname == null)
			textView_singer.setText(songname);
		else if (singer == null) {
			textView_singer.setText(albumname);
		} else if (albumname == null) {
			textView_singer.setText(singer);
		} else {
			textView_singer.setText(singer + "♪●♪" + albumname);
		}
	}
}
