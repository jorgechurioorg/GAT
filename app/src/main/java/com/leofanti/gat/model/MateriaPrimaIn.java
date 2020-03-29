package com.leofanti.gat.model;


import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@IgnoreExtraProperties

public class MateriaPrimaIn {

    public String nombreMp;
    public String unidadMp;
    public String thumbUrl;
    public String proveedor;
    public String loteFabricante;
    public String fechaElab;
    public String fechaVenc;
    public String loteInterno;
    public String timestamp;
    public String keyMp;
    public float cantIngresada;
    public float cantRechazada;
    public boolean visible;
    public String operador;

    @Exclude
    private String key;


    //constructor
    public MateriaPrimaIn() {
        this.thumbUrl = "no_image";
        DateFormat loteInternoFormat = new SimpleDateFormat("dd/MM");
        DateFormat timestampFormat = new SimpleDateFormat("yyMMdd-hh:mm:ss");
        Date today = new Date();
        this.loteInterno = loteInternoFormat.format(today);
        this.timestamp = timestampFormat.format(today);
        this.visible = true;

    }

    public MateriaPrimaIn(String nombre, String unidad, String proveedor, String loteFabricante, float cantIngresada, float cantRechazada, String oper) {
        this.nombreMp = nombre;
        this.unidadMp = unidad;
        this.proveedor = proveedor;
        this.loteFabricante = loteFabricante;
        this.cantIngresada = cantIngresada;
        this.cantRechazada = cantRechazada;
        this.thumbUrl = "no_image";
        DateFormat loteInternoFormat = new SimpleDateFormat("dd/MM");
        DateFormat timestampFormat = new SimpleDateFormat("yyyyMMdd-hh:mm:ss");
        Date today = new Date();
        this.loteInterno = loteInternoFormat.format(today);
        this.timestamp = timestampFormat.format(today);
        this.visible = true;
        this.operador = oper;
    }


    public void setLoteFabricante(String loteFabricante){this.loteFabricante= loteFabricante;}

    public String getNombreMp() {
        return this.nombreMp;
    }

    public void setNombreMp( String nombreMp) { this.nombreMp = nombreMp; }

    public void setProveedor(String proveedor){
        this.proveedor = proveedor;
    }

    public void setThumbUrl(String url) {
        this.thumbUrl = url;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getThumbUrl() {
        if (thumbUrl == null)
            this.thumbUrl = "no_image";
        return this.thumbUrl;
    }
    public String getProveedor() {
        return this.proveedor;
    }

    public String getLoteInterno() {
        return this.loteInterno;
    }

    public String getTimestamp() {
        return this.timestamp;
    }
    @Exclude
    public String getTimestampChopped() {
        if( this.timestamp.startsWith("19")){
            String sanityTs = "20";
            sanityTs += this.timestamp.substring(0,6);
            return sanityTs;
        } else {
            return this.timestamp.substring(0,8);
        }
    }

    public void setTimestamp() {
        DateFormat loteInternoFormat = new SimpleDateFormat("dd/MM");
        DateFormat timestampFormat = new SimpleDateFormat("yyyyMMdd-hh:mm:ss");
        Date today = new Date();
        this.loteInterno = loteInternoFormat.format(today);
        this.timestamp = timestampFormat.format(today);
    }

    public boolean getVisible() {return this.visible; }

    public void setVisible(boolean visible) { this.visible=visible; }

    @Exclude
    public void setKey( String key) {
        this.key = key;
    }
    @Exclude
    public String getKey() {
        return this.key;
    }
    @Exclude
    public String getCantOk() {
        return String.valueOf(this.cantIngresada) + " " + this.unidadMp;
    }
    @Exclude
    public String getCantRech() {
        return String.valueOf(this.cantRechazada) + " " + this.unidadMp;
    }

    public void clear(){
        this.nombreMp = null;
        this.unidadMp = null;
        this.proveedor = null;
        this.loteFabricante = null;
        this.cantIngresada = 0f;
        this.cantRechazada = 0f;
        this.thumbUrl = "no_image";
        this.loteInterno = null;
        this.timestamp = null;
        this.visible = true;
        this.operador = null;
    }
}

