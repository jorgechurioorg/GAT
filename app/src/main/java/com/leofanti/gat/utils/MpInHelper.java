package com.leofanti.gat.utils;


import com.google.firebase.database.DatabaseError;
import com.leofanti.gat.GatTon;
import com.leofanti.gat.model.MateriaPrimaIn;
import com.leofanti.gat.model.PtProducido;
import com.leofanti.gat.model.StrFourColReport;
import com.leofanti.gat.model.TraceLinkedList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

//se usa para lso reportes de auditoria de MPIN
public class MpInHelper {

    private GatTon gatTon = GatTon.getInstance();

    private static String TAG = "JCHMPINPHELPER";

    private ArrayList<TraceLinkedList> traceData = new ArrayList<>();
    private ArrayList<StrFourColReport> mpIn4ColReport = new ArrayList<>();


    public MpInHelper() {

    }

    public void setTraceData( ArrayList<TraceLinkedList> traceList) { this.traceData=traceList; }

    public ArrayList<TraceLinkedList> getTraceData() {return this.traceData; }

    public void arrangeReport() {
        mpIn4ColReport.clear();
        //TODO agrupar segun diferentes criterios de los chips ahora solo ordena alfabeticamente
        for (TraceLinkedList mprec : traceData) {
            //switch( modo de grouping)
            StrFourColReport singleLine = new StrFourColReport();
            singleLine.setReg(mprec.getDescripcion(), "", "", editLotFormat(mprec.getLote()));
            mpIn4ColReport.add(singleLine);
        }
        /*Collections.sort(mpIn4ColReport,new ReportChainedComparator(
                new ComparatorCol1(),
                new ComparatorCol2(),
                new ComparatorCol3()));

           }*/
    }
    public ArrayList<StrFourColReport> get4ColReport() {
        return mpIn4ColReport;
    }

    private String editLotFormat( String loteLong ) { return loteLong.substring(6,8)+"/"+loteLong.substring(4,6); }
}

