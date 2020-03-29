package com.leofanti.gat.utils;


import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.DefaultItemAnimator;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;
import com.leofanti.gat.GatTon;
import com.leofanti.gat.adapters.PtProdRecyclerView;
import com.leofanti.gat.model.ExpRegistro;
import com.leofanti.gat.model.ExpReport;
import com.leofanti.gat.model.MateriaPrimaIn;
import com.leofanti.gat.model.PtProducido;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

//Esta clase se encarga de manipular la informaicon de firebase y crear reportes segun diferentes criterios
public class PtProdHelper {


    public PtProdHelper() {

    }

    private static final int TODAY = 0;
    private static final int WTD = 1;
    private static final int MTD = 2;
    private static final int LMONTH = 3;
    private static  final int SIXTYD = 4 ;
    private int dateMode=WTD ;
    private String hoyEsHoy = null;
    private ArrayList<PtProducido> listaProducida = new ArrayList<>();
    private GatTon gatTon = GatTon.getInstance();
    private static String TAG = "JCHHOJAPHELPER";
    private String fD, fH;

    // la matriz de producdio qyeda aca
    Map<String, Map<String, Long>> matrix= new HashMap<>();
    final ArrayList<PtProducido> lista = new ArrayList<>();

    //armar aca la consulat del array de producido

    public void setMatrix( Map<String, Map<String, Long>> matrix ){
        this.matrix = matrix;
    }

    private Long grandTotal = 0L;

    public ArrayList<PtProducido> getHojaProdReport() {

        lista.clear();
        Map<String, Long> grouper = new HashMap<>();
        Map<String, Long> subTotal = new HashMap<>();
        grandTotal = 0L;

        for (Map.Entry<String,Map<String,Long>> entry : matrix.entrySet()) {
            Map<String, Long> value = entry.getValue();
            String key = entry.getKey();

            int hasta = key.compareTo(fH);
            int desde = key.compareTo(fD);
            if (desde >= 0 && hasta <= 0) {
                for (Map.Entry<String, Long> pt : value.entrySet()) {
                    grandTotal += pt.getValue();
                    //TODO subtotalizar por tipo de producto
                    if (grouper.containsKey(pt.getKey())) {
                        Long qty = grouper.get(pt.getKey()) + pt.getValue();
                        grouper.put(pt.getKey(), qty);
                    } else {
                        grouper.put(pt.getKey(), pt.getValue());
                    }
                }
            }
        }

        Map<String,Long> treeMap = new TreeMap<String,Long>(grouper);
        for( Map.Entry<String, Long> total: treeMap.entrySet()) {
            PtProducido report = new PtProducido();
            report.setPtName(total.getKey());
            report.setQty(total.getValue());
            lista.add(report);
        }
        return lista;
    }

    public String getGrandTotal() {
        return grandTotal.toString();
    }

    public void setHoyEsHoy(String hoyEsHoy) {
        this.hoyEsHoy = hoyEsHoy;

    }

    public void setDateMode( int mode ) {

        this.dateMode = mode ;

        if( hoyEsHoy==null) {
            DateFormat timestampFormat = new SimpleDateFormat("yyyyMMdd");
            Date today = new Date();
            this.hoyEsHoy = timestampFormat.format(today);
        }

        DatesHelper dh = new DatesHelper();
        String dt[] = {hoyEsHoy, hoyEsHoy};
        switch (dateMode){

            case TODAY:
                break;
            case SIXTYD:
                dt = dh.dateUntil(fH, -60);
                break;
            case MTD:
                dt = dh.dateMonthToDate( hoyEsHoy );
                break;
            case WTD:
                dt = dh.wtd(hoyEsHoy);
                break;
            case LMONTH:
                dt = dh.pastMonth(hoyEsHoy);
                break;

        }
        fD= dt[0] ;
        fH= dt[1] ;
    }


    public String getfD() { return this.fD;}

    public String getfH() {return  this.fH;}



    public int getDateMode() {return dateMode;}

    public String getHoyEsHoy() {
        return hoyEsHoy;
    }


    public String getHoyEsHoyHumanReadable() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdfH = new SimpleDateFormat( "EEE, dd MMMM yyyy", new Locale("es_AR", "AR"));
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(this.hoyEsHoy));
        } catch (ParseException e) {
            e.printStackTrace();
            return this.hoyEsHoy;
        }
        return sdfH.format(c.getTime());
    }


}

