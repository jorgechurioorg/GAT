package com.leofanti.gat.model;

import com.google.firebase.database.Exclude;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClienteOut {

    public String name;
    public String clienteKey;
    private String remitoKey;
    public String address;
    public String timestamp;
    public String status;
    public String operador ;
    public String despachado;
    public String ruta;
    public String dispatchOperador;
    public String filed;
    public String filedOperador;
    private String shipImageUrl;
    private String shipTimestamp;
    private Float shipCobrado;
    private Float shipPorCobrar;
    private String shipNote;

    public String getShipNote() {
        return shipNote;
    }

    public void setShipNote(String shipNote) {
        this.shipNote = shipNote;
    }

    public Float getShipCobrado() {
        return shipCobrado;
    }

    public Float getShipPorCobrar() {
        return shipPorCobrar;
    }

    public void setShipImageUrl(String shipImageUrl) {
        this.shipImageUrl = shipImageUrl;
    }

    public String getShipImageUrl() { return this.shipImageUrl;}

    public ClienteOut() {

    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getRuta() {
        return this.ruta;
    }

    public void setName(String name) { this.name= name; }

    public String getName() {
        return name;
    }

    public String getDespachado() {
        return despachado;
    }

    public void setDespachado( String dateOut){
        this.despachado = dateOut;
    }

    public void setFiled(String filed) {
        this.filed = filed;
    }

    public void setFiledOperador(String filedOperador){
        this.filedOperador = filedOperador;
    }

    public String getFiled() {
        return filed;
    }

    public String getStatus() {
        return status;
    }

    @Exclude
    public String getRemitoKey() {
        return remitoKey;
    }
    @Exclude
    public void setRemitoKey( String remitoKey){
        this.remitoKey = remitoKey;
    }

    public void setAddress( String address) {this.address=address;}
    public String getAddress() {return this.address; }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp( String timestamp) {
        this.timestamp = timestamp;
    }

    public void setOperador (String operador) { this.operador = operador; }

    public String getOperador () {return this.operador; }

    public void setDispatchOperador (String dispatchOperador) {this.dispatchOperador=dispatchOperador;}

    public void setStatus(String status) {
        this.status = status;
    }
}