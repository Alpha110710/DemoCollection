package com.lanou.chenfengyao.musicdemo.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lanou.chenfengyao.musicdemo.utils.BindContent;
import com.lanou.chenfengyao.musicdemo.utils.NoLayoutBindException;


/**
 * Created by ChenFengYao on 16/4/5.
 * Activity的基类,所有的Activity默认使用Auto布局
 */
public abstract class BaseAty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent();//绑定布局
        setAty();
        initView();
        initData();
    }

    /**
     * 绑定布局的方法
     * 使用的时候,只需要在类前加上@BindContent(R.layout.xx)即可
     */
    private void setContent(){
        Class clazz = this.getClass();
        if(clazz.isAnnotationPresent(BindContent.class)){
            BindContent bindContent = (BindContent) clazz.getAnnotation(BindContent.class);
            int id = bindContent.value();
            if(id > 0){
                this.setContentView(id);
            }
        }else {
            throw new NoLayoutBindException(clazz.getSimpleName() + "没有绑定布局");
        }
    }

    /**
     * 在绑定布局前如果需要多Activity进行设置
     * 则复写此方法
     */
    protected void setAty(){
        //在Activity创建的时候加入到List中
        MyApplication.addAty(this);
    }


    /**
     * 绑定布局里的组件
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    protected <T extends View> T bindView(@IdRes int id){
        return (T) findViewById(id);
    }

    protected <T extends View> T bindView(@IdRes int id,View view){
        return (T) view.findViewById(id);
    }
    @Override
    protected void onDestroy() {
        MyApplication.removeAty(this);//从ActivityList中移除
        super.onDestroy();
    }
}
