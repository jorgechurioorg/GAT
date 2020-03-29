package com.leofanti.gat.utils;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.leofanti.gat.model.CollectRegistro;
import com.leofanti.gat.model.Const;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

//Funciones de collections, canal y ventas
public class SalesHelper {

    public SalesHelper( ) {
    }

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbCollections = database.getReference("sales/main/collections");

    public void saveCollection(CollectRegistro collectRegistro){
        SalesTon salesTon = SalesTon.getInstance();
        salesTon.saveCollection(collectRegistro);

        //TODO guardar el collectionsBoard

    }

    public void updateCollectionsBoard( final DbGetDataListener<Map<String,Float>> listener){

        //Funcion: analiza toda la base de collections hasta el mes pasado y lo consolida en un HasMap
        listener.onStart();
        DatesHelper datesHelper = new DatesHelper();
        final Map<String,Float> collectionsBoard = new HashMap<>();

        collectionsBoard.put( Const.INV_TDY, 0.0f);
        collectionsBoard.put( Const.INV_MTD, 0.0f);
        collectionsBoard.put( Const.INV_WTD, 0.0f);

        collectionsBoard.put( Const.CSH_TDY, 0.0f);
        collectionsBoard.put( Const.CSH_WTD, 0.0f);
        collectionsBoard.put( Const.CSH_MTD, 0.0f);

        collectionsBoard.put( Const.CHK_TDY, 0.0f);
        collectionsBoard.put( Const.CHK_WTD, 0.0f);
        collectionsBoard.put( Const.CHK_MTD, 0.0f);

        collectionsBoard.put( Const.DUE_TDY, 0.0f);
        collectionsBoard.put( Const.DUE_WTD, 0.0f);
        collectionsBoard.put( Const.DUE_MTD, 0.0f);

        final String hoyEsHoy = datesHelper.getHoyEsHoy();
        final String[] wtd = datesHelper.wtd(hoyEsHoy);
        final String[] mtd = datesHelper.mtd(hoyEsHoy);
        //TODO guardar el board en sharedoptions y ver como recalcular cada tanto
        Query query  = dbCollections.orderByChild("timestamp").startAt(mtd[0]).endAt(hoyEsHoy);
        query.addListenerForSingleValueEvent  (new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot collRec : dataSnapshot.getChildren()) {
                    CollectRegistro cll = new CollectRegistro();
                    cll = collRec.getValue(CollectRegistro.class);
                    String timeStamp = cll.getTimestamp();
                    if( timeStamp.compareTo(mtd[0])>=0) {
                        //TODO aca deberia estar todo hasta que se implemente LMONTH
                        collectionsBoard.put(Const.INV_MTD,
                                collectionsBoard.containsKey(Const.INV_MTD) ?
                                        collectionsBoard.get(Const.INV_MTD) + cll.getMontoFact() : cll.getMontoFact());
                        collectionsBoard.put(Const.CSH_MTD,
                                collectionsBoard.containsKey(Const.CSH_MTD) ?
                                        collectionsBoard.get(Const.CSH_MTD) + cll.getPago() : cll.getPago());
                        collectionsBoard.put(Const.CHK_MTD,
                                collectionsBoard.containsKey(Const.CHK_MTD) ?
                                        collectionsBoard.get(Const.CHK_MTD) + cll.getCheque() : cll.getCheque());


                        if (timeStamp.compareTo(wtd[0]) >= 0) {
                            collectionsBoard.put(Const.INV_WTD,
                                    collectionsBoard.containsKey(Const.INV_WTD) ?
                                            collectionsBoard.get(Const.INV_WTD) + cll.getMontoFact() : cll.getMontoFact());
                            collectionsBoard.put(Const.CSH_WTD,
                                    collectionsBoard.containsKey(Const.CSH_WTD) ?
                                            collectionsBoard.get(Const.CSH_WTD) + cll.getPago() : cll.getPago());
                            collectionsBoard.put(Const.CHK_WTD,
                                    collectionsBoard.containsKey(Const.CHK_WTD) ?
                                            collectionsBoard.get(Const.CHK_WTD) + cll.getCheque() : cll.getCheque());

                            if (timeStamp.startsWith(hoyEsHoy)) {
                                collectionsBoard.put(Const.INV_TDY,
                                        collectionsBoard.containsKey(Const.INV_TDY) ?
                                                collectionsBoard.get(Const.INV_TDY) + cll.getMontoFact() : cll.getMontoFact());
                                collectionsBoard.put(Const.CSH_TDY,
                                        collectionsBoard.containsKey(Const.CSH_TDY) ?
                                                collectionsBoard.get(Const.CSH_TDY) + cll.getPago() : cll.getPago());
                                collectionsBoard.put(Const.CHK_TDY,
                                        collectionsBoard.containsKey(Const.CHK_TDY) ?
                                                collectionsBoard.get(Const.CHK_TDY) + cll.getCheque() : cll.getCheque());

                            }
                        }
                    }
                }

                collectionsBoard.put(Const.DUE_TDY,
                        collectionsBoard.get(Const.CSH_TDY)
                                + collectionsBoard.get(Const.CHK_TDY)
                                - collectionsBoard.get(Const.INV_TDY));
                collectionsBoard.put(Const.DUE_WTD, collectionsBoard.get(Const.CSH_WTD) + collectionsBoard.get(Const.CHK_WTD) - collectionsBoard.get(Const.INV_WTD));
                collectionsBoard.put(Const.DUE_MTD, collectionsBoard.get(Const.CSH_MTD) + collectionsBoard.get(Const.CHK_MTD) - collectionsBoard.get(Const.INV_MTD));
                //TODO guardar en sharedOptions
                listener.onSuccess(collectionsBoard);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }

        });
    }
}


