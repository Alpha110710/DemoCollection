package com.lanou.chenfengyao.musicdemo.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ChenFengYao on 16/4/5.
 */
public class MyApplication extends Application {
    public static Context context;
    //存放所有的Activity
    private static List<Activity> activityList;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        activityList = new LinkedList<>();
    }

    public static void addAty(Activity activity){
        activityList.add(activity);
    }

    public static void removeAty(Activity activity){
        activityList.remove(activity);
    }

    /**
     * 退出所有的Activity
     */
    public static void finishAll(){
        for (int i = activityList.size() - 1; i >= 0 ; i--) {
            activityList.get(i).finish();
        }
    }
}
