package com.lanou.chenfengyao.musicdemo.mian;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lanou.chenfengyao.musicdemo.R;
import com.lanou.chenfengyao.musicdemo.model.MusicBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2016/7/9.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private LayoutInflater layoutInflater;
    private List<MusicBean> musicBeen;
    private Context context;

    public MainAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        musicBeen = new ArrayList<>();
        MusicBean musicBean = new MusicBean();
        musicBean.setMusicName("Teahour.fm");
        musicBean.setBgPath("http://teahour.fm/images/new_logo.png");
        musicBeen.add(musicBean);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_main_channel,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MusicBean musicBean = musicBeen.get(position);
        Glide.with(context).load(musicBean.getBgPath())
                .into()
    }

    @Override
    public int getItemCount() {
        return musicBeen.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView bgIv;
        public MyViewHolder(View itemView) {
            super(itemView);
            bgIv = itemView.findViewById(R.id.item_main_rv)
        }
    }
}
