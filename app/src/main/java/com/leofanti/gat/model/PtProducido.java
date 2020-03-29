package com.leofanti.gat.model;

import com.google.firebase.database.Exclude;

public class PtProducido {

    public PtProducido(){}

    private String ptName;
    private Long qty=0L;
    @Exclude
    private Integer thisKey;

    public void setPtName( String ptName) { this.ptName = ptName; }

    public String getPtName() {
        return ptName;
    }

    public void setQty(Integer qty) {
        this.qty = qty.longValue();
    }

    public void setQty(Long qty){
        this.qty = qty;
    }

    public Long getQty() {
        return this.qty;
    }


    @Exclude
    public String getQtyAsString() {
        return this.qty.toString();
    }
    @Exclude
    public void setThisKey( Integer thisKey) { this.thisKey=thisKey;}
    @Exclude
    public Integer getThisKey() { return this.thisKey;}
}
