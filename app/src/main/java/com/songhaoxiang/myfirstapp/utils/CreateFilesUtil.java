package com.songhaoxiang.myfirstapp.utils;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2016/7/12.
 */
public class CreateFilesUtil {
	String filename = null;

	public CreateFilesUtil() {

	}

	//创建文件夹及文件
	public int CreateText(String songID) {
		String savDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+"/lrc";
		File dir = new File(savDir);
		if (!dir.exists()) {
			try {
				//按照指定的路径创建文件夹
				dir.mkdirs();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		filename = savDir+"/"+songID+".lrc";
		File file = new File(filename);
		if (!file.exists()) {
			try {
				//在指定的文件夹中创建文件
				boolean result = file.createNewFile();
				if (result){
					return 0;//表示没有文件，但成功创建文件
				}else {
					return 1;//表示没有文件，但创建文件失败
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 2;//表示遇到异常
		}else {
			return -1;//表示有文件
		}

	}

	//向已创建的文件中写入数据
	public void print(String str) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(filename,false);//
			// 创建FileWriter对象，用来写入字符流
			bw = new BufferedWriter(fw); // 将缓冲对文件的输出
			bw.write(str); // 写入文件
			bw.newLine();
			bw.flush(); // 刷新该流的缓冲
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				bw.close();
				fw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
			}
		}
	}
}
