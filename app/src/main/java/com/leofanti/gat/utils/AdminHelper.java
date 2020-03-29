package com.leofanti.gat.utils;

import android.content.Context;

import com.leofanti.gat.GatTon;
import com.leofanti.gat.model.Labels;
import com.leofanti.gat.model.ProductoTerminadoOut;

import java.util.ArrayList;

public class AdminHelper {


    private ArrayList<Labels> existingLabels = new ArrayList<>();
    private static String TAG = "GAT AdminHelper";


    public AdminHelper() {

    }

    public ArrayList<Labels> getExistingLabels() {
        ArrayList<Labels> dbLabels= new ArrayList<>();
        return dbLabels;
    }


}


