package com.leofanti.gat.model;

public class Cliente {

    /**
     * Objeto cliente
     *
     * @author JCH
     *
     */
    private String name;
    private String address;
    private String localidad;
    private String movil;
    private boolean activo = true;

    //constructor
    public Cliente() {
    }

    public Cliente(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public boolean getActivo() {return activo;}

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setMovil(String movil) {
        this.movil = movil;
    }

    public String getMovil() {
        return movil;
    }
}