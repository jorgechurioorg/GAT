package com.leofanti.gat.model;

public class StrFourColReport {

    private String col1;
    private String col2;
    private String col3;
    private String  col4;

    public StrFourColReport() {
    }

   public void setReg( String col1, String col2, String col3, String col4){
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
        this.col4 = col4;
   }


   public String getCol1() {return this.col1;}

   public String getCol2() {return this.col2;}

   public String getCol3() {return this.col3;}

   public String getCol4() {return this.col4;}

}
