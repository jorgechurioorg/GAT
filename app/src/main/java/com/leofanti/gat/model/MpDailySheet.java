package com.leofanti.gat.model;


import com.google.firebase.database.Exclude;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.leofanti.gat.model.MpLotSheet;

// Tiene que ser un singleton

public class MpDailySheet {

    @Exclude
    private String TAG = "JCHPMPDAILYSHEET";

    public String today;
    public ArrayList<MpLotSheet> mpLotSheet;

    public MpDailySheet() {

        DateFormat timestampFormat = new SimpleDateFormat("yyMMdd");
        Date today = new Date();
        this.today = timestampFormat.format(today);
        mpLotSheet = new ArrayList<>();
    }

    public String getToday() {
        return this.today;
    }

    public void setMpLotSheet( MpLotSheet mpLotSheet){
        this.mpLotSheet.add( mpLotSheet);
    }

    public ArrayList<MpLotSheet> getMpLotSheet () { return this.mpLotSheet; }

}

