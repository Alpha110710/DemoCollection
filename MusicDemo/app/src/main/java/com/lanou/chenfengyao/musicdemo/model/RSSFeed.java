package com.lanou.chenfengyao.musicdemo.model;

/**
 * Created by ChenFengYao on 16/7/11.
 */
import java.util.List;
import java.util.Vector;

public class RSSFeed {
    private String title;
    private int itemcount;
    private String summary;
    private String logoUrl;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    private List<RSSItem> itemList;


    public RSSFeed(){
        itemList = new Vector<RSSItem>(0);
    }


    /**
     * 负责将一个RSSItem加入到RSSFeed类中
     * @param item
     * @return
     */
    public int addItem(RSSItem item){
        itemList.add(item);
        itemcount++;
        return itemcount;
    }

    public RSSItem getItem(int location){
        return itemList.get(location);
    }

    public List<RSSItem> getAllItems(){
        return itemList;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getItemcount() {
        return itemcount;
    }

    public void setItemcount(int itemcount) {
        this.itemcount = itemcount;
    }

    public List<RSSItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<RSSItem> itemList) {
        this.itemList = itemList;
    }

}
