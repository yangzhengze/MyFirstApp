package com.songhaoxiang.myfirstapp.lrc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.songhaoxiang.myfirstapp.utils.GetTime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcView extends View implements IfLrcView {
	//mPaint为当前时间点显示歌词的画笔，nPaint为正常画笔
	private TextPaint mPaint, nPaint;
	private Paint paint;
	//sHeight,sWidth为view的高度及宽度
	private int sHeight, sWidth;
	//当前歌词在list中的位置
	private int position = -1;
	//歌词间隔行距
	private int interval;
	//字体高度
	private int fontHeight;
	//装载歌词、时间的map集合
	private List<Map<String, Object>> lrcList = new ArrayList<Map<String, Object>>();
	//posY,lastY,posX,lastX 滑动前后的X,Y值
	private float posX, posY, lastX, lastY;
	//歌词是否在拖动
	private boolean isDrag = false;
	//offsetY Y方向上的拖动量，addY Y方向上移动的累加量
	private int offsetY, offsetX;
	//一行歌词移动的频数
	private int step = 0, totalSteps = 0;
	//拖动后的时间,传递进来的播放时间
	private int setTime, time;
	private GetTime getTime;
	private boolean isFirst = true;
	private OnSeekToListener onSeekToListener;

	public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public LrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public LrcView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		initPaint();
		if (isDrag) {
			String text = getTime.timeTransTxt(setTime);
			canvas.drawLine(0, sHeight / 2, sWidth, sHeight / 2, paint);
			canvas.drawText(text, sWidth / 3, sHeight / 2, paint);
		}

		//offsetY=(interval+fontHeight);
		if (position < -1) {
			position = -1;
		}
		if (position >= lrcList.size()) {
			position = lrcList.size() - 1;
		}
		if (position == -1) {
			for (int i = 0; i < lrcList.size(); i++) {
				String tempString = (String) lrcList.get(i).get("rows");
				int x = 0;
				//int x=(int) (sWidth/2-nPaint.measureText(tempString)/2);
				int y = (int) (sHeight / 2 + (interval - fontHeight) * i);
				StaticLayout layout = new StaticLayout((String) lrcList.get(i).get("rows"), (TextPaint) nPaint, sWidth, Alignment.ALIGN_CENTER, 1.0f, 0.0f, true);
				canvas.save();
				canvas.translate(x, y);
				layout.draw(canvas);
				canvas.restore();
			}
		} else {
			if (totalSteps != 0) {
				float offset = (interval - fontHeight);
				offset = offset / totalSteps * step;
				//Log.i("ondraw", String.valueOf(step)+"  total"+String.valueOf(totalSteps));
				for (int i = position - 1, j = 1; i >= 0; i--, j++) {
					//String tempString=(String) lrcList.get(i).get("rows");
					float x = 0f;
					//float x=(sWidth/2-nPaint.measureText(tempString)/2);
					float y = (sHeight / 2 - (interval - fontHeight) * j - offset);
					//canvas.drawText((String) lrcList.get(i).get("rows"), x, y, nPaint);
					StaticLayout layout = new StaticLayout((String) lrcList.get(i).get("rows"), (TextPaint) nPaint, sWidth, Alignment.ALIGN_CENTER, 1.0f, 0.0f, true);
					canvas.save();
					canvas.translate(x, y);
					layout.draw(canvas);
					canvas.restore();
				}
				//Log.i("position", String.valueOf(position));
				//String tempString=(String) lrcList.get(position).get("rows");
				float x = 0f;
				//float x= (sWidth/2-mPaint.measureText(tempString)/2);
				float y = (sHeight / 2 - offset);
				StaticLayout layout = new StaticLayout((String) lrcList.get(position).get("rows"), (TextPaint) mPaint, sWidth, Alignment.ALIGN_CENTER, 1.0f, 0.0f, true);
				canvas.save();
				canvas.translate(x, y);
				layout.draw(canvas);
				canvas.restore();
				//canvas.drawText((String) lrcList.get(position).get("rows"), x, y, mPaint);

				for (int i = position + 1, j = 1; i < lrcList.size(); i++, j++) {
					//tempString=(String) lrcList.get(i).get("rows");
					float newx = 0f;
					//float newx=(sWidth/2-nPaint.measureText(tempString)/2);
					float newy = (sHeight / 2 + (interval - fontHeight) * j - offset);
					layout = new StaticLayout((String) lrcList.get(i).get("rows"), (TextPaint) nPaint, sWidth, Alignment.ALIGN_CENTER, 1.0f, 0.0f, true);
					canvas.save();
					canvas.translate(newx, newy);
					layout.draw(canvas);
					canvas.restore();
					//canvas.drawText((String) lrcList.get(i).get("rows"), newx, newy, nPaint);
				}
				//Log.i("next", String.valueOf(next));
			}
		}

	}


	//初始化画笔
	private void initPaint() {
		getTime = new GetTime();
		mPaint = new TextPaint();
		mPaint.setColor(Color.GREEN);
		mPaint.setTextSize(45);

		mPaint.setAntiAlias(true);
		//mPaint.setTextScaleX(1.5f);
		nPaint = new TextPaint();
		nPaint.setColor(Color.argb(200,255,229,231));
		nPaint.setTextSize(40);
		nPaint.setAntiAlias(true);
		sWidth = getWidth();
		sHeight = getHeight();
		interval = 40;
		//FontMetrics mFont=mPaint.getFontMetrics();
		FontMetrics nFont = nPaint.getFontMetrics();
		//System.out.println(nFont.bottom+"   "+nFont.top);
		//System.out.println(mFont.bottom+"   "+mFont.top);
		fontHeight = (int) (nFont.bottom + nFont.top);

		paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setTextSize(30);
		paint.setAntiAlias(true);


	}

	/**
	 * 处理歌词文件函数，播放前需要先调用该 函数，为显示歌词做准备.注意，rows还需要排序，因为不排除原来文件中歌词顺序有乱序情况
	 *
	 * @param  //要处理的歌词文件，包括路径
	 * @param endTime 歌曲的总时间，用于处理最后一句
	 * @return List<Map<String,Object>> ,其中，map有哪个键值对，一个"time",表示时间；一个"rows",表示对应出现的歌词
	 */
	public List<Map<String, Object>> parseLrcFile(InputStreamReader input, int endTime) {
		try {
			BufferedReader buffer = new BufferedReader(input);
			String line = "";
			if(lrcList!=null){
				lrcList.clear();
			}
			while ((line = buffer.readLine()) != null) {
				System.out.println(line);
				System.out.println("*");
				//匹配带时间的空行
				Matcher matcher = Pattern.compile("\\[\\d{2}:\\d{2}\\.\\d{3}\\]").matcher(line);            //正则匹配表达式，实际上为\[.+\].+，但是因为\要表示为\，必须是\\。
				System.out.println(matcher.matches());
				if (matcher.matches()) {
					line = line.replace("[", "");
					String[] tempString = line.split("]");
					for (int i = 0; i < tempString.length; i++) {
						Map<String, Object> newMap = new HashMap<String, Object>();
						newMap.put("time", tempString[i]);
						newMap.put("rows", "");
						lrcList.add(newMap);
					}
				}
				//matcher = Pattern.compile("(\\[\\d{2}:\\d{2}\\.\\d{2}+\\]){1,}.+").matcher(line);
				matcher = Pattern.compile("\\[\\d{2}:\\d{2}\\.\\d{2,3}\\].+").matcher(line);
				//匹配非空时间段
				System.out.println(matcher.matches());
				if (matcher.matches()) {
					line = line.replace("[", "");
					String[] tempString = line.split("]");
					if (tempString.length >= 3) {
						for (int i = 0; i < tempString.length - 1; i++) {
							Map<String, Object> newMap = new HashMap<String, Object>();
							newMap.put("time", tempString[i]);
							newMap.put("rows", tempString[tempString.length - 1]);
							lrcList.add(newMap);
						}
					} else {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("time", tempString[0]);
						//System.out.println(tempString[0]);
						map.put("rows", tempString[1]);
						lrcList.add(map);
					}
				}

			}
			if(lrcList.size()==0){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("time", "00:00.011");
				map.put("rows", "未查找到相关歌词");
				lrcList.add(map);
			}
			System.out.println("lrcList" + lrcList.size());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}


		//把歌词按时间排序
		Collections.sort(lrcList, new LrcSort());
		for (int i = 0; i < lrcList.size(); i++) {
			System.out.println(lrcList.get(i).get("time") + (String) lrcList.get(i).get("rows"));
		}
		getTime = new GetTime();
		String string = getTime.timeTransTxt(endTime);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("time", string);
		map.put("rows", "");
		lrcList.add(map);
		return lrcList;
	}

	public void resetLrc() {
		isFirst = true;
		position = -1;
	}

	public int showLrc(int time) {
		int lrcTime = 0;
		int lrclastTime = 0;
		if (!isDrag) {
			step+=4;
		}
		this.time = time;
		if (step > totalSteps) {
			step = totalSteps;
		}
		if (!isDrag && isFirst) {
			seekLrcToTime(time);
			isFirst = false;
		}
		if (position >= lrcList.size()) {
			position = lrcList.size() - 1;
		}
		if (position < lrcList.size() - 2) {
			lrcTime = getTime.textTransTime((String) lrcList.get(position + 1).get("time"));
			if (position >= 0) {
				lrclastTime = getTime.textTransTime((String) lrcList.get(position).get("time"));
			}
			if (time >= lrcTime) {
				String temp = (String) lrcList.get(position + 1).get("time");
				String temp2 = (String) lrcList.get(position + 2).get("time");
				if (!isDrag) {
					position++;
					totalSteps = getTime.interTime(temp, temp2, -1) / 100;
					step = 0;
				}
				//System.out.println(totalSteps+"totoal");
			} else if (time <= lrclastTime){
				String temp = (String) lrcList.get(position + 1).get("time");
				String temp2 = (String) lrcList.get(position + 2).get("time");
				if (!isDrag) {
					position--;
					totalSteps = getTime.interTime(temp, temp2, -1) / 100;
					step = 0;
				}
		}
	}

	postInvalidate();

	return time;
}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				posY = event.getY();
				posX = event.getX();
				setTime = time;
				break;
			case MotionEvent.ACTION_MOVE:
				invalidate();
				lastY = event.getY();
				lastX = event.getX();
				offsetX = (int) (lastX - posX);
				offsetY = (int) (lastY - posY);
				//System.out.println(offsetX+" @@@@  "+offsetY);
				if(Math.abs(offsetY)>20){

				if (Math.abs(offsetX) > Math.abs(offsetY)) {
					onSeekToListener.onScrollToX(offsetX);
					//System.out.println("onScrollTox");
				} else {
					isDrag = true;
					doNewScroll();
				}
				}
				//Log.i("offsetY", String.valueOf(offsetY));
				break;
			case MotionEvent.ACTION_UP:
				//System.out.println("Action_UP");
				if (isDrag) {
					isDrag = false;
					onSeekToListener.onLrcSeeked(setTime);
				}
				invalidate();

				time = setTime;
				break;
		}
		return true;
	}


	private void doNewScroll() {
		GetTime getTime = new GetTime();
		posY = lastY;
		//addX+=Math.abs(lastX-posX);
		posX = lastX;
		//addY+=Math.abs(offsetY);
		//Log.i("addX+addY", String.valueOf(addX)+"  "+String.valueOf(addY));
		int dragTime = -offsetY / 2 * 400;
		System.out.println("dragTime=="+dragTime);
		setTime += dragTime;
		int endTime = getTime.textTransTime((String) lrcList.get(lrcList.size() - 1).get("time"));
		System.out.println("endTime=="+endTime);
		System.out.println("setTime1=="+setTime);
		setTime = setTime >= endTime ? endTime : setTime;
		System.out.println("setTime2=="+setTime);
		setTime = setTime <= 0 ? 0 : setTime;
		System.out.println("setTime3=="+setTime);
		if (setTime == 0) {
			position = -1;
		} else if (setTime == endTime) {
			position = lrcList.size() - 1;
		}

		Log.i("doNewScroll", String.valueOf(setTime) + "  step" + String.valueOf(step) + "  totalstep" + String.valueOf(totalSteps));

		seekLrcToTime(setTime);
	}


	@Override
	public void seekLrcToTime(int time) {
		System.out.println("time=="+time);
		int lrcTime = 0;
		int lrcTime2 = 0;
		for (int i = 0; i < lrcList.size() - 2; i++) {
			lrcTime = getTime.textTransTime((String) lrcList.get(i).get("time"));
			lrcTime2 = getTime.textTransTime((String) lrcList.get(i + 1).get("time"));
			if (lrcTime <= time && time < lrcTime2) {
				position = i;
				totalSteps = (lrcTime2 - lrcTime) / 100;
				step = (time - lrcTime) / 100 - 1;
				System.out.println(step + "totalstep" + totalSteps);
				break;
			}
		}
	}

	@Override
	public void setListener(OnSeekToListener l) {
		// TODO Auto-generated method stub
		this.onSeekToListener = l;
		Log.i("setListener", "It's OK");
	}


}
