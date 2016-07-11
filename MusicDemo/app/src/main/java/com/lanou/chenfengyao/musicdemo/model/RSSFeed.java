package com.lanou.chenfengyao.musicdemo.model;

/**
 * Created by ChenFengYao on 16/7/11.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class RSSFeed {
    private String title;
    private int itemcount;
    
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

    /**
     * 负责从RSSFeed类中生成列表所需要的数据
     * @return
     */
    public List getAllItemForListView(){
        List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
        int size = itemList.size();
        for(int i=0 ; i<size ; i++){
            HashMap<String , Object> item = new HashMap<String, Object>();
            item.put(RSSItem.TITLE, itemList.get(i).getTitle());
            item.put(RSSItem.PUBDATE, itemList.get(i).getPubdate());
            data.add(item);
        }
        return data;
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
