package com.lanou.chenfengyao.musicdemo.mian;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.lanou.chenfengyao.musicdemo.R;
import com.lanou.chenfengyao.musicdemo.base.BaseFragment;
import com.lanou.chenfengyao.musicdemo.utils.BindContent;
import com.lanou.chenfengyao.musicdemo.utils.OnRecyclerItemClickListener;

/**
 * Created by hasee on 2016/7/9.
 */
@BindContent(value = R.layout.content_main)
public class MainFragment extends BaseFragment {
    private FloatingActionButton addChannelFab;
    private RecyclerView channelRv;
    private MainAdapter mainAdapter;


    @Override
    protected void initView() {
        addChannelFab = (FloatingActionButton) bindView(R.id.fab);
        channelRv = bindView(R.id.main_rv);
    }

    @Override
    protected void initData() {
        addChannelFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mainAdapter = new MainAdapter(context);
        channelRv.setLayoutManager(new GridLayoutManager(context,2));
        channelRv.setAdapter(mainAdapter);
        initItemClick();
    }

    //利用收拾处理recyclerView的点击事件
    private void initItemClick(){
        channelRv.addOnItemTouchListener(new OnRecyclerItemClickListener(channelRv){
            @Override
            public void onItemClick(int pos) {
                Toast.makeText(context, "pos" + pos, Toast.LENGTH_SHORT).show();
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.showChannelDetail(pos+"");
            }
        });
    }
}
