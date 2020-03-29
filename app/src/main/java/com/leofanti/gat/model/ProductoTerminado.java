package com.leofanti.gat.model;

public class ProductoTerminado {

    /**
     * Objeto de prodcuto terminado (definicion)
     *
     * @author JCH
     *
     */

    //este objeto solo se puede cargar desde la base de datos

    private String descripcion;
    private String EAN;
    private String unidad;
    private boolean active;
    private String grupo;

    //constructor
    public void ProductoTerminado() {
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEAN() {
        return EAN;
    }

    public String getUnidad() {
        return unidad;
    }

    public String getGrupo() {return grupo; }
}