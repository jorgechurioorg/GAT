package com.leofanti.gat.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.leofanti.gat.GatTon;
import com.leofanti.gat.model.Const;
import com.leofanti.gat.model.PtProducido;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//Funciones de fechas y calculos de rangos
public class DatesHelper {

    public DatesHelper() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date today = new Date();
        hoyEsHoy = sdf.format(today);
    }


    private int dateMode = Const.WTD;
    private String hoyEsHoy = null;
    private String fD, fH;

    public static String setTimeStampFromDMY ( boolean slashed, int dd, int mm, int yy){
        SimpleDateFormat sdf ;
        if( slashed)
            sdf = new SimpleDateFormat("dd/MM/yyyy");
        else
            sdf = new SimpleDateFormat("yyyyMMdd");
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.DATE, dd);
        c.set(Calendar.MONTH, mm);
        c.set(Calendar.YEAR, yy);
        String tstmp = sdf.format(c.getTime());
        return tstmp;
    }

    public static String humanToYYYYMMDD( String humanDate){
        String[] parsed = humanDate.split("/");
        String dateTst = parsed[2]+parsed[1]+parsed[0] ;
        return dateTst ;

    }

    public static class DatePickerFragment extends DialogFragment {

        private DatePickerDialog.OnDateSetListener listener;

        public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener) {
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.setListener(listener);
            return fragment;
        }

        public void setListener(DatePickerDialog.OnDateSetListener listener) {
            this.listener = listener;
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), listener, year, month, day);
        }
    }




    public String epochToHuman( Long epoch){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(epoch);
    }

    public String[] dateMonthToDate( String dateInMonth){
        String output = dateInMonth.substring(0,6) + "01";
        return new String[] {output, dateInMonth};
    }

    public String[] mtd( String today ){
        String output = today.substring(0,6) + "01";
        return new String[] {output, today};
    }

    public String[] pastMonth( String today){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        try {
            c.setTime(sdf.parse(today));
        } catch (ParseException e) {
            e.printStackTrace();
            return new String[] {today, today};
        }
        int dow = c.get(Calendar.DAY_OF_WEEK);
        int dia = c.get(Calendar.MONDAY);
        c.add(Calendar.MONTH, -1);
        int a = c.getActualMinimum(Calendar.DATE);
        int b = c.getActualMaximum(Calendar.DATE);
        c.set(Calendar.DAY_OF_MONTH, a);
        String desde = sdf.format(c.getTime());
        c.set(Calendar.DAY_OF_MONTH,b);
        String hasta = sdf.format(c.getTime());
        return new String[] {desde, hasta};
    }

    public String[] wtd( String today){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        try {
            c.setTime(sdf.parse(today));
        } catch (ParseException e) {
            e.printStackTrace();
            return new String[] {today, today};
        }
        int ddls = c.get(Calendar.DAY_OF_WEEK);
        if( ddls == 1 ) {
            c.add(Calendar.DAY_OF_MONTH, -6);
        } else {
            c.add(Calendar.DAY_OF_MONTH, -(ddls - 1));
        }
        String desde = sdf.format(c.getTime());
        return new String[] {desde, today};
    }

    public String[] dateUntil(String dateFrom, int days) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        try {
            c.setTime(sdf.parse(dateFrom));
        } catch (ParseException e) {
            e.printStackTrace();
            return new String[] {dateFrom, dateFrom};
        }
        c.add(Calendar.DATE, days);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
        String output = sdf1.format(c.getTime());
        return new String[] {dateFrom, output};
    }

    public String getHoyEsHoy() {
        return this.hoyEsHoy;
    }

    public String getTimestamp() {
        DateFormat timestampFormat = new SimpleDateFormat("yyyyMMdd-hh:mm:ss");
        Date today = new Date();
        return timestampFormat.format(today);
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

    public String getDateHumanReadable( String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdfH = new SimpleDateFormat( "EEE, dd MMMM yyyy", new Locale("es_AR", "AR"));
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
        return sdfH.format(c.getTime());}

        public String julianToDdMm( String julian){
            int dayOfYear = Integer.parseInt(julian);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
            return sdf.format(calendar.getTime());
        }
}
