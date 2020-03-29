package com.leofanti.gat.utils;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;




public class RecyclerViewGestureListener implements RecyclerView.OnItemTouchListener{

    private String TAG = "JCHGESTURE";

    private GestureDetector gestureDetector;
    private RecyclerViewTouchListener clickListener;
    //TODO agregar swipes
    //https://www.mytrendin.com/detecting-swipe-gestures-android-tutorial/
    public RecyclerViewGestureListener(Context context, final RecyclerView recyclerView, final RecyclerViewTouchListener clickListener) {

        this.clickListener = clickListener;

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }

        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
           int position = rv.getChildLayoutPosition(child);
           Log.d(TAG, "onClick Position : " + position);
           clickListener.onClick(child, position);
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }


}