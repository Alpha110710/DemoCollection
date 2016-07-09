package com.lanou.chenfengyao.musicdemo.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.lanou.chenfengyao.musicdemo.utils.BindContent;
import com.lanou.chenfengyao.musicdemo.utils.NoLayoutBindException;

import java.util.HashMap;
import java.util.Stack;


/**
 * Created by ChenFengYao on 16/4/5.
 * Activity的基类,所有的Activity默认使用Auto布局
 */
public abstract class BaseAty extends AppCompatActivity {

    protected HashMap<Integer, Stack<BaseFragment>> stackHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stackHashMap = new HashMap<>();

        setContent();//绑定布局
        setAty();
        initView();
        initData();
    }

    protected void addFragment(BaseFragment fragment, int id) {
        Stack<BaseFragment> stack = stackHashMap.get(id);
        if (stack == null) {
            stack = new Stack<>();
            stackHashMap.put(id,stack);
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (stack.size() > 0) {
            transaction.add(id,fragment).show(fragment).hide(stack.peek())
                    .commit();
        }else {
            transaction.add(id,fragment).commit();
        }
        stack.push(fragment);

    }

    protected void back(int id) {
        Stack<BaseFragment> fragmentStack = stackHashMap.get(id);
        if(fragmentStack == null){
            Log.d("Sysout",id + "没有加入过Fragment");
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragmentStack.size() > 1) {
            BaseFragment currentFragment = fragmentStack.pop();
            BaseFragment preFragment = fragmentStack.peek();
            transaction.remove(currentFragment).show(preFragment).commit();
        }else {
            transaction.remove(fragmentStack.pop()).commit();
            stackHashMap.remove(id);
        }
    }

    /**
     * 绑定布局的方法
     * 使用的时候,只需要在类前加上@BindContent(R.layout.xx)即可
     */
    private void setContent() {
        Class clazz = this.getClass();
        if (clazz.isAnnotationPresent(BindContent.class)) {
            BindContent bindContent = (BindContent) clazz.getAnnotation(BindContent.class);
            int id = bindContent.value();
            if (id > 0) {
                this.setContentView(id);
            }
        } else {
            throw new NoLayoutBindException(clazz.getSimpleName() + "没有绑定布局");
        }
    }

    /**
     * 在绑定布局前如果需要多Activity进行设置
     * 则复写此方法
     */
    protected void setAty() {
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

    protected <T extends View> T bindView(@IdRes int id) {
        return (T) findViewById(id);
    }

    protected <T extends View> T bindView(@IdRes int id, View view) {
        return (T) view.findViewById(id);
    }

    @Override
    protected void onDestroy() {
        MyApplication.removeAty(this);//从ActivityList中移除
        super.onDestroy();
    }
}
