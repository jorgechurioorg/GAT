package com.leofanti.gat.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

public class MfgItem {

    private String descripcion;
    private String receKey;
    private boolean visible = true;
    private String operador;
    private Float cantidad;
    private String loteLabel;
    private HashMap<String,Float> receta = new HashMap<>();
    private HashMap<String,String> timestamp = new HashMap<>();
    private String status;
    private String productType;
    private String alergenType;

    @Exclude
    private String thisKey;



    public MfgItem() {
    }


    public void setLoteLabel(String loteLabel) {
        this.loteLabel = loteLabel;
    }

    public String getLoteLabel() {
        return loteLabel;
    }

    public String getReceKey() {
        return receKey;
    }

    public void setReceKey(String receKey) {
        this.receKey = receKey;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public HashMap<String,Float> getReceta(){
        return receta;
    }

    public void setReceta(HashMap<String, Float> receta) {
        this.receta = receta;
    }

    public void setTimestamp(String stage, String timestampOp) {
        timestamp.put(stage,timestampOp);
    }

    public String getTimestamp(String stage) {
        if( timestamp.containsKey(stage)) {
            return timestamp.get(stage);
        } else {
            return "n/e";
        }
    }

    public String getAlergenType() {
        return alergenType;
    }

    public void setAlergenType(String alergenType) {
        this.alergenType = alergenType;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getOperador() {
        return operador;
    }


    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public boolean getVisible() {return this.visible; }

    public void setThisKey(String regKey ) {this.thisKey = regKey; }
    public String getThisKey() {return this.thisKey;}


}
