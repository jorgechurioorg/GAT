package com.leofanti.gat.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


@IgnoreExtraProperties

public class ProductoTerminadoOut {

    /**
     * Registro de salida de producto terminado
     *
     * @author JCH
     *
     */
    public String descripcion;
    public String descripcionLong;
    public String ean;
    public int cantidad;
    public String ptKey;
    public String lote;
    public String timestamp;

    public boolean activo;

    //TODO canal: hace un join de remito y cliente (estado=CLOSED)
    //y lo muestra en recycler view
    //cuando se despacha y se cobra cambia le estado del remito/cliente a SHIPPED
    //agrega el monto cobrado, la foto del remito y el monto a cobrar SHIPPEDOPEN
    //puede incluir alguna observacion

    @Exclude
    private String thisKey;

    public ProductoTerminadoOut () {

    }

    //constructor
    public ProductoTerminadoOut(String descripcion, String prodKey, String loteInt,int cantidad) {
        this.descripcion = descripcion;
        this.ptKey = prodKey;
        this.lote = loteInt;
        this.cantidad = cantidad;
        DateFormat timestampFormat = new SimpleDateFormat("yyyyMMdd-hh:mm:ss");
        Date today = new Date();
        this.timestamp = timestampFormat.format(today);
        this.ean = "0000000000";
        this.activo = true;
    }


    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion( String descripcion) { this.descripcion = descripcion;}

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad( int cantidad ) { this.cantidad = cantidad ; }

    public void setPtKey( String key){
        this.ptKey = key;
    }

    public String getEan() {
        return ean;
    }

    public String getPtKey() {
        return ptKey;
    }

    public String getLote() {return lote;}

    public void setLote( String lote){ this.lote=lote;}

    public String getTimestamp() {return timestamp;}

    @Exclude
    public void addCantidad( int cantidad){
        this.cantidad += cantidad;
    }
}