package com.lanou.chenfengyao.smalldemos.ui.activity.translucentbar;

import android.os.Bundle;

import com.lanou.chenfengyao.smalldemos.R;
import com.lanou.chenfengyao.smalldemos.ui.activity.base.TranslucentBarBaseActivity;


/**
 * TranslucentBar最好的实现方式
 *
 * @author Clock
 * @since 2016-02-22
 */
public class BestTranslucentBarActivity extends TranslucentBarBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_best_translucent_bar;
    }
}
