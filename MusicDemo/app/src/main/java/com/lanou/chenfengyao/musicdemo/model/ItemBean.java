package com.lanou.chenfengyao.musicdemo.model;

import java.util.List;

/**
 * Created by hasee on 2016/7/9.
 */
public class ItemBean {
    String title;
    List<Author> authors;
    String summary;
    String image;
    String description;
    String link;
    String guid;
    public class Author{
        private String name;
        private String headUrl;
    }
}
