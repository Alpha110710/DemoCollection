package com.lanou.chenfengyao.musicdemo.channel;

import android.os.Bundle;
import android.util.Log;

import com.lanou.chenfengyao.musicdemo.R;
import com.lanou.chenfengyao.musicdemo.base.BaseAty;
import com.lanou.chenfengyao.musicdemo.base.BaseFragment;
import com.lanou.chenfengyao.musicdemo.utils.BindContent;

import org.xml.sax.SAXException;
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
        String url = getArguments().getString("url");
        try {

            InputStream stream = context.getAssets().open("teahour.xml");

            //InputStreamReader inputStreamReader = new InputStreamReader(stream);
            // BufferedReader reader = new BufferedReader(inputStreamReader);
            //解析流，设定需要解析的节点
//            List<HashMap<String, String>> list
//                    = SaxService.readXML(stream, "person");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            //实例化SAX解析器。
            SAXParser sParser = factory.newSAXParser();
            //实例化DefaultHandler，设置需要解析的节点
            MyHandler myHandler = new MyHandler("item");
            // 开始解析
            sParser.parse(stream, myHandler);
            List<HashMap<String, String>> list = myHandler.getList();
            Log.d("sax",list.size()+"");
            for (HashMap<String, String> map : list) {
                //打印到LogCat中
               Log.d("sax",map.toString());
            }
//            String out = "";
//            while ((out = reader.readLine())!=null){
//                Log.d("Sysout",out);
//            }
//            reader.close();
//            inputStreamReader.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}
