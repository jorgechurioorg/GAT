package com.leofanti.gat.utils;

import android.content.Context;
import android.content.res.Configuration;

public class configHelper {

    public configHelper() {};

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}



