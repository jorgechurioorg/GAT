package com.leofanti.gat.model;

import com.google.firebase.database.Exclude;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CollectRegistro {

    private String timestamp;
    private Float montoFact;
    private Float  pago;
    private Float  debe;
    private Float  cheque;
    private Float  saldo;
    private String chequeDate;
    private boolean visible = true;
    private String cliente;
    private String operador;
    private String thumbUrl;
    private String notas;

    @Exclude
    private String thisKey;



    public CollectRegistro() {
    }

    public void setMontoFact(Float montoFact) {
        this.montoFact = montoFact;
    }

    public Float getMontoFact() {
        if( montoFact == null) {
            return 0.0f ;
        } else {
            return montoFact;
        }
    }

    public void setSaldo (Float saldo) {this.saldo = saldo; }
    public Float getSaldo() {return this.saldo;}

    public String getCliente() {
        return cliente;
    }
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }



    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getOperador() {
        return operador;
    }

    public void setPago(float pago) {
        this.pago= pago;
    }

    public float getPago(){
        if( pago== null) pago = 0.0F;
        return pago;
    }
    public void setDebe(float pago) {
        this.debe= pago;
    }
    public float getDebe(){
        if( debe== null) debe = 0.0F;
        return debe;
    }

    public void setCheque(float cheque) {
        this.cheque= cheque;
    }
    public float getCheque(){
        if( cheque== null) cheque = 0.0F;
        return cheque;
    }

    public void setNotas (String nota){
        this.notas = nota;
    }

    public String getNotas (){
        return this.notas;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp( String timestamp) {
        this.timestamp = timestamp;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public boolean getVisible() {return this.visible; }

    public String getThumbUrl() {
        return thumbUrl;
    }


    public void setThisKey(String regKey ) {this.thisKey = regKey; }

    public String getThisKey() {return this.thisKey;}

}
