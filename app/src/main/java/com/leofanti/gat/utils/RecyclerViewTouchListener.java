package com.leofanti.gat.utils;

//http://www.androidtutorialshub.com/android-recyclerview-click-listener-tutorial/

import android.view.View;

public interface RecyclerViewTouchListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}