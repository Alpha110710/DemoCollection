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


public class RSSHandler extends DefaultHandler {
    RSSFeed rssFeed;
    RSSItem rssItem;
    final int ITEM_TITLE = 1;
    final int ITEM_LINK = 2;
    final int ITEM_DESCRIPTION = 3;
    final int ITEM_CATEGORY = 4;
    final int ITEM_PUBDATE = 5;
    final int C_TITLE = 6;
    final int C_SUMMARY = 7;
    final int C_LOGO = 8;

    int currentstate = 0;
    private boolean begin = true;

    public RSSHandler() {
    }

    public RSSFeed getFeed() {
        Log.d("RSSHandler", "rssFeed:" + rssFeed);
        return rssFeed;
    }

    @Override
    public void startDocument() throws SAXException {
        rssFeed = new RSSFeed();
        rssItem = new RSSItem();
    }

    @Override
    public void endDocument() throws SAXException {

    }

    //qName是全名 而localName 是:后面的
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        Log.d("RSSHandler", qName);

        if (localName.equals("channel")) {
            currentstate = 0;
            return;
        }
        if (localName.equals("title") && begin) {
            currentstate = C_TITLE;
            return;
        }
        if (qName.equals("itunes:summary") && begin) {
            currentstate = C_SUMMARY;
            return;
        }
        if (qName.equals("itunes:image") && begin) {
            currentstate = C_LOGO;
            return;
        }
        if (localName.equals("item")) {
            rssItem = new RSSItem();
            begin = false;
            return;
        }
        if (qName.equals("enclosure")) {//设置音频url
            rssItem.setEnclosureUrl(attributes.getValue("url"));
        }
        if (localName.equals("title")) {
            currentstate = ITEM_TITLE;
            return;
        }
        if (localName.equals("description")) {
            currentstate = ITEM_DESCRIPTION;
            return;
        }
        if (localName.equals("link")) {
            currentstate = ITEM_LINK;
            return;
        }
        if (localName.equals("category")) {
            currentstate = ITEM_CATEGORY;
            return;
        }
        if (localName.equals("pubDate")) {
            currentstate = ITEM_PUBDATE;
            return;
        }
        currentstate = 0;
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (localName.equals("item")) {
            rssFeed.addItem(rssItem);
            return;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        String theString = new String(ch, start, length);
        switch (currentstate) {
            case C_TITLE:
                rssFeed.setTitle(theString);
                currentstate = 0;
                break;
            case C_LOGO:
                rssFeed.setLogoUrl(theString);
                break;
            case C_SUMMARY:
                rssFeed.setSummary(theString);
                break;
            case ITEM_TITLE:
                rssItem.setTitle(theString);
                currentstate = 0;
                break;
            case ITEM_DESCRIPTION:
                rssItem.setDescription(theString);
                currentstate = 0;
                break;
            case ITEM_LINK:
                rssItem.setLink(theString);
                currentstate = 0;
                break;
            case ITEM_PUBDATE:
                rssItem.setPubdate(theString);
                currentstate = 0;
                break;
            case ITEM_CATEGORY:
                rssItem.setCategory(theString);
                currentstate = 0;
                break;
            default:
                return;
        }
    }
}
