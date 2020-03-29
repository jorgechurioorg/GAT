package com.leofanti.gat.utils;

import android.content.Context;

import com.leofanti.gat.GatTon;
import com.leofanti.gat.model.ExpRegistro;
import com.leofanti.gat.model.ExpReport;
import com.leofanti.gat.model.ProductoTerminadoOut;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class CanalHelper {

    private static final int TODAY = 0;
    private static final int WTD = 1;
    private static final int MTD = 2;
    private static final int LMONTH = 3;
    private static  final int SIXTYD = 4 ;
    private static final int ITEM = 0;
    private static final int PROV = 1;
    private static final int RUBRO = 2;


    private ArrayList<ProductoTerminadoOut> remitos = new ArrayList<>();
    public static GatTon gatTon = GatTon.getInstance();
    private static String TAG = "JCHCANALHELPER";


    public CanalHelper() {

    }

    public void setRemitoList(Context context, int dateMode) {
        remitos.clear();


    }

    public ArrayList<ProductoTerminadoOut> getRemitoList() {
        return remitos;
    }





}


