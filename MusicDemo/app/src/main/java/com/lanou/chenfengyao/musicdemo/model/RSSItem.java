package com.lanou.chenfengyao.musicdemo.model;

/**
 * Created by ChenFengYao on 16/7/11.
 */
public class RSSItem {
    public static String TITLE = "title";
    public static String PUBDATE = "pubdate";
    public String title;
    public String description;
    public String link;
    public String category;
    public String pubdate;
    public RSSItem() {
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getPubdate() {
        return pubdate;
    }
    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }


}