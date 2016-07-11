package com.lanou.chenfengyao.musicdemo.channel;

/**
 * Created by ChenFengYao on 16/7/11.
 */
import android.util.Log;

import com.lanou.chenfengyao.musicdemo.model.RSSFeed;
import com.lanou.chenfengyao.musicdemo.model.RSSItem;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class RSSHandler extends DefaultHandler{
    RSSFeed RssFeed;
    RSSItem RssItem;
    final int RSS_TITLE = 1;
    final int RSS_LINK = 2;
    final int RSS_DESCRIPTION = 3;
    final int RSS_CATEGORY = 4;
    final int RSS_PUBDATE = 5;
    final int C_TITLE = 6;
    int currentstate = 0;
    private boolean begin = true;

    public RSSHandler(){}

    public RSSFeed getFeed(){
        Log.d("RSSHandler", "RssFeed:" + RssFeed);
        return RssFeed;
    }

    @Override
    public void startDocument() throws SAXException {
        RssFeed = new RSSFeed();
        RssItem = new RSSItem();
    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        if(localName.equals("channel")){
            currentstate = 0;
            return;
        }
        if(localName.equals("title") && begin){
            currentstate = C_TITLE;
            return;
        }
        if(localName.equals("item")){
            RssItem = new RSSItem();
            begin = false;
            return;
        }
        if(localName.equals("title")){
            currentstate = RSS_TITLE;
            return;
        }
        if(localName.equals("description")){
            currentstate = RSS_DESCRIPTION;
            return;
        }
        if(localName.equals("link")){
            currentstate = RSS_LINK;
            return;
        }
        if(localName.equals("category")){
            currentstate = RSS_CATEGORY;
            return;
        }
        if(localName.equals("pubDate")){
            currentstate = RSS_PUBDATE;
            return;
        }
        currentstate = 0;
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if(localName.equals("item")){
            RssFeed.addItem(RssItem);
            return;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        String theString = new String(ch, start, length);
        switch(currentstate){
            case C_TITLE:
                RssFeed.setTitle(theString);
                currentstate = 0;
                break;
            case RSS_TITLE:
                RssItem.setTitle(theString);
                currentstate = 0;
                break;
            case RSS_DESCRIPTION:
                RssItem.setDescription(theString);
                currentstate = 0;
                break;
            case RSS_LINK:
                RssItem.setLink(theString);
                currentstate = 0;
                break;
            case RSS_PUBDATE:
                RssItem.setPubdate(theString);
                currentstate = 0;
                break;
            case RSS_CATEGORY:
                RssItem.setCategory(theString);
                currentstate = 0;
                break;
            default:
                return;
        }
    }
}
