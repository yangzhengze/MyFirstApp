package com.songhaoxiang.myfirstapp.utils;

/**
 * Created by 宋浩祥 on 2016/3/10.
 */
public class NetMusic {


    @Override
    public String toString() {
        return "Song{" +
                "albumid='" + albumid + '\'' +
                ", albumname='" + albumname + '\'' +
                ", albumpic_big='" + albumpic_big + '\'' +
                ", albumpic_small='" + albumpic_small + '\'' +
                ", downUrl='" + downUrl + '\'' +
                ", m4a='" + m4a + '\'' +
                ", singerid='" + singerid + '\'' +
                ", singername='" + singername + '\'' +
                ", songid='" + songid + '\'' +
                ", songname='" + songname + '\'' +
                '}';
    }

    public String albumname;         //海阔天空	专辑名称
    public String albumpic_big;      //http://imgcache.qq.com/music/photo/album_300/60/300_albumpic_62660_0.jpg	专辑大图片，高宽300
    public String albumpic_small;  //http://imgcache.qq.com/music/photo/album/60/90_albumpic_62660_0.jpg	专辑小图片，高宽90
    public String pingtai;
    public String getAlbumname() {
        return albumname;
    }

    public String getAlbumpic_big() {
        return albumpic_big;
    }

    public String getAlbumpic_small() {
        return albumpic_small;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public String getM4a() {
        return m4a;
    }

    public String getSingerid() {
        return singerid;
    }

    public String getSingername() {
        return singername;
    }

    public String getSongid() {
        return songid;
    }

    public String getAlbumid() {
        return albumid;
    }

    public String getSongname() {
        return songname;
    }

    public String downUrl;          //http:stream10.qqmusic.qq.com/34833285.mp3	mp3下载链接
    public String m4a;              //http:ws.stream.qqmusic.qq.com/4833285.m4a?fromtag=46	流媒体地址
    public String singerid;          //123	歌手id
    public String singername;          //BEYOND	歌手名
    public String songid;              //歌曲id
    public String albumid;           //专辑id
    public String songname;          //海阔天空	歌曲名称

}
