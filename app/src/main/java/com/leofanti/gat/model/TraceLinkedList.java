package com.leofanti.gat.model;

public class TraceLinkedList {

    /**
     * objeto generico para trazabilidad de cualquier elemenot forward y backward
     *
     * @author JCH
     *
     */
    private String descripcion;  //nombre del elemento segun las tablas de produccion
    private String lote;
    private String key;


    public String getDescripcion() {
        return descripcion;
    }

    public String getKey() {
        return key;
    }

    public String getLote() {
        return lote;
    }

    public void setItem( String desc, String lot, String key){
        this.descripcion = desc;
        this.lote = lot;
        this.key = key ;

    }
}