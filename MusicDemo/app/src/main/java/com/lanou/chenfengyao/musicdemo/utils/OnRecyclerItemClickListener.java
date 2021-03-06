package com.lanou.chenfengyao.musicdemo.utils;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by hasee on 2016/7/9.
 */
public class OnRecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private GestureDetectorCompat gestureDetectorCompat;
    private RecyclerView recyclerView;

    public OnRecyclerItemClickListener(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        gestureDetectorCompat = new GestureDetectorCompat(recyclerView.getContext(),
                new ItemTouchHelperGestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetectorCompat.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetectorCompat.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
            if(child != null){
                RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
                int pos = vh.getLayoutPosition();
                onItemClick(pos);
            }
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
            if(child != null){
                RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
                int pos = vh.getLayoutPosition();
                onItemClick(pos);
            }
            return super.onDown(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
            if(child != null){
                RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
                int pos = vh.getLayoutPosition();
                onItemLongPress(pos);
            }
        }




    }
    public void onItemClick(int pos){
    }
    public void onItemLongPress(int pos){

    }


}
