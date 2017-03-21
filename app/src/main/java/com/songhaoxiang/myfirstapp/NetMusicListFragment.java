package com.songhaoxiang.myfirstapp;


import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.songhaoxiang.myfirstapp.adapter.NetMusicListAdapter;
import com.songhaoxiang.myfirstapp.net.download.DownloadProgressListener;
import com.songhaoxiang.myfirstapp.net.download.FileDownloader;
import com.songhaoxiang.myfirstapp.utils.BaiduPicThread;
import com.songhaoxiang.myfirstapp.utils.NetMusic;
import com.songhaoxiang.myfirstapp.utils.ReaderJSONUtil;
import com.songhaoxiang.myfirstapp.utils.ShowApiRequest;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 宋浩祥 on 2016/1/25.
 */
public class NetMusicListFragment extends Fragment implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
	private SearchView searchView;
	private TextView resultView;
	public String songName;
	private String page = "1";
	private ListView listView_net_music;
	private ProgressBar progressBar;
	private int temp = 0;
	public NetMusicListAdapter netMusicListAdapter;
	public ArrayList<NetMusic> list = new ArrayList<>();
	public ArrayList<NetMusic> QQlist = new ArrayList<>();
	public ArrayList<NetMusic> WYlist = new ArrayList<>();
	private MainActivity mainActivity;
	private boolean querytemp = false;
	private int click_num = 0;
	ReaderJSONUtil readerJSONUtil = new ReaderJSONUtil(this);

	public static NetMusicListFragment newInstance() {
		NetMusicListFragment netMusicListFragment = new NetMusicListFragment();
		return netMusicListFragment;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_netmusic, null);
		searchView = (SearchView) view.findViewById(R.id.search_view);
		listView_net_music = (ListView) view.findViewById(R.id.listView);
		resultView = (TextView) view.findViewById(R.id.resultView);
		resultView.setVisibility(View.GONE);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		progressBar.setVisibility(View.GONE);
		resultView.setVisibility(View.GONE);
		searchView.setOnQueryTextListener(this);
		searchView.clearFocus();
		listView_net_music.setOnItemClickListener(this);
		listView_net_music.setOnItemLongClickListener(this);
		return view;
	}


	@Override
	public boolean onQueryTextSubmit(String query) {
		if (!query.equals(songName)) {
			querytemp = true;
			songName = query;
			readerJSONUtil.WangyiGetJSON(songName, this);
			startSearchSong();
			if (list != null) {
				list.clear();
			}
		} else {
			if (list == null || list.isEmpty()) {
				Toast.makeText(mainActivity, "正在拼命为宝宝查询，表捉急~~", Toast.LENGTH_LONG).show();
			}
		}
		// readerJSONUtil.Tingge123(songName);

		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}


	private void startSearchSong() {
		final AsyncHttpResponseHandler resHandler = new AsyncHttpResponseHandler() {
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
				//做一些异常处理
				e.printStackTrace();
			}

			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				try {
					System.out.println("response is :" + new String(responseBody, "utf-8"));
					String json = new String(responseBody, "utf-8") + new Date();
					beginReaderQQJSON(json);
					//在此对返回内容做处理
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

			}
		};
		new ShowApiRequest("http://route.showapi.com/213-1", "16599", "8501f52ec00a41ecbdd2e9e58ad5b57d")
				.setResponseHandler(resHandler)
				.addTextPara("keyword", songName)
				.addTextPara("page", page)
				.post();


	}

	private void beginReaderQQJSON(String json) {
		if (QQlist != null) {
			QQlist.clear();
		}
		QQlist = readerJSONUtil.QQReaderJSONUtil(json, this);
		temp++;
		loadData(QQlist, -2);
	}

	public void loadData(ArrayList<NetMusic> list, int position) {
		if (position == -1) {
			WYlist = list;
			temp++;
		}
		if (temp == 2) {
			temp = 0;
			if (this.list != null) {
				this.list.clear();
			}
			int i = 0;
			while (i < QQlist.size() && i < WYlist.size()) {
				this.list.add(QQlist.get(i));
				this.list.add(WYlist.get(i));
				i++;
			}
			if (i < QQlist.size()) {
				while (i < QQlist.size()) {
					this.list.add(QQlist.get(i));
					i++;
				}
			} else {
				while (i < WYlist.size()) {
					this.list.add(WYlist.get(i));
					i++;
				}
			}
			netMusicListAdapter = new NetMusicListAdapter(getActivity(), this.list, position);
			System.out.println("list的尺寸是" + list.size());
			//填充数据到列表
			listView_net_music.setAdapter(netMusicListAdapter);
		}


	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mainActivity = (MainActivity) context;
	}

	private boolean threadisruning = false;
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		click_num++;
		if (click_num <= 3) {
			mainActivity.playService.netplay(position, list);
			if (!threadisruning) {
				threadisruning = true;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(1000);//每多次点击中从第一次点击1000毫秒后开始清零
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("一秒钟点击了click_num="+click_num);
						click_num = 0;
						threadisruning = false;
					}
				}).start();
			}
		} else {

		}
		if(netMusicListAdapter!=null){
			netMusicListAdapter.setPosition(position);
			netMusicListAdapter.notifyDataSetChanged();
		}
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		listView_net_music.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
				menu.add(0, position, 0, "播放");
				menu.add(0, position, 1, "下载");
				menu.add(0, position, 2, "设为喜欢");
				menu.add(0, position, 3, "关于");
			}
		});
		return false;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getOrder()) {
			case 0:
				Toast.makeText(getContext(), "正在缓冲....", Toast.LENGTH_LONG).show();
				mainActivity.playService.netplay(item.getItemId(), list);
				break;
			case 1:
				NetMusic song = list.get(item.getItemId());
				String path = song.getDownUrl();
				String filename = song.getSingername() + " - " + song.getSongname() + ".mp3";
				//如果我们想要读取或者向SD卡写入，这时就必须先要判断一个SD卡的状态，否则有可能出错。
				//详情请参阅http://blog.csdn.net/yuzhiboyi/article/details/8645730
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// File savDir =
					// Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
					// 保存路径
					/**
					 * 这里有改动，源程序是File savDir = Environment.getExternalStorageDirectory();
					 * 因为如果你的api 版本低于8，那么不能使用getExternalStoragePublicDirectory()，
					 * 而是使用Environment.getExternalStorageDirectory(),他不带参数，
					 * 也就不能自己创建一个目录，只是返回外部存储的根路径。
					 * */
					//File savDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),filename);
					File savDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
					download(path, savDir, filename);
					progressBar.setVisibility(View.VISIBLE);
					resultView.setVisibility(View.VISIBLE);
				} else {
					Toast.makeText(getContext(),
							"无SD卡", Toast.LENGTH_LONG).show();
				}

				break;
			case 2:

				break;
			case 3:

				break;

		}
		searchView.clearFocus();

		return true;
	}


	private DownloadTask task;

	private void exit() {
		if (task != null)
			task.exit();
	}

	private void download(String path, File savDir, String filename) {
		task = new DownloadTask(path, savDir, filename);
		new Thread(task).start();
	}

	/**
	 * UI控件画面的重绘(更新)是由主线程负责处理的，如果在子线程中更新UI控件的值，更新后的值不会重绘到屏幕上
	 * 一定要在主线程里更新UI控件的值，这样才能在屏幕上显示出来，不能在子线程中更新UI控件的值
	 */
	private final class DownloadTask implements Runnable {
		private String path;
		private File saveDir;
		private FileDownloader loader;
		private String filename;

		public DownloadTask(String path, File saveDir, String filename) {
			this.path = path;
			this.saveDir = saveDir;
			this.filename = filename;
		}

		/**
		 * 退出下载
		 */
		public void exit() {
			if (loader != null)
				loader.exit();
		}

		DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
			@Override
			public void onDownloadSize(int size) {
				Message msg = new Message();
				msg.what = 1;
				msg.getData().putInt("size", size);
				handler.sendMessage(msg);
			}
		};

		public void run() {
			try {
				// 实例化一个文件下载器
				loader = new FileDownloader(getContext(), path,
						saveDir, filename, 3);
				// 设置进度条最大值
				progressBar.setMax(loader.getFileSize());
				loader.download(downloadProgressListener);
			} catch (Exception e) {
				e.printStackTrace();
				handler.sendMessage(handler.obtainMessage(-1)); // 发送一条空消息对象
			}
		}
	}

	private Handler handler = new UIHandler();

	private final class UIHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1: // 更新进度
					progressBar.setProgress(msg.getData().getInt("size"));
					float num = (float) progressBar.getProgress()
							/ (float) progressBar.getMax();
					int result = (int) (num * 100); // 计算进度
					resultView.setText(result + "%");
					if (progressBar.getProgress() == progressBar.getMax()) { // 下载完成
						Toast.makeText(getContext(), "下载成功！",
								Toast.LENGTH_LONG).show();
						progressBar.setVisibility(View.GONE);
						resultView.setVisibility(View.GONE);
					}
					break;
				case -1: // 下载失败
					Toast.makeText(getContext(), "下载出错",
							Toast.LENGTH_LONG).show();
					break;
			}
		}
	}
}
