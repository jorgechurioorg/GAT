package com.leofanti.gat.model;


import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;


public class MpLotSheet {

    private String TAG = "JCHPMPLOTSHEET";

    public String mpName;
    public HashMap<String,String> lotesL = new HashMap<>();

    public MpLotSheet() {

    }

    public void setMpName(String mpName) {
        this.mpName = mpName;
    }

    public String getMpName() {

        return this.mpName;
    }

    public void setLotes(HashMap<String,String> lotes) {
        lotes.remove(null);
        TreeMap<String, String> sorted = new TreeMap<>(lotes);
        this.lotesL.putAll(sorted);
    }

    public String[] getLotes() {
        String lotesActivos[] = new String[3];
        int pos = lotesL.size()-1;
        ArrayList<String> lotis = new ArrayList<String>(lotesL.keySet());
        lotesActivos[0] = lotis.get(pos);
        lotesActivos[1] = (pos-2>0)? lotis.get(pos-2) : null ;
        return lotesActivos;
    }


    @Exclude
    public String getLotSlashed(int ix){
        try {
            ArrayList<String> lotis = new ArrayList<String>(lotesL.keySet());
            if( ix<lotis.size()) {
                String lote = lotis.get(ix);
                String loteSlashed = lote.substring(4, 6) + "/" + lote.substring(2, 4);
                return loteSlashed;
            } else {
                return null ;
            }
        }
        catch(ArrayIndexOutOfBoundsException exception) {
            Log.e(TAG, "out of bound ix:" + ix );
            return null;
        }

    }

}

