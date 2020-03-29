package com.leofanti.gat.model;

public class MateriaPrima {

    /**
     * Objeto de materia prima (definicion de cada producto)
     *
     * @author JCH
     *
     */
    private String descripcion;
    private String EAN;
    private Long aptitud;
    private String unidad;
    private String ccosto;
    private boolean activo = true;
    //TODO nutricional

    //constructor
    public void MateriaPrima() {
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Long getAptitud() {
        return aptitud;
    }

    public String getEAN() {
        return EAN;
    }

    public String getUnidad() {
        return unidad;
    }

    public String getCcosto() {return  ccosto; }
}