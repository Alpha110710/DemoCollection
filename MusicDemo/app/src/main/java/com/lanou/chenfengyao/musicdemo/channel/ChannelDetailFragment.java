package com.lanou.chenfengyao.musicdemo.channel;

import android.os.Bundle;
import android.util.Log;

import com.lanou.chenfengyao.musicdemo.R;
import com.lanou.chenfengyao.musicdemo.base.BaseAty;
import com.lanou.chenfengyao.musicdemo.base.BaseFragment;
import com.lanou.chenfengyao.musicdemo.utils.BindContent;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by hasee on 2016/7/9.
 */
@BindContent(value = R.layout.fragmnet_channel_detail)
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
        try {

            InputStream stream = context.getAssets().open("teahour.xml");

            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String out = "";
            while ((out = reader.readLine())!=null){
                Log.d("Sysout",out);
            }
            reader.close();
            inputStreamReader.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
