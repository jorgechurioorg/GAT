package com.leofanti.gat.utils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.leofanti.gat.GatTon;
import com.leofanti.gat.model.ClienteOut;
import com.leofanti.gat.model.Const;
import com.leofanti.gat.model.ExpRegistro;
import com.leofanti.gat.model.ExpReport;


import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;

import java.util.Date;
import java.util.HashMap;

//Esta clase se encarga de manipular la informaicon de firebase y crear reportes segun diferentes criterios
public class ExpenseHelper {

    private static final int TODAY = 0;
    private static final int WTD = 1;
    private static final int MTD = 2;
    private static final int LMONTH = 3;
    private static  final int SIXTYD = 4 ;
    private static final int ITEM = 0;
    private static final int PROV = 1;
    private static final int RUBRO = 2;
    public static final int EXPGROUP = 3;
    private int dateMode ;
    private int groupMode ;


    private ArrayList<ExpRegistro> expRegistro = new ArrayList<>();
    private ArrayList<ExpReport> expenseReport = new ArrayList<>();
    public static GatTon gatTon = GatTon.getInstance();
    private static String TAG = "JCHEXPHELPER";
    private String fD, fH;

    private HashMap<String,String> grouping = new HashMap<>();


    public ExpenseHelper() {

    }

    //normalizacion de gastos por grupos


    public void normalizeExpenses(){
        gatTon.readGroupingDb(new DbGetDataListener<HashMap<String,String>>() {
            @Override
            public void onStart() {

            }
            @Override
            public void onSuccess(final HashMap<String,String> grupos) {
                for (ExpRegistro expR : gatTon.getExpAuditList()) {
                    String group = expR.getGrupo();
                    if (group == null) {
                        String ccosto = expR.getcCosto().toLowerCase();
                        String grupo;
                        if(grupos.containsKey(ccosto)) {
                            grupo = grupos.get(ccosto);
                        } else {
                            Log.d(TAG, "Grouping no encontrado, ccosto :" + ccosto);
                            grupo= "undef";
                        }
                        expR.setGrupo(grupo);
                        gatTon.updateExpense(expR, "grupo", grupo);
                    }
                }
            }
            @Override
            public void onFailed(DatabaseError databaseError) {
                //DO SOME THING WHEN GET DATA FAILED HERE
            }
        });
    }


    //Esta funcion solo recupera la lista de datos de gastos en un rango de fechas determinados
    public void setExpenseList(Context context, int dateMode) {

        String hoyEsHoy;
        DateFormat timestampFormat = new SimpleDateFormat("yyyyMMdd");
        Date today = new Date();
        hoyEsHoy = timestampFormat.format(today);
        DatesHelper dh = new DatesHelper();
        String dt[] = {hoyEsHoy, hoyEsHoy};
        switch (dateMode){
            case Const.TODAY:
                break;
            case Const.SIXTYD:
                dt = dh.dateUntil(fH, -60);
                break;
            case Const.MTD:
                dt = dh.dateMonthToDate( hoyEsHoy );
                break;
            case Const.WTD:
                dt = dh.wtd(hoyEsHoy);
                break;
            case Const.LMONTH:
                dt = dh.pastMonth(hoyEsHoy);
                break;

        }
        fD= dt[0] ;
        fH= dt[1] ;

        expRegistro.clear();
        for( ExpRegistro expR: gatTon.getExpAuditList() ) {
            ExpRegistro xpr = new ExpRegistro();
            String tStamp = expR.getTimestamp().substring(0,8);
            int hasta = tStamp.compareTo(fH);
            int desde = tStamp.compareTo(fD);
            if( desde >= 0 && hasta <=0 ) {
                expRegistro.add(expR);
            }
        }

    }


    public ArrayList<ExpReport> getExpenseList() {
        return expenseReport;
    }

    public void setExpenseReport(){
        HashMap<String, Float> grupoCcosto = new HashMap<>();
        expenseReport.clear();
        montoTotal = 0.0f ;
        for( ExpRegistro expR: expRegistro){
            montoTotal += expR.getMonto();
            ExpReport expRep = new ExpReport();
            switch (this.groupMode) {
                case Const.ITEM:
                    expRep.setReg(expR.getItemName(), expR.getTimestampSlashed(), expR.getProveedor(), expR.getMonto());
                    expenseReport.add(expRep);
                    break;
                case Const.PROV:
                   boolean createEntry = true;
                   /*for( ExpReport record: expenseReport) {
                       if (record.getCol1().equalsIgnoreCase(expR.getProveedor())) {
                           int ix = expenseReport.indexOf(record);
                           float subt = record.getCol4() + expR.getMonto();
                           expRep.setReg(expR.getProveedor(), "", "", subt);
                           expenseReport.set(ix, expRep);
                           createEntry = false;
                           break;
                       }
                   }*/
                   if( createEntry) {
                       expRep.setReg(expR.getProveedor(), expR.getTimestampSlashed(), expR.getItemName(), expR.getMonto());
                       expenseReport.add(expRep);
                   }
                   break;

                case Const.RUBRO:

                    createEntry = true;
                    String grupo = expR.getGrupo();
                    String rubro = expR.getcCosto();
                    String prove = expR.getProveedor();
                    ExpReport expRecord = new ExpReport();
                    expRecord.setReg(grupo,rubro,prove,expR.getMonto());
                    expenseReport.add(expRecord);
                    float pre;
                    if( grupoCcosto.containsKey(grupo)){
                        float val = expR.getMonto();
                        pre = grupoCcosto.get(grupo) + val;
                    } else {
                        pre = expR.getMonto();
                    }
                    grupoCcosto.put(grupo, pre);

                    if(rubro==null) rubro = "n/d";
                    for( ExpReport record: expenseReport){
                        if( record.getCol2().equalsIgnoreCase(rubro) && record.getCol3().equals("__")){
                            int ix = expenseReport.indexOf(record);
                            float subt = record.getCol4() + expR.getMonto();
                            expRep.setReg(grupo, rubro, "__", subt);
                            expenseReport.set(ix, expRep) ;
                            createEntry = false;
                            break;
                        }
                    }
                    if( createEntry) {
                        expRep.setReg(grupo, rubro, "__", expR.getMonto());
                        expenseReport.add(expRep);
                    }

                    break;
                case Const.EXPGROUP:
                    /*FILA GRUPO * ccosto  -- --    TOTAL*/
                    /*columna 1: item columna 2 proveedo  col 3 fecha  col4 monto*/


            }

        }
        if(this.groupMode==Const.RUBRO) {
            for( HashMap.Entry<String,Float> entry: grupoCcosto.entrySet()){
                ExpReport expRep = new ExpReport();
                expRep.setReg(entry.getKey(),"__","__",entry.getValue());
                expenseReport.add(expRep);
            }
        }
        Collections.sort(expenseReport,new ReportChainedComparator(
                new ComparatorCol1(),
                new ComparatorCol2(),
                new ComparatorCol3()));

        }

    private Float montoTotal = 0.0f;

    public Float getMontoTotal(){return montoTotal; }

    public String getMontoTotalAsString() {
        return NumberFormat.getInstance().format(this.montoTotal);

    }

    public String getDateFrom() {
        return fD;
    }

    public String getDateTo() {
        return fH;
    }

    public void setDateMode(int mode){
        this.dateMode = mode ;
    }

    public int getDateMode() {return dateMode;}

    public void setGroupMode( int mode){
        this.groupMode = mode;
    }

    public int getGroupMode() { return groupMode;}

    public ArrayList<ExpReport> groupBy( int dateMode, int groupMode){
        return expenseReport;
    }


}


