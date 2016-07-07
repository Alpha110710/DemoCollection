package com.lanou.chenfengyao.musicdemo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChenFengYao on 16/7/5.
 * 简单的音乐曲目数据提供者。
 * 来提供音乐数据的类。
 */
public class MusicProvider {
    private List<MusicBean> musicBeanList;//播放列表

    public MusicProvider() {
        getMusicBeanList();
    }

    //获得歌曲列表
    public void getMusicBeanList(){
        musicBeanList = new ArrayList<>();
    }

    //设置歌曲列表
    public void setMusicBeanList(){

    }

    //这是对的
    //通过索引来确定播放的歌曲
    public MusicBean getMusicFromIndex(int index){
        if(index>=0 && index < musicBeanList.size()){
            return musicBeanList.get(index);
        }
        return null;
    }

}
