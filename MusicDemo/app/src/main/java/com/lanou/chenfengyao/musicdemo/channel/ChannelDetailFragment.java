package com.lanou.chenfengyao.musicdemo.channel;

import android.os.Bundle;

import com.lanou.chenfengyao.musicdemo.base.BaseAty;
import com.lanou.chenfengyao.musicdemo.base.BaseFragment;

/**
 * Created by hasee on 2016/7/9.
 */
public class ChannelDetailFragment extends BaseFragment {
    public static ChannelDetailFragment instance(String url) {
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        ChannelDetailFragment channelDetailFragment = new ChannelDetailFragment();
        channelDetailFragment.setArguments(bundle);
        return channelDetailFragment;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        String url = getArguments().getString("url");
    }
}
