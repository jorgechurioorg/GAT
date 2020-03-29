package com.leofanti.gat.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class SeList {
    /**
     * SeList object
     * guarda cada Se donde
     * rece define la receta por unidad
     * dosis define la cantidad de unidades
     * rece MP,cantF
     * MP es la misma que se define en la tabla MP
     */
    private String descripcion;
    private HashMap<String,Float> dosis;
    private HashMap<String,Float> rece;
    private String type;

    @Exclude
    private String thisKey;



    public SeList() {
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

    public HashMap<String,Float> getDosis() {
        return dosis;
    }

    public void setDosis(HashMap<String,Float> dosis) {
        this.dosis = dosis;
    }
}
