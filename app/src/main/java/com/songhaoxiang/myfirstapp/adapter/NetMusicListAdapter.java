package com.songhaoxiang.myfirstapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.songhaoxiang.myfirstapp.R;
import com.songhaoxiang.myfirstapp.utils.ChooseUtil;
import com.songhaoxiang.myfirstapp.utils.NetMusic;

import java.util.ArrayList;

/**
 * Created by 宋浩祥 on 2016/1/25.
 */
public class NetMusicListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NetMusic> mp3Infos;
    private int position;

    public void setPosition(int position) {
        this.position = position;
    }

    public NetMusicListAdapter(Context context, ArrayList<NetMusic> Mp3Infos, int position) {
        this.context = context;
        this.mp3Infos = Mp3Infos;
        this.position = position;

    }


    public void setMp3Infos(ArrayList<NetMusic> mp3Infos) {
        this.mp3Infos = mp3Infos;
    }

    @Override
    public int getCount() {
        return mp3Infos.size();
    }

    public int getViewTypeCount() {
        return 2;
    }

    public int getItemViewType(int position) {
        if (position == this.position) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public Object getItem(int position) {
        return mp3Infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        int currentType= getItemViewType(position);
        if (convertView == null) {
            if (currentType == 0) {
                convertView = LayoutInflater.from(context).inflate(R.layout.player_music_list, null);
                vh = new ViewHolder();
                vh.textView_Singer = (TextView) convertView.findViewById(R.id.textView_Singer);
                vh.textView_SongName = (TextView) convertView.findViewById(R.id.textView_SongName);
                vh.textView_time = (TextView) convertView.findViewById(R.id.textView_time);
                convertView.setTag(vh);
            }else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_music_list_layout, null);
                vh = new ViewHolder();
                vh.textView_Singer = (TextView) convertView.findViewById(R.id.textView_Singer);
                vh.textView_SongName = (TextView) convertView.findViewById(R.id.textView_SongName);
                vh.textView_time = (TextView) convertView.findViewById(R.id.textView_time);
                convertView.setTag(vh);
            }
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        NetMusic mp3Info = mp3Infos.get(position);
        vh.textView_SongName.setText(mp3Info.getSongname());
        ChooseUtil chooseUtil = new ChooseUtil();
        chooseUtil.chooseUtil(vh.textView_Singer,mp3Info.getSingername(),mp3Info.getAlbumname(),mp3Info.getSongname());
        vh.textView_time.setText("");
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView textView_SongName;
        TextView textView_Singer;
        TextView textView_time;

    }
}
