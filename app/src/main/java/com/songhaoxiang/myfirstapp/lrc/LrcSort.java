package com.songhaoxiang.myfirstapp.lrc;

import java.util.Comparator;
import java.util.Map;

public class LrcSort implements Comparator<Map<String,Object>> {

	@Override
	public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
		// TODO Auto-generated method stub
		String str1=(String) lhs.get("time");
		String str2=(String) rhs.get("time");
		return str1.compareTo(str2);
	}
	
}
