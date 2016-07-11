package com.lanou.chenfengyao.musicdemo.channel;

import android.os.Bundle;
import android.util.Log;

import com.lanou.chenfengyao.musicdemo.R;
import com.lanou.chenfengyao.musicdemo.base.BaseAty;
import com.lanou.chenfengyao.musicdemo.base.BaseFragment;
import com.lanou.chenfengyao.musicdemo.model.RSSFeed;
import com.lanou.chenfengyao.musicdemo.utils.BindContent;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by hasee on 2016/7/9.
 */
@BindContent(value = R.layout.fragmnet_channel_detail)
public class ChannelDetailFragment extends BaseFragment {
    private RSSFeed rssFeed;

    public static ChannelDetailFragment instance(String url) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        ChannelDetailFragment channelDetailFragment = new ChannelDetailFragment();
        channelDetailFragment.setArguments(bundle);
        return channelDetailFragment;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        rssFeed = getRssFeed("url");
        Log.d("ChannelDetailFragment", rssFeed.getTitle());
        Log.d("ChannelDetailFragment", "rssFeed.getAllItems().size():" + rssFeed.getAllItems().size());
    }

    private RSSFeed getRssFeed(String rssUrl){
        try {
// 这里我们实现了本地解析，所以注掉了这个取网络数据的。
//            URL url = new URL(rssUrl);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            RSSHandler handler = new RSSHandler();
            reader.setContentHandler(handler);
            InputStream inputStream = context.getAssets().open("teahour.xml");
            InputSource is = new InputSource(inputStream);//取得本地xml文件

            reader.parse(is);
            return handler.getFeed();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
