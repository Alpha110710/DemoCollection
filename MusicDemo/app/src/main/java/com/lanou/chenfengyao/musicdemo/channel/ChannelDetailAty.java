package com.lanou.chenfengyao.musicdemo.channel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lanou.chenfengyao.musicdemo.R;
import com.lanou.chenfengyao.musicdemo.base.BaseAty;
import com.lanou.chenfengyao.musicdemo.model.RSSFeed;
import com.lanou.chenfengyao.musicdemo.utils.BindContent;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by hasee on 2016/7/9.
 */
@BindContent(value = R.layout.activity_channel_detail)
public class ChannelDetailAty extends BaseAty {
    private RSSFeed rssFeed;

    public static void instance(Context context,String url) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        Intent intent = new Intent(context, ChannelDetailAty.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        rssFeed = getRssFeed("url");
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
            InputStream inputStream = getAssets().open("teahour.xml");
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
