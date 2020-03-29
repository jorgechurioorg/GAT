package com.leofanti.gat.model;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ExpRegistro {

    public String itemName;
    public String itemKey;
    public String timestamp;
    public Float  monto;
    public boolean factura;
    public boolean visible;
    public String cCosto;
    public String proveedor;
    public String operador;
    public String thumbUrl;
    private String grupo;

    @Exclude
    private String thisKey;

    public ExpRegistro() {
        DateFormat timestampFormat = new SimpleDateFormat("yyyyMMdd-hh:mm:ss");
        Date today = new Date();
        this.timestamp = timestampFormat.format(today);
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() { return this.itemName; }

    public void setItemKey( String itemKey){
        this.itemKey = itemKey;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getOperador() {
        return operador;
    }

    public void setMonto(float monto, boolean fact) {
        this.monto = monto;
        this.factura = fact;
    }
    public float getMonto(){
        if( monto == null) monto = 0.0F;
        return monto;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp( String timestamp) {
        this.timestamp = timestamp + "-00:00:01";
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public boolean getVisible() {return this.visible; }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public boolean getFactura(){return this.factura;}

    public void setcCosto(String cCosto) {
        this.cCosto = cCosto;
    }

    public String getcCosto() { return cCosto; }

    public void setThisKey(String regKey ) {this.thisKey = regKey; }

    public String getThisKey() {return this.thisKey;}

    @Exclude
    public String getMontoAsString(){
        if( this.monto==null) {
            return "-" ;
        } else {
            return "$ " + monto.toString();
        }
    }

    @Exclude
    public String getTimestampSlashed(){
        String loteSlashed = timestamp.substring(6, 8) + "/" + timestamp.substring(4, 6);
        return loteSlashed;
    }
}
