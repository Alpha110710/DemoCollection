package com.lanou.chenfengyao.smalldemos.expandrv;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by ChenFengYao on 16/7/12.
 */
public abstract class ExRvAdapter extends RecyclerView.Adapter {
    private final int TYPE_GROUP = 0;
    private final int TYPE_CHILD = 1;

    public abstract int getGroupCount();

    public abstract int getChildCount(int groupPos);

    public abstract RecyclerView.ViewHolder onCreateGroupVH(ViewGroup parent);

    public abstract RecyclerView.ViewHolder onCreateChildVH(ViewGroup parent);

    public abstract void onBindGroupVH(RecyclerView.ViewHolder groupHolder, int groupPos);

    public abstract void onBindChildVH(RecyclerView.ViewHolder childVH, int groupPos, int childPos);

    @Override
    public int getItemViewType(int position) {
        PosInfo posInfo = getPosInfo(position);
        if (posInfo.child < 0) {
            return TYPE_GROUP;
        } else {
            return TYPE_CHILD;
        }
    }


    //通过pos来判断位置
    private PosInfo getPosInfo(int pos) {
        pos++;
        PosInfo posInfo = new PosInfo();
        posInfo.group = -1;
        posInfo.child = -1;
        while (pos > 0) {
            posInfo.group++;
            pos--;
            if (pos <= 0) {
                break;
            }
            if (pos > getChildCount(posInfo.group)) {
                posInfo.child = -1;
                pos -= getChildCount(posInfo.group);
            } else {
                posInfo.child = pos - 1;
                break;
            }
        }
        return posInfo;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_GROUP:
                return onCreateGroupVH(parent);
            case TYPE_CHILD:
                return onCreateChildVH(parent);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        PosInfo posInfo = getPosInfo(position);
        switch (type) {
            case TYPE_GROUP:
                onBindGroupVH(holder, posInfo.group);
                break;
            case TYPE_CHILD:
                onBindChildVH(holder, posInfo.group, posInfo.child);
                break;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (int i = 0; i < getGroupCount(); i++) {
            count += getChildCount(i);
        }
        return getGroupCount() + count;
    }

    class PosInfo {
        int group;
        int child;
    }

}
