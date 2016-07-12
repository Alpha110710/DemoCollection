package com.lanou.chenfengyao.smalldemos.expandrv;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ChenFengYao on 16/7/12.
 */
public class MyAdapter extends ExRvAdapter {
    private HashMap<String, List<String>> data;
    private List<String> group;
    private Context context;

    public MyAdapter(Context context) {
        this.context = context;
        data = new HashMap<>();
        group = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            List<String> strings = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                strings.add("这是子-"+j);
            }
            group.add("这是组" + i);
            data.put("这是组" + i,strings);
        }
    }

    @Override
    public int getGroupCount() {
        return group.size();
    }

    @Override
    public int getChildCount(int groupPos) {
        return data.get(group.get(groupPos)).size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateGroupVH(ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(com.lanou.chenfengyao.expandablerecyclerview.R.layout.item,parent,false);

        return new MyVH(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateChildVH(ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(com.lanou.chenfengyao.expandablerecyclerview.R.layout.item,parent,false);

        return new MyVH(view);
    }

    @Override
    public void onBindGroupVH(RecyclerView.ViewHolder groupHolder, int groupPos) {
        MyVH myVH = (MyVH) groupHolder;
        myVH.textView.setText(group.get(groupPos));
    }

    @Override
    public void onBindChildVH(RecyclerView.ViewHolder childVH, int groupPos, int childPos) {
        MyVH myVH = (MyVH) childVH;
        myVH.textView.setText(data.get(group.get(groupPos)).get(childPos));
    }

    class MyVH extends RecyclerView.ViewHolder{
        TextView textView;
        public MyVH(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(com.lanou.chenfengyao.expandablerecyclerview.R.id.item_text);
        }
    }
}
