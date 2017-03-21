package com.songhaoxiang.myfirstapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 歌词时间类型字符串转换为毫秒整形
 * @author Lance
 *
 */
public class GetTime {
	/**
	 * 
	 * @param txt  歌词时间类型字符串
	 * @return time 时间毫秒整形
	 */
	public int textTransTime(String txt){
		String temp[]=new String[3];
		int time=0;
		Matcher matcher= Pattern.compile("\\d{2}\\:\\d{2}\\.\\d{2,}").matcher(txt);
		if(matcher.matches()){
			temp[0]=txt.substring(0, 2);
			temp[1]=txt.substring(3, 5);
			temp[2]=txt.substring(6, 8);
			time= Integer.valueOf(temp[0])*60*1000+ Integer.valueOf(temp[1])*1000+ Integer.valueOf(temp[2])*10;
		}
		//System.out.println("texttranstime"+time);
		return time;
	}
	
	
	public String timeTransTxt(int time){
		String txt="";
		String temp[]=new String[3];
		int[] trans=new int[3];
		trans[0]=time/(1000*60);
		trans[1]=time%(1000*60)/1000;
		trans[2]=time%(1000*60)%1000/10;
		
		for(int i=0;i<=2;i++){
			if(trans[i]<10){
				temp[i]="0"+ String.valueOf(trans[i]);
			}else{
				temp[i]= String.valueOf(trans[i]);
			}
		}
		txt=temp[0]+":"+temp[1]+"."+temp[2];
		return txt;
	}
	
	public int interTime(String st1, String st2, int model){
		int time=0;
		if(model>=0){
			time=textTransTime(st1)-textTransTime(st2);
		}else{
			time=textTransTime(st2)-textTransTime(st1);
		}
		return time;
	}
}
