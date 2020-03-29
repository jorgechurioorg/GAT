package com.leofanti.gat.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

public class Rece {
    /**
     * SeList object
     * guarda cada Se donde
     * rece define la receta por unidad
     * dosis define la cantidad de unidades
     * rece MP,cantF
     * MP es la misma que se define en la tabla MP
     */
    private String descripcion;
    private String label;
    private String timestamp;
    private Float xunit; //peso por unidad de produccion en Kg
    private HashMap<String,Float> rece; //receta normalizada base 1.0
    private HashMap<String,Float> loteProd;
    private String receType;  //premix, masa, relleno
    private String alergenType; //vegan, lactea, carne
    private Boolean visible;

    @Exclude
    private String thisKey;

    public Rece() {
    }

    public Float getXunit() {
        return xunit;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }


    public HashMap<String, Float> getLoteProd() {
        return loteProd;
    }

    public void setXunit(Float xunit) {
        this.xunit = xunit;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getReceType() {
        return receType;
    }

    public String getAlergenType() {
        return alergenType;
    }

    public String getLabel() {
        return label;
    }

    public HashMap<String,Float> getRece() {
        return rece;
    }

    public void setRece(HashMap<String,Float> rece) {
        this.rece = rece;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setThisKey(String regKey ) {this.thisKey = regKey; }
    public String getThisKey() {return this.thisKey;}

}
