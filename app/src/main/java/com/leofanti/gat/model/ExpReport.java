package com.leofanti.gat.model;

import com.google.firebase.database.Exclude;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpReport {

    private String col1;
    private String col2;
    private String col3;
    private Float  col4;

    public ExpReport() {
    }

   public void setReg( String col1, String col2, String col3, Float col4){
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
        this.col4 = col4;
   }


   public String getCol1() {return this.col1;}

   public String getCol2() {return this.col2;}

   public String getCol3() {return this.col3;}

   public Float getCol4() {return this.col4;}

   public void setCol4( Float f){ this.col4=f; }
}
