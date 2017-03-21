package com.songhaoxiang.myfirstapp.lrc;

/**
 * 用于处理歌词拖动事件
 * @author Lance
 *
 */
public interface IfLrcView {
	void seekLrcToTime(int time);
	void setListener(OnSeekToListener l);
	public static interface OnSeekToListener{
		void onLrcSeeked(int newTime);
		void onScrollToX(int offsetX);
	}
	
	
}
